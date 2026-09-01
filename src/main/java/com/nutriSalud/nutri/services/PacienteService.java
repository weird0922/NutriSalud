package com.nutriSalud.nutri.services;

import com.nutriSalud.nutri.models.Cita;
import com.nutriSalud.nutri.models.EstadoCita;
import com.nutriSalud.nutri.models.Paciente;
import com.nutriSalud.nutri.models.Seguro;
import com.nutriSalud.nutri.models.TipoSeguro;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PacienteService {

    Paciente registrarNuevoPaciente(String dni, String nombre, LocalDate fechNac, double hemoglobina, TipoSeguro tipoSeguro);
    List<Paciente> obtenerTodosLosPacientes();
    Optional<Paciente> buscarPorDni(String dni);

    Seguro registrarSeguro(TipoSeguro tipoSeguro);
    List<Seguro> listarTodosLosSeguros();
    Optional<Seguro> buscarSeguroPorTipo(TipoSeguro tipo);

    Cita asignarCitaAutomatica(String dniPaciente);
    Optional<Cita> buscarCitaPorNumeroOrden(Integer numeroOrden);
    List<Cita> listarCitasPorEstado(EstadoCita estado);
    List<Cita> listarTodasLasCitas();
    Cita cambiarEstadoCita(Integer numeroOrden, EstadoCita nuevoEstado);
}
