package org.myapp.security.jwt;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.myapp.entity.User;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @ConfigProperty(name = "mp.jwt.verify.secret.key")
    String secretKey;

    @ConfigProperty(name = "app.jwt.expiry-hours", defaultValue = "24")
    long expiryHours;

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofHours(expiryHours));

        return Jwt.issuer(issuer)
                .subject(user.id.toString())
                .groups(Set.of(user.systemRole.name()))
                .claim("username", user.username)
                .claim("fullName", user.fullName)
                .issuedAt(now)
                .expiresAt(expiry)
                .signWithSecret(secretKey);
    }
}
