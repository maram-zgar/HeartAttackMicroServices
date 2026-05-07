package dev.maram.gateway.config;

import dev.maram.gateway.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshExpiration;

    /**
     * Extracts the JWT subject.
     * In this architecture, the subject = user ID.
     */
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the email custom claim.
     */
    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    /**
     * Generic claim extractor.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates an ACCESS token.
     */
    public String generateToken(User user) {

        Map<String, Object> extraClaims = new HashMap<>();

        extraClaims.put("email", user.getEmail());

        extraClaims.put(
                "authorities",
                user.getAuthorities()
                        .stream()
                        .map(authority -> authority.getAuthority())
                        .collect(Collectors.toList())
        );

        return generateToken(extraClaims, user);
    }

    /**
     * Generates an ACCESS token with custom claims.
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            User user
    ) {
        return buildToken(extraClaims, user, jwtExpiration);
    }

    /**
     * Generates a REFRESH token.
     */
    public String generateRefreshToken(User user) {

        Map<String, Object> extraClaims = new HashMap<>();

        extraClaims.put("email", user.getEmail());

        return buildToken(extraClaims, user, refreshExpiration);
    }

    /**
     * Core JWT builder.
     *
     * SUBJECT = USER ID
     * EMAIL = CUSTOM CLAIM
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            User user,
            long expiration
    ) {

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Validates the token.
     *
     * Since subject = user ID,
     * validation compares IDs.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {

        if (!(userDetails instanceof User user)) {
            return false;
        }

        final String userId = extractUserId(token);

        return userId.equals(user.getId().toString())
                && !isTokenExpired(token);
    }

    /**
     * Checks whether the token is expired.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts expiration date.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from JWT.
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Returns the signing key used
     * to sign and validate JWTs.
     */
    private Key getSignInKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}