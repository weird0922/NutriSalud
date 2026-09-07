package com.nutriSalud.nutri.controllers;

import com.nutriSalud.nutri.models.Cita;
import com.nutriSalud.nutri.models.EstadoCita;
import com.nutriSalud.nutri.models.EstadoPaciente;
import com.nutriSalud.nutri.models.MotivoCita;
import com.nutriSalud.nutri.models.Paciente;
import com.nutriSalud.nutri.models.Seguro;
import com.nutriSalud.nutri.models.TipoSeguro;
import com.nutriSalud.nutri.services.IPacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/${api.version}/pacientes")
public class PacienteController {

    @Autowired
    private IPacienteService pacienteService;

    // =======================
    // PACIENTES — CRUD COMPLETO
    // =======================

    @PostMapping("")
    public ResponseEntity<?> crearPaciente(@RequestBody Paciente datosEntrada) {
        try {
            Paciente registrado = pacienteService.registrarPacienteDesdeRequest(datosEntrada);
            return ResponseEntity.ok(registrado);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("")
    public List<Paciente> listarPacientes() {
        return pacienteService.obtenerTodosLosPacientes();
    }

    @GetMapping("/inactivos")
    public List<Paciente> listarPacientesInactivos() {
        return pacienteService.obtenerTodosLosPacientesInactivos();
    }

    @GetMapping("/inactivos/cantidad")
    public Map<String, Long> cantidadPacientesInactivos() {
        return Map.of("cantidadInactivos", pacienteService.contarPacientesInactivos());
    }

    @GetMapping("/{dni}")
    public ResponseEntity<Paciente> buscarPorDni(
            @PathVariable String dni,
            @RequestParam(required = false, defaultValue = "false") boolean incluirInactivos) {
        if (incluirInactivos) {
            return pacienteService.buscarPorDniIncluyendoInactivos(dni)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return pacienteService.buscarPorDni(dni)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{dni}")
    public ResponseEntity<?> actualizarPaciente(
            @PathVariable String dni,
            @RequestBody Paciente datosActualizar) {
        try {
            Paciente actualizado = pacienteService.actualizarPaciente(dni, datosActualizar);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<?> desactivarPaciente(@PathVariable String dni) {
        try {
            pacienteService.desactivarPaciente(dni);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Paciente desactivado exitosamente",
                    "dni", dni,
                    "accion", "soft-delete (activo = false)"
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{dni}/reactivar")
    public ResponseEntity<?> reactivarPaciente(@PathVariable String dni) {
        try {
            pacienteService.reactivarPaciente(dni);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Paciente reactivado exitosamente",
                    "dni", dni,
                    "accion", "restauracion (activo = true)"
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =======================
    // PACIENTES — BÚSQUEDAS
    // =======================

    @GetMapping("/buscar/apellido")
    public List<Paciente> buscarPorApellido(@RequestParam String q) {
        return pacienteService.buscarPorApellido(q);
    }

    @GetMapping("/buscar/apellido/named")
    public List<Paciente> buscarPorApellidoNamed(@RequestParam String q) {
        return pacienteService.buscarPorApellidoUsandoNamedQuery(q);
    }

    @GetMapping("/buscar/estado")
    public List<Paciente> buscarPorEstado(@RequestParam EstadoPaciente estado) {
        return pacienteService.buscarPorEstadoOrdenados(estado);
    }

    @GetMapping("/buscar/hemoglobina")
    public List<Paciente> buscarPorRangoHemoglobina(
            @RequestParam double min,
            @RequestParam double max) {
        return pacienteService.buscarPorRangoHemoglobina(min, max);
    }

    @GetMapping("/buscar/hemoglobina/menor")
    public List<Paciente> buscarHemoglobinaInferior(@RequestParam double umbral) {
        return pacienteService.buscarConHemoglobinaInferiorA(umbral);
    }

    @GetMapping("/buscar/seguro")
    public List<Paciente> buscarPorTipoSeguro(@RequestParam TipoSeguro tipo) {
        return pacienteService.buscarPorTipoSeguro(tipo);
    }

    @GetMapping("/buscar/nacimiento")
    public List<Paciente> buscarPorRangoFechaNacimiento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return pacienteService.buscarPorRangoFechaNacimiento(inicio, fin);
    }

    @GetMapping("/resumen/estado")
    public Map<EstadoPaciente, Long> resumenPorEstado() {
        return pacienteService.resumenPorEstado();
    }

    @GetMapping("/resumen/hemoglobina/promedio")
    public ResponseEntity<Map<String, Object>> promedioHemoglobina(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(pacienteService.promedioHemoglobinaResumido(inicio, fin));
    }

    // =======================
    // SEGUROS
    // =======================

    @GetMapping("/api/${api.version}/tipos-seguro")
    public List<Map<String, String>> listarTiposSeguro() {
        return Arrays.stream(TipoSeguro.values())
                .map(t -> Map.of(
                        "valor", t.name(),
                        "abreviatura", t.getAbreviatura(),
                        "nombreCompleto", t.getNombreCompleto(),
                        "etiqueta", t.getEtiqueta()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/${api.version}/seguros")
    public List<Seguro> listarSeguros() {
        return pacienteService.listarTodosLosSeguros();
    }

    @GetMapping("/api/${api.version}/seguros/{tipo}")
    public ResponseEntity<Seguro> buscarSeguroPorTipo(@PathVariable TipoSeguro tipo) {
        return pacienteService.buscarSeguroPorTipo(tipo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/${api.version}/seguros/{tipo}/pacientes/cantidad")
    public Map<String, Object> contarPacientesPorSeguro(@PathVariable TipoSeguro tipo) {
        long cantidad = pacienteService.contarPacientesPorTipoSeguro(tipo);
        return Map.of(
                "tipoSeguro", tipo.name(),
                "etiqueta", tipo.getEtiqueta(),
                "cantidadPacientes", cantidad
        );
    }

    @PostMapping("/api/${api.version}/seguros")
    public ResponseEntity<Seguro> crearSeguro(@RequestParam TipoSeguro tipo) {
        return ResponseEntity.ok(pacienteService.registrarSeguro(tipo));
    }

    // =======================
    // CITAS
    // =======================

    @GetMapping("/api/${api.version}/citas")
    public List<Cita> listarCitas(
            @RequestParam(required = false) EstadoCita estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(required = false) MotivoCita motivo) {
        return pacienteService.buscarCitasConFiltrosCompuestos(estado, desde, hasta, motivo);
    }

    @GetMapping("/api/${api.version}/citas/paciente/{dniPaciente}")
    public List<Cita> buscarCitasDePaciente(@PathVariable String dniPaciente) {
        return pacienteService.buscarCitasDePaciente(dniPaciente);
    }

    @GetMapping("/api/${api.version}/citas/motivo")
    public List<Cita> buscarCitasPorMotivo(@RequestParam MotivoCita motivo) {
        return pacienteService.buscarCitasPorMotivo(motivo);
    }

    @GetMapping("/api/${api.version}/citas/rango")
    public List<Cita> buscarCitasPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return pacienteService.buscarCitasPorRangoFechas(desde, hasta);
    }

    @GetMapping("/api/${api.version}/citas/orden/{numeroOrden}")
    public ResponseEntity<Cita> buscarCitaPorOrden(@PathVariable Integer numeroOrden) {
        return pacienteService.buscarCitaPorNumeroOrden(numeroOrden)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/${api.version}/citas/resumen/estado")
    public Map<EstadoCita, Long> resumenCitasPorEstado() {
        return pacienteService.resumenCitasPorEstado();
    }

    @GetMapping("/api/${api.version}/citas/resumen/motivo")
    public Map<MotivoCita, Long> resumenCitasPorMotivo() {
        return pacienteService.resumenCitasPorMotivo();
    }

    @PutMapping("/api/${api.version}/citas/{numeroOrden}/estado")
    public ResponseEntity<?> cambiarEstadoCita(
            @PathVariable Integer numeroOrden,
            @RequestParam EstadoCita nuevoEstado) {
        try {
            Cita actualizada = pacienteService.cambiarEstadoCita(numeroOrden, nuevoEstado);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
