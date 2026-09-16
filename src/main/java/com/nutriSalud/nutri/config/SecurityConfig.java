package com.nutriSalud.nutri.config;

import com.nutriSalud.nutri.dto.ApiResponse;
import com.nutriSalud.nutri.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Value("${cors.origenes-permitidos:http://localhost:4200,http://localhost:8080}")
    private String origenesPermitidos;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(fo -> fo.sameOrigin()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // --- ENDPOINTS PUBLICOS (RQ: login entrega token) ---
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/register/ciudadano",
                                "/error",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/openapi.json",
                                "/h2-console/**"
                        ).permitAll()

                        // --- REGISTRO DE PERSONAL: SOLO PERSONAL DE SALUD ---
                        .requestMatchers("/api/v1/auth/register/personal")
                        .hasRole("PERSONAL_SALUD")

                        // --- ESCRITURA CLINICA: SOLO PERSONAL DE SALUD ---
                        .requestMatchers(HttpMethod.POST, "/api/v1/pacientes").hasRole("PERSONAL_SALUD")
                        .requestMatchers(HttpMethod.PUT,  "/api/v1/pacientes/**").hasRole("PERSONAL_SALUD")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/pacientes/**").hasRole("PERSONAL_SALUD")
                        .requestMatchers(HttpMethod.POST, "/api/v1/pacientes/**").hasRole("PERSONAL_SALUD")
                        .requestMatchers(HttpMethod.POST, "/api/v1/seguros/**").hasRole("PERSONAL_SALUD")
                        .requestMatchers(HttpMethod.PUT,  "/api/v1/citas/**").hasRole("PERSONAL_SALUD")

                        // --- REPORTES GLOBALES Y LISTADOS COMPLETOS: PRIVACIDAD ---
                        .requestMatchers(
                                "/api/v1/pacientes/inactivos/**",
                                "/api/v1/pacientes/resumen/**"
                        ).hasRole("PERSONAL_SALUD")

                        .requestMatchers(HttpMethod.GET, "/api/v1/pacientes")
                        .hasRole("PERSONAL_SALUD")

                        // --- LECTURAS AUTENTICADAS ---
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                enviarJSend(response, 401, "fail", "No autorizado: token ausente, invalido o expirado", "jwt"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                enviarJSend(response, 403, "fail", "Prohibido: su rol no tiene permiso para realizar esta accion", "rol"))
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void enviarJSend(HttpServletResponse response, int status, String statusJs, String mensaje, String claveMensaje) {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> data = new LinkedHashMap<>();
        data.put(claveMensaje, mensaje);
        data.put("codigoHttp", status);
        String jsonClaveMensaje = "\"" + escapeJson(claveMensaje) + "\"";
        String jsonMensaje = "\"" + escapeJson(mensaje) + "\"";
        String body = "{" +
                "\"status\":\"" + statusJs + "\"," +
                "\"data\":{" +
                jsonClaveMensaje + ":" + jsonMensaje + "," +
                "\"codigoHttp\":" + status +
                "}}";
        try {
            response.getWriter().write(body);
            response.getWriter().flush();
        } catch (Exception ignored) {
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        String[] origenes = Arrays.stream(origenesPermitidos.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toArray(String[]::new);
        config.setAllowedOrigins(List.of(origenes));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
