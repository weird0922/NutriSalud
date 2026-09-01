package com.nutriSalud.nutri.controllers;

import com.nutriSalud.nutri.models.Cita;
import com.nutriSalud.nutri.models.EstadoCita;
import com.nutriSalud.nutri.models.Paciente;
import com.nutriSalud.nutri.models.Seguro;
import com.nutriSalud.nutri.models.TipoSeguro;
import com.nutriSalud.nutri.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/web")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    // =======================
    // PACIENTES
    // =======================

    @GetMapping("/pacientes")
    public List<Paciente> listarPacientes() {
        return pacienteService.obtenerTodosLosPacientes();
    }

    @GetMapping("/pacientes/{dni}")
    public ResponseEntity<Paciente> buscarPorDni(@PathVariable String dni) {
        return pacienteService.buscarPorDni(dni)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/pacientes")
    public ResponseEntity<?> agregarPaciente(@RequestBody Paciente datosEntrada) {
        if (datosEntrada.getDni() == null || datosEntrada.getDni().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El DNI es obligatorio"));
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
        } catch (Exception ignored) {
        }

        try {
            Paciente registrado = pacienteService.registrarNuevoPaciente(
                    datosEntrada.getDni(),
                    datosEntrada.getNombre(),
                    datosEntrada.getFechNac(),
                    datosEntrada.getHemoglobina(),
                    tipoSeguro
            );
            return ResponseEntity.ok(registrado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =======================
    // SEGUROS
    // =======================

    @GetMapping("/tipos-seguro")
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

    @GetMapping("/seguros")
    public List<Seguro> listarSeguros() {
        return pacienteService.listarTodosLosSeguros();
    }

    @PostMapping("/seguros")
    public ResponseEntity<Seguro> crearSeguro(@RequestParam TipoSeguro tipo) {
        return ResponseEntity.ok(pacienteService.registrarSeguro(tipo));
    }

    // =======================
    // CITAS
    // =======================

    @GetMapping("/citas")
    public List<Cita> listarCitas(
            @RequestParam(required = false) EstadoCita estado) {
        if (estado != null) {
            return pacienteService.listarCitasPorEstado(estado);
        }
        return pacienteService.listarTodasLasCitas();
    }

    @GetMapping("/citas/orden/{numeroOrden}")
    public ResponseEntity<Cita> buscarCitaPorOrden(@PathVariable Integer numeroOrden) {
        return pacienteService.buscarCitaPorNumeroOrden(numeroOrden)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/citas/{numeroOrden}/estado")
    public ResponseEntity<?> cambiarEstadoCita(
            @PathVariable Integer numeroOrden,
            @RequestParam EstadoCita nuevoEstado) {
        try {
            Cita actualizada = pacienteService.cambiarEstadoCita(numeroOrden, nuevoEstado);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
