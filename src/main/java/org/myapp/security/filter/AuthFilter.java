package org.myapp.security.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.myapp.dao.UserDao;
import org.myapp.entity.User;
import org.myapp.security.identity.CurrentUser;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

@Provider
public class AuthFilter implements ContainerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER    = "Authorization";
    private static final ObjectMapper MAPPER   = new ObjectMapper();

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/auth/register",
            "/auth/login",
            "/q/health",
            "/q/openapi",
            "/q/swagger-ui"
    );

    @ConfigProperty(name = "mp.jwt.verify.secret.key")
    String secretKey;

    @Inject
    UserDao userDao;

    @Inject
    CurrentUser currentUser;

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String path = ctx.getUriInfo().getPath();

        // Bỏ qua các public path
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return;
        }

        // Kiểm tra Authorization header
        String authHeader = ctx.getHeaderString(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            abort(ctx, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // Parse & verify JWT thủ công
        Long userId = parseAndVerify(token);
        if (userId == null) {
            abort(ctx, "Invalid token");
            return;
        }

        // Kiểm tra user tồn tại và còn active
        Optional<User> userOpt = userDao.findByIdOptional(userId);
        if (userOpt.isEmpty() || !userOpt.get().active) {
            abort(ctx, "User not found or inactive");
            return;
        }

        currentUser.setUser(userOpt.get());
    }

    /**
     * Xác thực chữ ký HS256 và trả về userId từ claim "sub".
     * Trả về null nếu token không hợp lệ.
     */
    private Long parseAndVerify(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;

            // 1. Verify signature
            String signingInput = parts[0] + "." + parts[1];
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(), "HmacSHA256"));
            byte[] expectedSig = mac.doFinal(signingInput.getBytes());
            byte[] actualSig   = Base64.getUrlDecoder().decode(addPadding(parts[2]));

            if (!MessageDigest.isEqual(expectedSig, actualSig)) return null;

            // 2. Parse claims
            String claimsJson = new String(Base64.getUrlDecoder().decode(addPadding(parts[1])));
            JsonNode claims = MAPPER.readTree(claimsJson);

            // 3. Kiểm tra expiry
            if (claims.has("exp")) {
                long exp = claims.get("exp").asLong();
                if (System.currentTimeMillis() / 1000 > exp) return null;
            }

            // 4. Lấy subject (userId)
            JsonNode sub = claims.get("sub");
            if (sub == null) return null;

            return Long.parseLong(sub.asText());

        } catch (Exception e) {
            return null;
        }
    }

    /** Base64URL padding */
    private String addPadding(String s) {
        return switch (s.length() % 4) {
            case 2 -> s + "==";
            case 3 -> s + "=";
            default -> s;
        };
    }

    private void abort(ContainerRequestContext ctx, String message) {
        ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\":\"" + message + "\"}")
                .type("application/json")
                .build());
    }
}
