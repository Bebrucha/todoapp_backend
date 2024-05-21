package com.todoapp.todoapp.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service

public class JwtService {
    private static final String SECRET_KEY = "9AFC7B44C1686F6153055F8A47F7D23E993456F9036E7CE09E8308F8A6995A1B";

    /**
     * Extracts the username from the given JWT.
     *
     * @param jwt the JWT from which to extract the username
     * @return the username extracted from the JWT
     */
    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    /**
     * Extracts a claim from the given JWT token.
     *
     * @param <T>            the type of the claim to extract
     * @param jwt            the JWT token
     * @param claimsResolver the function to resolve the claim from the JWT token's
     *                       claims
     * @return the extracted claim
     */
    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    /**
     * Checks if the given JWT token is valid for the specified user.
     *
     * @param jwt         The JWT token to validate.
     * @param userDetails The user details of the user to validate against.
     * @return true if the token is valid for the user, false otherwise.
     */
    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String username = extractUsername(jwt);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
    }

    /**
     * Checks if the given JWT token is expired.
     *
     * @param jwt the JWT token to check
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    /**
     * Extracts the expiration date from the provided JWT token.
     *
     * @param jwt The JWT token from which to extract the expiration date.
     * @return The expiration date extracted from the JWT token.
     */
    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    /**
     * Generates a JWT token with the given extra claims and user details.
     *
     * @param extraClaims the extra claims to include in the token
     * @param userDetails the user details used to generate the token
     * @return the generated JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails the user details used to generate the token
     * @return the generated JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Represents the claims (payload) of a JSON Web Token (JWT).
     * Claims are statements about an entity (typically, the user) and additional
     * data.
     * They can be used to provide information such as the user's identity, roles,
     * and permissions.
     */
    private Claims extractAllClaims(String jwt) {
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(jwt).getBody();
    }

    /**
     * Retrieves the signing key used for JWT token generation and verification.
     *
     * @return The signing key as a Key object.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
