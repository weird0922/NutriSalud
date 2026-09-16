package com.nutriSalud.nutri.controllers;

import com.nutriSalud.nutri.dto.ApiResponse;
import com.nutriSalud.nutri.dto.AuthResponse;
import com.nutriSalud.nutri.dto.LoginRequest;
import com.nutriSalud.nutri.dto.RegisterRequest;
import com.nutriSalud.nutri.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${api.version}/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse auth = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(auth));
    }

    @PostMapping("/register/ciudadano")
    public ResponseEntity<ApiResponse<AuthResponse>> registerCiudadano(@Valid @RequestBody RegisterRequest request) {
        AuthResponse auth = authService.registrarCiudadano(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(auth));
    }

    @PostMapping("/register/personal")
    public ResponseEntity<ApiResponse<AuthResponse>> registerPersonal(@Valid @RequestBody RegisterRequest request) {
        AuthResponse auth = authService.registrarPersonalDeSalud(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(auth));
    }
}
