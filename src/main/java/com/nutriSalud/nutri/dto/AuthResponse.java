package com.nutriSalud.nutri.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private String token;
    private String tokenTipo;
    private Long vencimientoMinutos;
    private String username;
    private String rol;
    private String rolEtiqueta;
    private String mensaje;
}
