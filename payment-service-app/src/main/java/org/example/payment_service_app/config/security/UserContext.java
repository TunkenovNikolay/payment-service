package org.example.payment_service_app.config.security;

import java.util.Optional;

/**
 * Current user context for retrieving authentication information
 */
public interface UserContext {

    /**
     * Get the username
     */
    String getUsername();

    /**
     * Get the user roles as a string
     */
    String getRoles();

    /**
     * Get the user ID (sub)
     */
    Optional<String> getUserId();

    /**
     * Check if a specific role is present
     */
    boolean hasRole(String role);

    /**
     * Check if a specific permission is present
     */
    boolean hasPermission(String permission);

    /**
     * Check if the user is authenticated
     */
    boolean isAuthenticated();

    /**
     * Get the user's full name
     */
    Optional<String> getFullName();

    /**
     * Get the user's email
     */
    Optional<String> getEmail();
}