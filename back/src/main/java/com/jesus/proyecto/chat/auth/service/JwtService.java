package com.jesus.proyecto.chat.auth.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.jesus.proyecto.chat._general.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final JwtConfig jwtConfig;
    private final SecretKey claveSecreta;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.claveSecreta = Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
    }


    public String extraerUsuario(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extraerFechaExpiracion(token).before(new Date());
    }

    public String generarRefreshToken(UserDetails detallesUsuario) { 
        return generarToken(detallesUsuario, jwtConfig.getRefreshExpiration(), new HashMap<>());
    }
    public String generarRefreshToken(UserDetails detallesUsuario, Map<String, Object> claimsExtra) {
        return generarToken(detallesUsuario, jwtConfig.getRefreshExpiration(), claimsExtra);
    }

    public String generarAccessToken(UserDetails detallesUsuario) {
        return generarToken(detallesUsuario, jwtConfig.getAccessExpiration(), new HashMap<>());
    }
    public String generarAccessToken(UserDetails detallesUsuario, Map<String, Object> claimsExtra) {
        return generarToken(detallesUsuario, jwtConfig.getAccessExpiration(), claimsExtra);
    }

    public boolean esTokenValido(String token, UserDetails userDetails) {
        final String username = extraerUsuario(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private String generarToken(UserDetails detallesUsuario, long fechaExpiracion, Map<String, Object> claimsExtra) {
        return Jwts.builder()
                .claims(claimsExtra)
                .subject(detallesUsuario.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + fechaExpiracion))
                .signWith(claveSecreta)
                .compact();
    }

    // Un claim es un dato del JWT (sujeto, expliracion y demas)
    private <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private Date extraerFechaExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(claveSecreta)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
