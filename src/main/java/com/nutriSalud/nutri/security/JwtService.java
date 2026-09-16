package com.nutriSalud.nutri.security;

import com.nutriSalud.nutri.models.Rol;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secreto}")
    private String secreto;

    @Value("${jwt.vencimiento-minutos:120}")
    private long vencimientoMinutos;

    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public Date extraerVencimiento(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    public Rol extraerRol(String token) {
        Claims claims = extraerTodosLosClaims(token);
        String rolString = claims.get("rol", String.class);
        if (rolString == null) return null;
        return Rol.valueOf(rolString);
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generarToken(String username, Rol rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol.name());
        claims.put("rolEtiqueta", rol.getEtiqueta());
        return construirToken(claims, username);
    }

    public boolean esTokenValido(String token, String username) {
        final String usernameToken = extraerUsername(token);
        return (usernameToken.equals(username) && !estaVencido(token));
    }

    public boolean estaVencido(String token) {
        try {
            return extraerVencimiento(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private SecretKey getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secreto);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            byte[] keyBytes = secreto.getBytes(StandardCharsets.UTF_8);
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String construirToken(Map<String, Object> claims, String subject) {
        long ahora = System.currentTimeMillis();
        long vencimientoMs = vencimientoMinutos * 60 * 1000L;
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(ahora))
                .expiration(new Date(ahora + vencimientoMs))
                .signWith(getSigningKey())
                .compact();
    }
}
