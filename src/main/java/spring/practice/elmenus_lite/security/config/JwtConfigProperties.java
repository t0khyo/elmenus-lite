package spring.practice.elmenus_lite.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtConfigProperties(
        String issuer,
        RSAPublicKey publicKey,
        RSAPrivateKey privateKey,
        Long tokenExpirationSeconds
) {
}