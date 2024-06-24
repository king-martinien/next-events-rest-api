package com.kingmartinien.nextevents.security;

import com.kingmartinien.nextevents.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final TokenRepository tokenRepository;

    @Value("${application.security.jwt.expiration}")
    private Long JWT_EXPIRATION;
    @Value("${application.security.jwt.secret}")
    private String JWT_SECRET;

    public String generateJwtToken(UserDetails userDetails) {
        return this.generateJwtToken(new HashMap<>(), userDetails);
    }

    public String generateJwtToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(JWT_EXPIRATION, ChronoUnit.DAYS)))
                .subject(userDetails.getUsername())
                .claims(extraClaims)
                .claim("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .signWith(getSignInKey())
                .compact();
    }

    public String extractUsername(String jwtToken) {
        return this.extractClaim(jwtToken, Claims::getSubject);
    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        String username = this.extractUsername(jwtToken);
        boolean isValid = this.tokenRepository.findByToken(jwtToken)
                .map(token -> !token.isExpired() && !token.isRevoked())
                .orElse(false);
        return (username.equals(userDetails.getUsername())) && !this.isTokenExpired(jwtToken) && isValid;
    }

    private boolean isTokenExpired(String jwtToken) {
        return this.extractClaim(jwtToken, Claims::getExpiration).before(Date.from(Instant.now()));
    }

    private <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(bytes);
    }

}
