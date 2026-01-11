package org.example.payment_service_app.config.security;

import lombok.extern.slf4j.Slf4j;
import org.example.payment_service_app.controller.Roles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Slf4j
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        final JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());

        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/payments/**").hasRole(Roles.ADMIN.name())
                .requestMatchers("/actuator/**").authenticated()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter))
            );

        return http.build();
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        log.info("Creating custom JwtDecoder with multiple issuers");

        final String jwkSetUri = "http://keycloak:8080/realms/pet-lms/protocol/openid-connect/certs";
        final NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        final OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
            new JwtTimestampValidator(),
            new MultiIssuerValidator()
        );

        jwtDecoder.setJwtValidator(validator);
        return jwtDecoder;
    }

    private static class MultiIssuerValidator implements OAuth2TokenValidator<Jwt> {

        private final List<String> validIssuers = List.of(
            "http://localhost:8085/realms/pet-lms",
            "http://keycloak:8080/realms/pet-lms",
            "http://host.docker.internal:8085/realms/pet-lms"
        );

        @Override
        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            final String issuer = jwt.getIssuer().toString();

            if (validIssuers.contains(issuer)) {
                return OAuth2TokenValidatorResult.success();
            }

            return OAuth2TokenValidatorResult.failure(
                new JwtValidationException(
                    "Invalid issuer: " + issuer,
                    List.of(new OAuth2Error(
                        "invalid_issuer",
                        "The issuer '" + issuer + "' is not valid",
                        null
                    ))
                ).getErrors()
            );
        }
    }
}
