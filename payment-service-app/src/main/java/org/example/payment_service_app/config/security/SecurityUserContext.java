package org.example.payment_service_app.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SecurityUserContext implements UserContext {

    private final Authentication authentication;

    public SecurityUserContext() {
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
    }

    // Конструктор для тестирования
    public SecurityUserContext(Authentication authentication) {
        this.authentication = authentication;
    }

    @Override
    public String getUsername() {
        if (!isAuthenticated()) {
            return "anonymous";
        }

        final Object principal = authentication.getPrincipal();

        // Для JWT токенов
        if (principal instanceof Jwt jwt) {
            // Пробуем получить username из различных claims
            String username = jwt.getClaimAsString("preferred_username");
            if (username != null) {
                return username;
            }

            username = jwt.getClaimAsString("email");
            if (username != null) {
                return username;
            }

            return jwt.getSubject(); // sub claim
        }

        return Objects.requireNonNull(principal).toString();
    }

    @Override
    public String getRoles() {
        if (!isAuthenticated()) {
            return "ROLE_ANONYMOUS";
        }

        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(", "));
    }

    @Override
    public boolean hasRole(String role) {
        if (!isAuthenticated()) {
            return false;
        }

        // Нормализуем роль: добавляем префикс ROLE_ если его нет
        final String normalizedRole = normalizeRole(role);

        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(authority -> Objects.equals(authority, normalizedRole));
    }

    @Override
    public boolean hasPermission(String permission) {
        if (!isAuthenticated()) {
            return false;
        }

        // 1. Проверяем как роль (если permission без префикса)
        if (hasRole(permission)) {
            return true;
        }

        // 2. Проверяем прямое соответствие authority
        final boolean hasDirectPermission = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(authority -> Objects.equals(authority, permission));

        if (hasDirectPermission) {
            return true;
        }

        // 3. Проверяем scope из JWT токена
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            final String scope = jwt.getClaimAsString("scope");
            if (scope != null) {
                final List<String> scopes = List.of(scope.split(" "));
                return scopes.contains(permission);
            }
        }

        return false;
    }

    @Override
    public boolean isAuthenticated() {
        return authentication != null &&
            authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getPrincipal());
    }

    // Help methods

    /**
     * Нормализует название роли: добавляет префикс ROLE_ если его нет
     */
    private String normalizeRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return "";
        }

        role = role.trim();
        if (!role.startsWith("ROLE_")) {
            return "ROLE_" + role.toUpperCase();
        }
        return role;
    }

    /**
     * Получить ID пользователя из Keycloak JWT
     */
    public Optional<String> getUserId() {
        if (!isAuthenticated() || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return Optional.empty();
        }

        // В Keycloak subject (sub) содержит ID пользователя
        return Optional.ofNullable(jwt.getSubject());
    }

    public Optional<String> getEmail() {
        if (!isAuthenticated() || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return Optional.empty();
        }

        return Optional.ofNullable(jwt.getClaimAsString("email"));
    }

    public Optional<String> getFullName() {
        if (!isAuthenticated() || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return Optional.empty();
        }

        return Optional.ofNullable(jwt.getClaimAsString("name"));
    }

}
