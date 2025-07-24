package spring.practice.elmenus_lite.security.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.exception.InvalidJwtTokenException;
import spring.practice.elmenus_lite.exception.JwtGenerationFailedException;
import spring.practice.elmenus_lite.security.config.JwtConfigProperties;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtConfigProperties jwtConfig;

    // generate token
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(jwtConfig.tokenExpirationSeconds(), ChronoUnit.SECONDS);

        final String username = authentication.getName();
        final List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("roles", roles)
                .issuer(jwtConfig.issuer())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expirationTime))
                .jwtID(UUID.randomUUID().toString())
                .build();

        SignedJWT signedJWT;
        try {
            JWSSigner signer = new RSASSASigner(jwtConfig.privateKey());
            signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build(),
                    claims
            );
            signedJWT.sign(signer);

        } catch (JOSEException e) {
            throw new JwtGenerationFailedException(
                    ErrorMessage.JWT_GENERATION_FAILED.getFinalMessage(),
                    e.getCause()
            );
        }

        return signedJWT.serialize();
    }

    // validate token
    public SignedJWT validateToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new RSASSAVerifier(jwtConfig.publicKey());

        if (!signedJWT.verify(verifier)) {
            log.error("Token verification failed. signature invalid. Token ID: {}, Subject: {}",
                    extractClaims(signedJWT).getJWTID(), extractClaims(signedJWT).getSubject());

            throw new InvalidJwtTokenException(ErrorMessage.INVALID_TOKEN_SIGNATURE.getFinalMessage());
        }

        if (isTokenExpired(signedJWT)) {
            throw new InvalidJwtTokenException(ErrorMessage.TOKEN_EXPIRED.getFinalMessage());
        }

        return signedJWT;
    }

    // extract claims
    public JWTClaimsSet extractClaims(SignedJWT signedJWT) throws ParseException {
        return signedJWT.getJWTClaimsSet();
    }

    public String extractUsername(SignedJWT signedJWT) throws ParseException {
        return extractClaims(signedJWT).getSubject();
    }

    public List<String> extractRoles(SignedJWT signedJWT) throws ParseException {
        return extractClaims(signedJWT).getStringListClaim("roles");
    }

    public Date extractExpiration(SignedJWT signedJWT) throws ParseException {
        return extractClaims(signedJWT).getExpirationTime();
    }

    public boolean isTokenExpired(SignedJWT signedJWT) throws ParseException {
        return extractExpiration(signedJWT).before(new Date());
    }
}
