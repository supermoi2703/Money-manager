package com.springboot.moneymanager.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    private long expirationMs = 86400000; // 24 hours in milliseconds

    // Lấy key để ký token
    public Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Thời gian hết hạn token
    public Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + expirationMs);
    }

    // Tạo JWT token từ email
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(getExpirationDate())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Decode token để lấy thông tin claims
    public Claims getTokenFromClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // lấy email từ token
    public String getEmailFromToken(String token) {
        return getTokenFromClaims(token).getSubject();
    }

    // lấy thời gian hêt hạn từ token
    public  Date getExpirationDateFromToken(String token) {
        return getTokenFromClaims(token).getExpiration();
    }

    // kiểm tra token còn hiệu lực hay không
    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    // kiểm tra token có hợp lệ hay không
    public boolean isTokenValid(String token, String email) {
        try {
            String tokenEmail = getEmailFromToken(token);
            boolean emailMatches = tokenEmail.equals(email);
            boolean notExpired = !isTokenExpired(token);
            return emailMatches && notExpired;
        }catch (Exception e){
            return false;
        }
    }

    // lấy username từ token (trong trường hợp này là email)
    public String extractUsername(String jwt) {
        return getEmailFromToken(jwt);
    }

    // kiểm tra token có hợp lệ hay không dựa trên thông tin userDetails
    public boolean validateToken(String jwt, UserDetails userDetails) {
        String email = extractUsername(jwt);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
    }
}
