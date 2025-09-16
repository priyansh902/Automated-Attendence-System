package com.attendence.rural.Jwt;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class Teacher_jwt {

    // private final String secret = "supersecretkey_715366_Attendence_Automated";

    private final Key key ;
    private final long expirations;

    public Teacher_jwt(@Value("${jwt.secret}") String secret,
                         @Value("${jwt.expirations}") long expirations) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirations = expirations;

    }


    public String generateToken(String username, List<String> roles) {

        return Jwts.builder()
                .setSubject(username)
                    .claim("roles", roles)
                    .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + expirations))
                            .signWith(key, SignatureAlgorithm.HS256)
                                .compact();        

    }

    public String extractUsername (String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                    .build()
                        .parseClaimsJws(token)
                            .getBody()
                                .getSubject();
    }

    // Extract roles from token
    public List<String> extractRoles(String token) {
         Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                    .build()
                        .parseClaimsJws(token)
                            .getBody();

             Object rolesObj = claims.get("roles");
                 if (rolesObj instanceof List<?> list) {
     return list.stream()
                   .map(String::valueOf) // convert each element to String
                   .toList();
                 }
                 
    return Collections.emptyList();
        
    }


    public boolean isTokenValid(String token ) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    
}
