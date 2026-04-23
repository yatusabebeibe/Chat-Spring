package com.jesus.proyecto.chat.auth.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jesus.proyecto.chat._general.config.JwtConfig;
import com.jesus.proyecto.chat._general.config.SecurityConfig;
import com.jesus.proyecto.chat._general.exceptions.AuthException;
import com.jesus.proyecto.chat.auth.dto.AuthRequest;
import com.jesus.proyecto.chat.auth.dto.AuthResponse;
import com.jesus.proyecto.chat.auth.dto.RegistroRequest;
import com.jesus.proyecto.chat.auth.entity.RefreshToken;
import com.jesus.proyecto.chat.auth.repository.RefreshTokenRepository;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.repository.UsuarioRepository;

import jakarta.servlet.http.Cookie;
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


    // Registrar nuevo usuario
    @Transactional
    public AuthResponse registrar(RegistroRequest request, HttpServletResponse response) {
        if (usuarioRepository.existsByUsuario(request.getUsuario())) {
            throw new AuthException(Map.of("error", "El usuario ya existe"));
        }

        Usuario usuario = new Usuario();
        usuario.setUsuario(request.getUsuario());
        usuario.setNombre(request.getNombre());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getPassword()));
        
        Usuario guardado = usuarioRepository.save(usuario);

        UserDetails userDetails = userDetailsService.loadUserById(guardado.getId());
        String accessToken = jwtService.generarAccessToken(userDetails);
        String refreshToken = jwtService.generarRefreshToken(userDetails);

        RefreshToken token = new RefreshToken();
        token.setToken(refreshToken);
        token.setFechaExpiracion(Instant.now().plusMillis(jwtConfig.getRefreshExpiration()));
        token.setUsuario(usuario);
        token.setValido(true);
        refreshTokenRepository.save(token);

        AuthResponse authResp = new AuthResponse();
        authResp.setId(usuario.getId());
        authResp.setUsuario(usuario.getUsuario());
        authResp.setNombre(usuario.getNombre());
        authResp.setAccessToken(accessToken);
        authResp.setRefreshToken(refreshToken);

        response.addCookie(generarAccessCookie(accessToken));
        response.addCookie(generarRefreshCookie(refreshToken));

        return authResp;
    }

    // Login completo (genera tokens)
    @Transactional
    public AuthResponse autenticar(AuthRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsuario(), request.getPassword())
        );

        // Obtener detalles del usuario
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());

        // Generar tokens con UserDetails en lugar de String
        String accessToken = jwtService.generarAccessToken(userDetails);

        String refreshToken = jwtService.generarRefreshToken(userDetails);
        
        RefreshToken token = new RefreshToken();
        token.setToken(refreshToken);
        token.setFechaExpiracion(Instant.now().plusMillis(jwtConfig.getRefreshExpiration()));
        
        Usuario usuario = usuarioRepository.findByUsuario(authentication.getName())
            .orElseThrow(() -> new AuthException("Usuario no encontrado"));
        
        token.setUsuario(usuario);
        token.setValido(true);
        refreshTokenRepository.save(token);

        AuthResponse authResp = new AuthResponse();
        authResp.setId(usuario.getId());
        authResp.setUsuario(usuario.getUsuario());
        authResp.setNombre(usuario.getNombre());
        authResp.setAccessToken(accessToken);
        authResp.setRefreshToken(token.getToken());

        response.addCookie(generarAccessCookie(accessToken));
        response.addCookie(generarRefreshCookie(token.getToken()));

        return authResp;
    }


    @Transactional
    public AuthResponse refrescarAccessToken(String refreshTokenJwt, String username) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenJwt)
                .orElseThrow(() -> new AuthException("Refresh token inválido"));

        if (!refreshToken.isValido()) {
            throw new AuthException("Refresh token revocado");
        }

        // Verificar expiración
        if (Instant.now().isAfter(refreshToken.getFechaExpiracion())) {
            refreshToken.setValido(false);
            refreshTokenRepository.save(refreshToken);
            throw new AuthException("Refresh token expirado");
        }

        // Generar nuevo access y refresh tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String accessToken = jwtService.generarAccessToken(userDetails);
        String newRefreshToken = jwtService.generarRefreshToken(userDetails);

        RefreshToken nuevoRefresh = new RefreshToken();
        nuevoRefresh.setUsuario(refreshToken.getUsuario());
        nuevoRefresh.setToken(newRefreshToken);
        nuevoRefresh.setFechaExpiracion(Instant.now().plusMillis(jwtConfig.getRefreshExpiration()));
        nuevoRefresh.setValido(true);
        
        refreshTokenRepository.save(nuevoRefresh);

        Usuario usuario = refreshToken.getUsuario();

        AuthResponse authResp = new AuthResponse();
        authResp.setId(usuario.getId());
        authResp.setUsuario(usuario.getUsuario());
        authResp.setNombre(usuario.getNombre());
        authResp.setAccessToken(accessToken);
        authResp.setRefreshToken(newRefreshToken);
        return authResp;
    }

    private Cookie generarAccessCookie(String token) {
        return generearCookie("accessToken", token, jwtConfig.getAccessExpiration());
    }
    private Cookie generarRefreshCookie(String token) {
        return generearCookie("refreshToken", token, jwtConfig.getAccessExpiration());
    }

    private Cookie generearCookie(String tipo, String token, long edad) {
        Cookie cookie = new Cookie(tipo, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge((int)(edad / 1000));
        cookie.setPath("/");
        // cookie.setAttribute("SameSite", "None");
        return cookie;
    }

    public void limpiarCookies(HttpServletResponse response) {
        Cookie accessCookie = generarAccessCookie("");
        accessCookie.setMaxAge(0); // Expirar inmediatamente
        response.addCookie(accessCookie);
        
        Cookie refreshCookie = generarRefreshCookie("");
        refreshCookie.setMaxAge(0); // Expirar inmediatamente
        response.addCookie(refreshCookie);
    }
}
