package com.nutriSalud.nutri.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 4, max = 50)
    private String username;

    @NotBlank(message = "El password es obligatorio")
    @Size(min = 6, max = 128)
    private String password;

    @Size(max = 15)
    private String dniPaciente;

    @Size(max = 200)
    private String nombresCompletos;
}
