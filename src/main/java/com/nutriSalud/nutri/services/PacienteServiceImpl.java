package com.nutriSalud.nutri.services;

import com.nutriSalud.nutri.core.GestorAsignacionCitasSingleton;
import com.nutriSalud.nutri.core.PacienteFactory;
import com.nutriSalud.nutri.models.Cita;
import com.nutriSalud.nutri.models.EstadoCita;
import com.nutriSalud.nutri.models.EstadoPaciente;
import com.nutriSalud.nutri.models.MotivoCita;
import com.nutriSalud.nutri.models.Paciente;
import com.nutriSalud.nutri.models.Seguro;
import com.nutriSalud.nutri.models.TipoSeguro;
import com.nutriSalud.nutri.repositories.CitaRepository;
import com.nutriSalud.nutri.repositories.PacienteRepository;
import com.nutriSalud.nutri.repositories.SeguroRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteServiceImpl implements PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private SeguroRepository seguroRepository;

    @Autowired
    private CitaRepository citaRepository;

    @PostConstruct
    public void inicializarDesdeBD() {
        GestorAsignacionCitasSingleton gestor = GestorAsignacionCitasSingleton.getInstancia();
        int maxOrden = citaRepository.findMaxNumeroOrden().orElse(0);
        LocalDateTime ultimaFecha = citaRepository.findTopByOrderByFechaHoraDesc().map(Cita::getFechaHora).orElse(null);
        gestor.sincronizarConBaseDeDatos(maxOrden, ultimaFecha);
    }

    @Override
    @Transactional
    public Paciente registrarNuevoPaciente(String dni, String nombre, LocalDate fechNac, double hemoglobina, TipoSeguro tipoSeguro) {
        Paciente nuevo = PacienteFactory.crearPaciente(dni, nombre, fechNac, hemoglobina);

        Seguro seguro = seguroRepository.findByTipo(tipoSeguro)
                .orElseGet(() -> seguroRepository.save(new Seguro(tipoSeguro)));
        nuevo.setSeguro(seguro);

        Paciente guardado = pacienteRepository.save(nuevo);

        Cita cita = asignarCitaAutomatica(guardado.getDni());
        guardado.setOrdenCita(cita.getNumeroOrden());
        return pacienteRepository.save(guardado);
    }

    @Override
    public List<Paciente> obtenerTodosLosPacientes() {
        return pacienteRepository.findAll();
    }

    @Override
    public Optional<Paciente> buscarPorDni(String dni) {
        return pacienteRepository.findByDni(dni);
    }

    @Override
    public Seguro registrarSeguro(TipoSeguro tipoSeguro) {
        return seguroRepository.findByTipo(tipoSeguro)
                .orElseGet(() -> seguroRepository.save(new Seguro(tipoSeguro)));
    }

    @Override
    public List<Seguro> listarTodosLosSeguros() {
        return seguroRepository.findAll();
    }

    @Override
    public Optional<Seguro> buscarSeguroPorTipo(TipoSeguro tipo) {
        return seguroRepository.findByTipo(tipo);
    }

    @Override
    @Transactional
    public Cita asignarCitaAutomatica(String dniPaciente) {
        Paciente paciente = pacienteRepository.findByDni(dniPaciente)
                .orElseThrow(() -> new IllegalStateException("Paciente con DNI " + dniPaciente + " no existe"));

        GestorAsignacionCitasSingleton gestor = GestorAsignacionCitasSingleton.getInstancia();

        int numeroOrden = gestor.siguienteNumeroOrden();
        LocalDateTime fechaHora = gestor.siguienteFechaHora();
        MotivoCita motivo = determinarMotivoCita(paciente.getEstado());

        Cita cita = new Cita(numeroOrden, fechaHora, motivo, paciente);
        return citaRepository.save(cita);
    }

    private MotivoCita determinarMotivoCita(EstadoPaciente estado) {
        if (estado == null) return MotivoCita.CONSULTA;
        return switch (estado) {
            case CRITICO -> MotivoCita.TRATAMIENTO;
            case MEDIO -> MotivoCita.SEGUIMIENTO;
            case BUENO -> MotivoCita.CONSULTA;
        };
    }

    @Override
    public Optional<Cita> buscarCitaPorNumeroOrden(Integer numeroOrden) {
        return citaRepository.findById(numeroOrden);
    }

    @Override
    public List<Cita> listarCitasPorEstado(EstadoCita estado) {
        return citaRepository.findByEstado(estado);
    }

    @Override
    public List<Cita> listarTodasLasCitas() {
        return citaRepository.findAll();
    }

    @Override
    @Transactional
    public Cita cambiarEstadoCita(Integer numeroOrden, EstadoCita nuevoEstado) {
        Cita cita = citaRepository.findById(numeroOrden)
                .orElseThrow(() -> new IllegalStateException("Cita con orden " + numeroOrden + " no existe"));
        cita.setEstado(nuevoEstado);
        return citaRepository.save(cita);
    }
}
