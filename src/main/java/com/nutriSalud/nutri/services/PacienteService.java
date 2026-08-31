package com.nutriSalud.nutri.services;

import com.nutriSalud.nutri.models.Paciente;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PacienteService {
    Paciente registrarNuevoPaciente(String dni, String nombre, LocalDate fechNac, double hemoglobina);
    List<Paciente> obtenerTodosLosPacientes();
    Optional<Paciente> buscarPorDni(String dni);
}
