package com.hotel.notificacion.infrastructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Cliente para comunicarse con ms-auth-service
 * Resuelve userId desde email
 */
@Component
public class UserClient {

    private static final Logger log = LoggerFactory.getLogger(UserClient.class);
    private final RestTemplate restTemplate;

    public UserClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Obtiene el userId desde el email del usuario
     * @param email Email del usuario
     * @return userId o null si no se encuentra
     */
    public Long getUserIdByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        try {
            String url = String.format("http://auth-service/api/v1/internal/users/by-email?email=%s", email);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("userId")) {
                Object userIdObj = response.get("userId");
                if (userIdObj instanceof Number) {
                    return ((Number) userIdObj).longValue();
                }
            }
            return null;
        } catch (Exception e) {
            log.warn("No se pudo resolver userId para email: {} - Error: {}", email, e.getMessage());
            return null;
        }
    }
}
