package com.ninos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;


@Service
public class TokenService {

    private SecretKey key;

    @Value("${jwt.secret.string}")
    private String JWT_SECRET;

    @Value("${jwt.expiration.time}")
    private long EXPIRATION_TIME;

    // when TokenService class created, this method will run
    @PostConstruct
    private void init(){
        byte[] keyByte = JWT_SECRET.getBytes(StandardCharsets.UTF_8); // Convert the secret string (JWT_SECRET) into a sequence of bytes
        this.key = new SecretKeySpec(keyByte, "HmacSHA256"); // Create a SecretKey object from the byte array using the HmacSHA256 algorithm.
    }


    public String generateToken(String email){
        return Jwts.builder()
                .subject(email) // Set the "subject" claim in the JWT payload.
                .issuedAt(new Date(System.currentTimeMillis())) // Set the "issuedAt" time (iat claim) to the current time.
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Set the "expiration" time (exp claim) by adding EXPIRATION_TIME
                .signWith(key)
                .compact(); // converts all claims into one Base64-encoded JWT string(header.payload.signature).
    }


    // Extract the username from the JWT token
    public String getUsernameFromToken(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims,T> claimsTFunction){
        return claimsTFunction
                .apply(Jwts.parser() // Creates a parser to read the JWT
                        .verifyWith(key) // Sets your secret key to verify the signature
                        .build() //  // Build the parser
                        .parseSignedClaims(token) // // Parse(scan) the token and get the signed claims
                        .getPayload()); // Extract the payload part (the actual claims inside the JWT)
    }


    // ✅ Method to check if a token is valid for a given user
    public boolean isTokenValid(String token, UserDetails userDetails){
        // Step 1: Extract the username from the JWT token
        final String username = getUsernameFromToken(token);

        // Step 2: Compare the username inside the token with the actual logged-in user
        // Step 3: Also make sure the token is not expired
        // If both are true → token is valid
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token){
        // Step 1: Extract the "expiration" claim (the expiry date) from the JWT
        Date expirationDate = extractClaims(token, Claims::getExpiration);

        // Step 2: Check if expiration date is before the current time
        // If expirationDate < currentDate → token is expired → return true
        // Otherwise → token is still valid → return false
        return expirationDate.before(new Date());
    }



}




/*
* what will happen when .signWith(key) called
-Header:   {"alg":"HS256","typ":"JWT"}           → Base64Url → eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
-Payload:  {"sub":"ninoos@example.com","iat":...} → Base64Url → eyJzdWIiOiJuaW5vb3NAZXhhbXBsZS5jb20iLCJpYXQiOj...
-Data to sign: header.payload
-Signature: HMAC-SHA256(key, data) → Base64Url
-Final JWT: header.payload.signature
*
* | Part             | JSON / Description                                                     | Base64Url Encoded                                                                                                                                                         |
| ---------------- | ---------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Header**       | `{"alg":"HS256","typ":"JWT"}`                                          | `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9`                                                                                                                                    |
| **Payload**      | `{"sub":"ninoos@example.com","iat":1697184000000,"exp":1697184060000}` | `eyJzdWIiOiJuaW5vb3NAZXhhbXBsZS5jb20iLCJpYXQiOjE2OTcxODQwMDAwLCJleHAiOjE2OTcxODQ2MDAwfQ`                                                                                  |
| **Data to sign** | header.payload                                                         | `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJuaW5vb3NAZXhhbXBsZS5jb20iLCJpYXQiOjE2OTcxODQwMDAwLCJleHAiOjE2OTcxODQ2MDAwfQ`                                             |
| **Signature**    | HMAC-SHA256(key, data)                                                 | `J2RmkxZRcVZlZf3oYvP1kI7pV0x7i4Y8IXLhxqLv1tY`                                                                                                                             |
| **Final JWT**    | header.payload.signature  (compact())                                  | `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJuaW5vb3NAZXhhbXBsZS5jb20iLCJpYXQiOjE2OTcxODQwMDAwLCJleHAiOjE2OTcxODQ2MDAwfQ.J2RmkxZRcVZlZf3oYvP1kI7pV0x7i4Y8IXLhxqLv1tY` |
*
* */
