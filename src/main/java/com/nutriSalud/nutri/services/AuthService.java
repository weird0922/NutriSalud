package com.nutriSalud.nutri.services;

import com.nutriSalud.nutri.dto.AuthResponse;
import com.nutriSalud.nutri.dto.LoginRequest;
import com.nutriSalud.nutri.dto.RegisterRequest;
import com.nutriSalud.nutri.exceptions.ResourceConflictException;
import com.nutriSalud.nutri.exceptions.ResourceNotFoundException;
import com.nutriSalud.nutri.models.Rol;
import com.nutriSalud.nutri.models.Usuario;
import com.nutriSalud.nutri.repositories.UsuarioRepository;
import com.nutriSalud.nutri.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.vencimiento-minutos:120}")
    private long vencimientoMinutos;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findActivoPorUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", request.getUsername()));

        String token = jwtService.generarToken(usuario.getUsername(), usuario.getRol());
        return AuthResponse.builder()
                .token(token)
                .tokenTipo("Bearer")
                .vencimientoMinutos(vencimientoMinutos)
                .username(usuario.getUsername())
                .rol(usuario.getRol().name())
                .rolEtiqueta(usuario.getRol().getEtiqueta())
                .build();
    }

    @Transactional
    public AuthResponse registrarCiudadano(RegisterRequest request) {
        return registrarUsuarioConRol(request, Rol.CIUDADANO);
    }

    @Transactional
    public AuthResponse registrarPersonalDeSalud(RegisterRequest request) {
        return registrarUsuarioConRol(request, Rol.PERSONAL_SALUD);
    }

    private AuthResponse registrarUsuarioConRol(RegisterRequest request, Rol rol) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new ResourceConflictException("Usuario",
                    "El username '" + request.getUsername() + "' ya se encuentra registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setDniPaciente(request.getDniPaciente());
        usuario.setNombresCompletos(request.getNombresCompletos());

        Usuario guardado = usuarioRepository.save(usuario);
        String token = jwtService.generarToken(guardado.getUsername(), guardado.getRol());
        return AuthResponse.builder()
                .token(token)
                .tokenTipo("Bearer")
                .vencimientoMinutos(vencimientoMinutos)
                .username(guardado.getUsername())
                .rol(guardado.getRol().name())
                .rolEtiqueta(guardado.getRol().getEtiqueta())
                .mensaje("Usuario creado correctamente")
                .build();
    }
}
