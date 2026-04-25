package com.jesus.proyecto.chat._general.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
public class JwtConfig {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private String accessExpirationStr;

    @Value("${jwt.refresh.expiration}")
    private String refreshExpirationStr;

    @Value("${jwt.refresh.rotation}")
    private String refreshRotationStr;

    @Getter
    private long accessExpiration = parseAccessDuration();
    @Getter
    private long refreshExpiration = parseRefreshDuration();
    @Getter
    private long refreshRotation = parseRefreshRotation();


    public String getSecretKey() {
        System.out.println("\n-------");
        System.out.println(secretKey);
        System.out.println("-------\n");
        if (secretKey.length() < 32) {
            throw new IllegalArgumentException("Clave secreta debe ser al menos 256 bits (32 chars ASCII)");
        }
        return secretKey;
    }

    private long parseAccessDuration() {
        try {
            return parseDuration(accessExpirationStr);
        } catch (Exception e) {
            return (long)(15 * 60 * 1000L); // fallback: 15 minutos
        }
    }
    private long parseRefreshDuration() {
        try {
            return parseDuration(refreshExpirationStr);
        } catch (Exception e) {
            return (long)(7 * 24 * 60 * 60 * 1000L); // fallback: 7 days
        }
    }
    private long parseRefreshRotation() {
        try {
            return parseDuration(refreshRotationStr);
        } catch (Exception e) {
            return (long)(3 * 24 * 60 * 60 * 1000L); // fallback: 3 dias
        }
    }

    private static long parseDuration(String durationStr) throws IllegalArgumentException {
        if (durationStr.endsWith("m")) {
            return Long.parseLong(durationStr.replace("m", "")) * 60 * 1000L;
        } else if (durationStr.endsWith("h")) {
            return Long.parseLong(durationStr.replace("h", "")) * 3600 * 1000L;
        } else if (durationStr.endsWith("d")) {
            return Long.parseLong(durationStr.replace("d", "")) * 24 * 60 * 60 * 1000L;
        }
        throw new IllegalArgumentException("Formato de expiración inválido. Usa 'm', 'h', o 'd'");
    }
}
