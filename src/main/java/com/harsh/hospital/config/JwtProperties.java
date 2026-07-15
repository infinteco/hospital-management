package com.harsh.hospital.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Binds {@code app.jwt.*} configuration. */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, long expirationMs) {
}
