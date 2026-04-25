package com.jesus.proyecto.chat.auth.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jesus.proyecto.chat._general.config.JwtConfig;
import com.jesus.proyecto.chat._general.config.SecurityConfig;
import com.jesus.proyecto.chat._general.exceptions.MyAuthException;
import com.jesus.proyecto.chat.auth.dto.AuthRequest;
import com.jesus.proyecto.chat.auth.dto.AuthResponse;
import com.jesus.proyecto.chat.auth.dto.RegistroRequest;
import com.jesus.proyecto.chat.auth.entity.RefreshToken;
import com.jesus.proyecto.chat.auth.repository.RefreshTokenRepository;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.repository.UsuarioRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService userDetailsService;
    private final SecurityConfig securityConfig;
    private final JwtConfig jwtConfig;

    @Transactional
    public AuthResponse registrar(RegistroRequest request, HttpServletResponse response) {
        if (usuarioRepository.existsByUsuario(request.getUsuario())) {
            throw new MyAuthException(Map.of("error", "El usuario ya existe"));
        }

        Usuario usuario = new Usuario();
        usuario.setUsuario(request.getUsuario());
        usuario.setNombre(request.getNombre());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getPassword()));

        Usuario guardado = usuarioRepository.save(usuario);

        generarTokensYCookies(guardado, response);

        return crearResponse(guardado);
    }

    @Transactional
    public AuthResponse autenticar(AuthRequest request, HttpServletResponse response) {

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsuario(),
                            request.getPassword()));
        } catch (AuthenticationException e) {
            throw new MyAuthException("Usuario o password incorrectos");
        }

        Usuario usuario = usuarioRepository.findByUsuario(authentication.getName())
                .orElseThrow(() -> new MyAuthException("Usuario no encontrado"));

        generarTokensYCookies(usuario, response);

        return crearResponse(usuario);
    }

    private void generarTokensYCookies(Usuario usuario, HttpServletResponse response) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsuario());

        Map<String, Object> claims =  Map.of("userId", usuario.getId()) ;

        String accessToken = jwtService.generarAccessToken(userDetails, claims);
        String refreshToken = jwtService.generarRefreshToken(userDetails,claims);

        guardarRefreshToken(refreshToken, usuario);

        response.addCookie(generarAccessCookie(accessToken));
        response.addCookie(generarRefreshCookie(refreshToken));
    }

    @Transactional
    public ResponseEntity<?> refrescarAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshTokenJwt = extraerRefreshToken(request);

        RefreshToken refreshToken = validarRefreshToken(refreshTokenJwt);

        Usuario usuario = refreshToken.getUsuario();
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsuario());

        String token = jwtService.generarAccessToken(userDetails, Map.of("userId", usuario.getId()) );

        response.addCookie(generarAccessCookie(token));

        rotarRefreshSiEsNecesario(refreshToken, usuario, userDetails, response);

        return ResponseEntity.ok().build();
    }

    private String extraerRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new MyAuthException("Refresh token no encontrado");
        }

        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        throw new MyAuthException("Refresh token no encontrado");
    }

    private RefreshToken validarRefreshToken(String tokenJwt) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenJwt)
                .orElseThrow(() -> new MyAuthException("Refresh token invalido"));

        if (!refreshToken.isRevocado()) {
            throw new MyAuthException("Refresh token revocado");
        }

        if (Instant.now().isAfter(refreshToken.getFechaExpiracion())) {
            refreshToken.setRevocado(true);
            refreshTokenRepository.save(refreshToken);
            throw new MyAuthException("Refresh token expirado");
        }

        return refreshToken;
    }

    private void rotarRefreshSiEsNecesario(
            RefreshToken refreshToken,
            Usuario usuario,
            UserDetails userDetails,
            HttpServletResponse response
    ) {
        long tiempoRestante = refreshToken.getFechaExpiracion().toEpochMilli()
                - Instant.now().toEpochMilli();

        if (tiempoRestante >= jwtConfig.getRefreshRotation()) {
            return;
        }

        refreshToken.setRevocado(true);
        refreshTokenRepository.save(refreshToken);

        Map<String, Object> claims =  Map.of("userId", usuario.getId()) ;

        String newRefreshToken = jwtService.generarRefreshToken(userDetails,claims);

        guardarRefreshToken(newRefreshToken, usuario);

        response.addCookie(generarRefreshCookie(newRefreshToken));
    }

    private void guardarRefreshToken(String token, Usuario usuario) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUsuario(usuario);
        refreshToken.setRevocado(true);
        refreshToken.setFechaExpiracion(
                Instant.now().plusMillis(jwtConfig.getRefreshExpiration()));
        refreshTokenRepository.save(refreshToken);
    }

    private AuthResponse crearResponse(Usuario usuario) {
        AuthResponse resp = new AuthResponse();
        resp.setId(usuario.getId());
        resp.setUsuario(usuario.getUsuario());
        resp.setNombre(usuario.getNombre());
        return resp;
    }

    private Cookie generarAccessCookie(String token) {
        return generarCookie("accessToken", token, jwtConfig.getAccessExpiration());
    }

    private Cookie generarRefreshCookie(String token) {
        return generarCookie("refreshToken", token, jwtConfig.getRefreshExpiration());
    }

    private Cookie generarCookie(String nombre, String token, long edad) {
        Cookie cookie = new Cookie(nombre, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (edad / 1000));
        return cookie;
    }

    public void limpiarCookies(HttpServletResponse response) {
        Cookie access = generarAccessCookie("");
        access.setMaxAge(0);
        response.addCookie(access);

        Cookie refresh = generarRefreshCookie("");
        refresh.setMaxAge(0);
        response.addCookie(refresh);
    }
}