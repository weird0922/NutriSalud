package com.nutriSalud.nutri.services;

import com.nutriSalud.nutri.core.EvaluacionUmbralAnemia;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PacienteServiceImpl implements IPacienteService {

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
    public Paciente registrarPacienteDesdeRequest(Paciente datosEntrada) {
        if (datosEntrada == null) {
            throw new IllegalArgumentException("El cuerpo de la solicitud es obligatorio");
        }
        if (datosEntrada.getDni() == null || datosEntrada.getDni().isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }
        if (datosEntrada.getPrimerNombre() == null || datosEntrada.getPrimerNombre().isBlank()) {
            throw new IllegalArgumentException("El primerNombre es obligatorio");
        }
        if (datosEntrada.getApellidoPaterno() == null || datosEntrada.getApellidoPaterno().isBlank()) {
            throw new IllegalArgumentException("El apellidoPaterno es obligatorio");
        }

        TipoSeguro tipoSeguro = TipoSeguro.SIS;
        try {
            Object seguroObj = datosEntrada.getSeguro();
            if (seguroObj instanceof Seguro s && s.getTipo() != null) {
                tipoSeguro = s.getTipo();
            } else if (datosEntrada.getSeguro() != null) {
                String nombreSeguro = datosEntrada.getSeguro().toString().trim();
                if (!nombreSeguro.isBlank()) {
                    tipoSeguro = TipoSeguro.valueOf(nombreSeguro.toUpperCase());
                }
            }
        } catch (IllegalArgumentException conversion) {
            throw new IllegalArgumentException("El tipo de seguro enviado no es válido");
        }

        return registrarNuevoPaciente(
                datosEntrada.getDni(),
                datosEntrada.getPrimerNombre(),
                datosEntrada.getApellidoPaterno(),
                datosEntrada.getApellidoMaterno(),
                datosEntrada.getFechNac(),
                datosEntrada.getHemoglobina(),
                tipoSeguro
        );
    }

    @Override
    @Transactional
    public Paciente registrarNuevoPaciente(String dni, String primerNombre, String apellidoPaterno, String apellidoMaterno, LocalDate fechNac, double hemoglobina, TipoSeguro tipoSeguro) {
        Paciente nuevo = PacienteFactory.crearPaciente(dni, primerNombre, apellidoPaterno, apellidoMaterno, fechNac, hemoglobina);
        nuevo.setActivo(true);

        Seguro seguro = seguroRepository.findByTipo(tipoSeguro)
                .orElseGet(() -> seguroRepository.save(new Seguro(tipoSeguro)));
        nuevo.setSeguro(seguro);

        Paciente guardado = pacienteRepository.save(nuevo);
        asignarCitaAutomatica(guardado.getDni());
        return guardado;
    }

    @Override
    public List<Paciente> obtenerTodosLosPacientes() {
        return pacienteRepository.listarTodosActivosConNamedQuery();
    }

    @Override
    public List<Paciente> obtenerTodosLosPacientesInactivos() {
        return pacienteRepository.listarTodosInactivos();
    }

    @Override
    public Optional<Paciente> buscarPorDni(String dni) {
        return pacienteRepository.buscarPorDniConNamedQuery(dni);
    }

    @Override
    public Optional<Paciente> buscarPorDniIncluyendoInactivos(String dni) {
        return pacienteRepository.findByDniIncluyendoInactivos(dni);
    }

    @Override
    @Transactional
    public Paciente actualizarPaciente(String dni, Paciente datosActualizar) {
        Paciente existente = pacienteRepository.findByDniIncluyendoInactivos(dni)
                .orElseThrow(() -> new IllegalStateException("Paciente con DNI " + dni + " no existe"));

        if (datosActualizar == null) {
            throw new IllegalArgumentException("El cuerpo de actualización es obligatorio");
        }

        if (datosActualizar.getPrimerNombre() != null && !datosActualizar.getPrimerNombre().isBlank()) {
            existente.setPrimerNombre(datosActualizar.getPrimerNombre());
        }
        if (datosActualizar.getApellidoPaterno() != null && !datosActualizar.getApellidoPaterno().isBlank()) {
            existente.setApellidoPaterno(datosActualizar.getApellidoPaterno());
        }
        if (datosActualizar.getApellidoMaterno() != null) {
            existente.setApellidoMaterno(datosActualizar.getApellidoMaterno());
        }
        if (datosActualizar.getFechNac() != null) {
            existente.setFechNac(datosActualizar.getFechNac());
        }
        if (datosActualizar.getSeguro() != null && datosActualizar.getSeguro().getTipo() != null) {
            TipoSeguro nuevoTipo = datosActualizar.getSeguro().getTipo();
            Seguro seguro = seguroRepository.findByTipo(nuevoTipo)
                    .orElseGet(() -> seguroRepository.save(new Seguro(nuevoTipo)));
            existente.setSeguro(seguro);
        }

        boolean cambioDatosEvaluables = false;
        if (datosActualizar.getHemoglobina() > 0) {
            existente.setHemoglobina(datosActualizar.getHemoglobina());
            cambioDatosEvaluables = true;
        }

        if (cambioDatosEvaluables && existente.getFechNac() != null) {
            EvaluacionUmbralAnemia evaluador = EvaluacionUmbralAnemia.getInstancia();
            int edad = existente.getEdad();
            double hemoglobina = existente.getHemoglobina();
            double umbralNormal = evaluador.obtenerUmbralNormal(edad);
            EstadoPaciente nuevoEstado;
            if (hemoglobina < evaluador.getUmbralCritico()) {
                nuevoEstado = EstadoPaciente.CRITICO;
            } else if (hemoglobina < umbralNormal) {
                nuevoEstado = EstadoPaciente.MEDIO;
            } else {
                nuevoEstado = EstadoPaciente.BUENO;
            }
            existente.setEstado(nuevoEstado);
        }

        return pacienteRepository.save(existente);
    }

    @Override
    @Transactional
    public void desactivarPaciente(String dni) {
        pacienteRepository.findByDniIncluyendoInactivos(dni)
                .orElseThrow(() -> new IllegalStateException("Paciente con DNI " + dni + " no existe"));

        int filas = pacienteRepository.desactivarPorDni(dni);
        if (filas == 0) {
            throw new IllegalStateException("No se pudo desactivar el paciente con DNI " + dni);
        }
    }

    @Override
    @Transactional
    public void reactivarPaciente(String dni) {
        pacienteRepository.findByDniIncluyendoInactivos(dni)
                .orElseThrow(() -> new IllegalStateException("Paciente con DNI " + dni + " no existe"));

        int filas = pacienteRepository.reactivarPorDni(dni);
        if (filas == 0) {
            throw new IllegalStateException("No se pudo reactivar el paciente con DNI " + dni);
        }
    }

    @Override
    public long contarPacientesInactivos() {
        return pacienteRepository.contarInactivos();
    }

    @Override
    public List<Paciente> buscarPorApellido(String apellido) {
        return pacienteRepository.buscarPorApellidoConteniendo(apellido);
    }

    @Override
    public List<Paciente> buscarPorApellidoUsandoNamedQuery(String apellido) {
        return pacienteRepository.buscarPorApellidoConNamedQuery(apellido);
    }

    @Override
    public List<Paciente> buscarPorEstadoOrdenados(EstadoPaciente estado) {
        return pacienteRepository.buscarPorEstadoSaludConNamedQuery(estado);
    }

    @Override
    public List<Paciente> buscarPorRangoHemoglobina(double min, double max) {
        return pacienteRepository.buscarPorRangoHemoglobina(min, max);
    }

    @Override
    public List<Paciente> buscarPorTipoSeguro(TipoSeguro tipoSeguro) {
        return pacienteRepository.buscarPorTipoSeguro(tipoSeguro);
    }

    @Override
    public List<Paciente> buscarPorRangoFechaNacimiento(LocalDate inicio, LocalDate fin) {
        return pacienteRepository.buscarPorRangoFechaNacimiento(inicio, fin);
    }

    @Override
    public List<Paciente> buscarConHemoglobinaInferiorA(double umbral) {
        return pacienteRepository.buscarConHemoglobinaInferiorA(umbral);
    }

    @Override
    public Map<EstadoPaciente, Long> resumenPorEstado() {
        List<Object[]> filas = pacienteRepository.contarPacientesPorEstado();
        Map<EstadoPaciente, Long> resultado = new LinkedHashMap<>();
        for (EstadoPaciente e : EstadoPaciente.values()) {
            resultado.put(e, 0L);
        }
        for (Object[] fila : filas) {
            EstadoPaciente estado = (EstadoPaciente) fila[0];
            Long cantidad = ((Number) fila[1]).longValue();
            resultado.put(estado, cantidad);
        }
        return resultado;
    }

    @Override
    public Map<String, Object> promedioHemoglobinaResumido(LocalDate inicioNac, LocalDate finNac) {
        Map<String, Object> base = new LinkedHashMap<>();
        base.put("inicioNac", inicioNac);
        base.put("finNac", finNac);
        Optional<Double> promedio = promedioHemoglobinaPorRangoEdad(inicioNac, finNac);
        promedio.ifPresentOrElse(
                valor -> base.put("promedioHemoglobina", valor),
                () -> {
                    base.put("promedioHemoglobina", null);
                    base.put("mensaje", "No hay pacientes en ese rango");
                });
        return base;
    }

    @Override
    public Optional<Double> promedioHemoglobinaPorRangoEdad(LocalDate inicioNac, LocalDate finNac) {
        return pacienteRepository.promedioHemoglobinaPorRangoEdad(inicioNac, finNac);
    }

    @Override
    public Seguro registrarSeguro(TipoSeguro tipoSeguro) {
        return seguroRepository.findByTipo(tipoSeguro)
                .orElseGet(() -> seguroRepository.save(new Seguro(tipoSeguro)));
    }

    @Override
    public List<Seguro> listarTodosLosSeguros() {
        return seguroRepository.listarTodosOrdenadosPorTipo();
    }

    @Override
    public Optional<Seguro> buscarSeguroPorTipo(TipoSeguro tipo) {
        return seguroRepository.buscarPorTipoJPQL(tipo);
    }

    @Override
    public long contarPacientesPorTipoSeguro(TipoSeguro tipoSeguro) {
        return seguroRepository.contarPacientesPorTipoSeguro(tipoSeguro);
    }

    @Override
    @Transactional
    public Cita asignarCitaAutomatica(String dniPaciente) {
        Paciente paciente = pacienteRepository.findByDniIncluyendoInactivos(dniPaciente)
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
        return citaRepository.buscarPorEstadoConPaciente(estado);
    }

    @Override
    public List<Cita> listarTodasLasCitas() {
        return citaRepository.buscarPorRangoFechasConPaciente(
                LocalDateTime.of(1970, 1, 1, 0, 0),
                LocalDateTime.of(2200, 12, 31, 23, 59));
    }

    @Override
    @Transactional
    public Cita cambiarEstadoCita(Integer numeroOrden, EstadoCita nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevoEstado de la cita es obligatorio");
        }
        Cita cita = citaRepository.findById(numeroOrden)
                .orElseThrow(() -> new IllegalStateException("Cita con orden " + numeroOrden + " no existe"));
        cita.setEstado(nuevoEstado);
        return citaRepository.save(cita);
    }

    @Override
    public List<Cita> buscarCitasDePaciente(String dniPaciente) {
        return citaRepository.buscarCitasDePaciente(dniPaciente);
    }

    @Override
    public List<Cita> buscarCitasPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return citaRepository.buscarPorRangoFechasConPaciente(inicio, fin);
    }

    @Override
    public List<Cita> buscarCitasPorMotivo(MotivoCita motivo) {
        return citaRepository.buscarPorMotivo(motivo);
    }

    @Override
    public List<Cita> buscarCitasPorEstadoYRangoFechas(EstadoCita estado, LocalDateTime inicio, LocalDateTime fin) {
        return citaRepository.buscarPorEstadoYRangoFechas(estado, inicio, fin);
    }

    @Override
    public List<Cita> buscarCitasConFiltrosCompuestos(EstadoCita estado, LocalDateTime desde, LocalDateTime hasta, MotivoCita motivo) {
        if (motivo != null) {
            return buscarCitasPorMotivo(motivo);
        }
        if (estado != null && desde != null && hasta != null) {
            return buscarCitasPorEstadoYRangoFechas(estado, desde, hasta);
        }
        if (desde != null && hasta != null) {
            return buscarCitasPorRangoFechas(desde, hasta);
        }
        if (estado != null) {
            return listarCitasPorEstado(estado);
        }
        return listarTodasLasCitas();
    }

    @Override
    public Map<EstadoCita, Long> resumenCitasPorEstado() {
        List<Object[]> filas = citaRepository.contarCitasPorEstado();
        Map<EstadoCita, Long> resultado = new LinkedHashMap<>();
        for (EstadoCita e : EstadoCita.values()) {
            resultado.put(e, 0L);
        }
        for (Object[] fila : filas) {
            EstadoCita estado = (EstadoCita) fila[0];
            Long cantidad = ((Number) fila[1]).longValue();
            resultado.put(estado, cantidad);
        }
        return resultado;
    }

    @Override
    public Map<MotivoCita, Long> resumenCitasPorMotivo() {
        List<Object[]> filas = citaRepository.contarCitasPorMotivo();
        Map<MotivoCita, Long> resultado = new LinkedHashMap<>();
        for (MotivoCita m : MotivoCita.values()) {
            resultado.put(m, 0L);
        }
        for (Object[] fila : filas) {
            MotivoCita motivo = (MotivoCita) fila[0];
            Long cantidad = ((Number) fila[1]).longValue();
            resultado.put(motivo, cantidad);
        }
        return resultado;
    }
}
