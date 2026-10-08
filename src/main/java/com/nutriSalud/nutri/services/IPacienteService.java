package com.nutriSalud.nutri.services;

import com.nutriSalud.nutri.models.Cita;
import com.nutriSalud.nutri.models.EstadoCita;
import com.nutriSalud.nutri.models.EstadoPaciente;
import com.nutriSalud.nutri.models.MotivoCita;
import com.nutriSalud.nutri.models.Paciente;
import com.nutriSalud.nutri.models.Seguro;
import com.nutriSalud.nutri.models.TipoSeguro;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IPacienteService {

    Paciente registrarNuevoPaciente(String dni, String primerNombre, String apellidoPaterno, String apellidoMaterno, LocalDate fechNac, double hemoglobina, TipoSeguro tipoSeguro);
    Paciente registrarPacienteDesdeRequest(Paciente datosEntrada);
    List<Paciente> obtenerTodosLosPacientes();
    List<Paciente> obtenerTodosLosPacientesInactivos();
    Optional<Paciente> buscarPorDni(String dni);
    Optional<Paciente> buscarPorDniIncluyendoInactivos(String dni);

    Paciente actualizarPaciente(String dni, Paciente datosActualizar);
    void desactivarPaciente(String dni);
    void reactivarPaciente(String dni);
    long contarPacientesInactivos();

    List<Paciente> buscarPorApellido(String apellido);
    List<Paciente> buscarPorApellidoUsandoNamedQuery(String apellido);
    List<Paciente> buscarPorEstadoOrdenados(EstadoPaciente estado);
    List<Paciente> buscarPorRangoHemoglobina(double min, double max);
    List<Paciente> buscarPorTipoSeguro(TipoSeguro tipoSeguro);
    List<Paciente> buscarPorRangoFechaNacimiento(LocalDate inicio, LocalDate fin);
    List<Paciente> buscarConHemoglobinaInferiorA(double umbral);
    Map<EstadoPaciente, Long> resumenPorEstado();
    Map<String, Object> promedioHemoglobinaResumido(LocalDate inicioNac, LocalDate finNac);
    Optional<Double> promedioHemoglobinaPorRangoEdad(LocalDate inicioNac, LocalDate finNac);

    Seguro registrarSeguro(TipoSeguro tipoSeguro);
    List<Seguro> listarTodosLosSeguros();
    Optional<Seguro> buscarSeguroPorTipo(TipoSeguro tipo);
    long contarPacientesPorTipoSeguro(TipoSeguro tipoSeguro);

    Cita asignarCitaAutomatica(String dniPaciente);
    Optional<Cita> buscarCitaPorNumeroOrden(Integer numeroOrden);
    List<Cita> listarCitasPorEstado(EstadoCita estado);
    List<Cita> listarTodasLasCitas();
    Cita cambiarEstadoCita(Integer numeroOrden, EstadoCita nuevoEstado);

    List<Cita> buscarCitasDePaciente(String dniPaciente);
    List<Cita> buscarCitasPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);
    List<Cita> buscarCitasPorMotivo(MotivoCita motivo);
    List<Cita> buscarCitasPorEstadoYRangoFechas(EstadoCita estado, LocalDateTime inicio, LocalDateTime fin);
    List<Cita> buscarCitasConFiltrosCompuestos(EstadoCita estado, LocalDateTime desde, LocalDateTime hasta, MotivoCita motivo);
    Map<EstadoCita, Long> resumenCitasPorEstado();
    Map<MotivoCita, Long> resumenCitasPorMotivo();
}
