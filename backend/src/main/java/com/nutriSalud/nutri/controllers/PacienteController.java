package com.nutriSalud.nutri.controllers;

import com.nutriSalud.nutri.dto.ApiResponse;
import com.nutriSalud.nutri.exceptions.ResourceNotFoundException;
import com.nutriSalud.nutri.models.Cita;
import com.nutriSalud.nutri.models.EstadoCita;
import com.nutriSalud.nutri.models.EstadoPaciente;
import com.nutriSalud.nutri.models.MotivoCita;
import com.nutriSalud.nutri.models.Paciente;
import com.nutriSalud.nutri.models.Seguro;
import com.nutriSalud.nutri.models.TipoSeguro;
import com.nutriSalud.nutri.services.IPacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/${api.version}/pacientes")
public class PacienteController {

    @Autowired
    private IPacienteService pacienteService;

    // =======================
    // PACIENTES — CRUD COMPLETO (show/create/update/delete + listar)
    // =======================

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Paciente>>> listarPacientes() {
        List<Paciente> data = pacienteService.obtenerTodosLosPacientes();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{dni}")
    public ResponseEntity<ApiResponse<Paciente>> show(
            @PathVariable String dni,
            @RequestParam(required = false, defaultValue = "false") boolean incluirInactivos) {
        Paciente paciente = (incluirInactivos
                ? pacienteService.buscarPorDniIncluyendoInactivos(dni)
                : pacienteService.buscarPorDni(dni))
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "dni", dni));
        return ResponseEntity.ok(ApiResponse.success(paciente));
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<Paciente>> create(@Valid @RequestBody Paciente datosEntrada) {
        Paciente registrado = pacienteService.registrarPacienteDesdeRequest(datosEntrada);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(registrado));
    }

    @PutMapping("/{dni}")
    public ResponseEntity<ApiResponse<Paciente>> update(
            @PathVariable String dni,
            @Valid @RequestBody Paciente datosActualizar) {
        Paciente actualizado = pacienteService.actualizarPaciente(dni, datosActualizar);
        return ResponseEntity.ok(ApiResponse.success(actualizado));
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> delete(@PathVariable String dni) {
        pacienteService.desactivarPaciente(dni);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("dni", dni);
        data.put("accion", "soft-delete");
        data.put("activo", false);
        data.put("mensaje", "Paciente inhabilitado correctamente");
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/{dni}/reactivar")
    public ResponseEntity<ApiResponse<Map<String, Object>>> reactivar(@PathVariable String dni) {
        pacienteService.reactivarPaciente(dni);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("dni", dni);
        data.put("accion", "reactivar");
        data.put("activo", true);
        data.put("mensaje", "Paciente reactivado correctamente");
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/inactivos")
    public ResponseEntity<ApiResponse<List<Paciente>>> listarInactivos() {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.obtenerTodosLosPacientesInactivos()));
    }

    @GetMapping("/inactivos/cantidad")
    public ResponseEntity<ApiResponse<Map<String, Long>>> cantidadInactivos() {
        Map<String, Long> data = Map.of("cantidadInactivos", pacienteService.contarPacientesInactivos());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // =======================
    // PACIENTES — BÚSQUEDAS
    // =======================

    @GetMapping("/buscar/apellido")
    public ResponseEntity<ApiResponse<List<Paciente>>> buscarPorApellido(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.buscarPorApellido(q)));
    }

    @GetMapping("/buscar/apellido/named")
    public ResponseEntity<ApiResponse<List<Paciente>>> buscarPorApellidoNamed(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.buscarPorApellidoUsandoNamedQuery(q)));
    }

    @GetMapping("/buscar/estado")
    public ResponseEntity<ApiResponse<List<Paciente>>> buscarPorEstado(@RequestParam EstadoPaciente estado) {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.buscarPorEstadoOrdenados(estado)));
    }

    @GetMapping("/buscar/hemoglobina")
    public ResponseEntity<ApiResponse<List<Paciente>>> buscarPorRangoHemoglobina(
            @RequestParam double min,
            @RequestParam double max) {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.buscarPorRangoHemoglobina(min, max)));
    }

    @GetMapping("/buscar/hemoglobina/menor")
    public ResponseEntity<ApiResponse<List<Paciente>>> buscarHemoglobinaInferior(@RequestParam double umbral) {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.buscarConHemoglobinaInferiorA(umbral)));
    }

    @GetMapping("/buscar/seguro")
    public ResponseEntity<ApiResponse<List<Paciente>>> buscarPorTipoSeguro(@RequestParam TipoSeguro tipo) {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.buscarPorTipoSeguro(tipo)));
    }

    @GetMapping("/buscar/nacimiento")
    public ResponseEntity<ApiResponse<List<Paciente>>> buscarPorRangoFechaNacimiento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.buscarPorRangoFechaNacimiento(inicio, fin)));
    }

    @GetMapping("/resumen/estado")
    public ResponseEntity<ApiResponse<Map<EstadoPaciente, Long>>> resumenPorEstado() {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.resumenPorEstado()));
    }

    @GetMapping("/resumen/hemoglobina/promedio")
    public ResponseEntity<ApiResponse<Map<String, Object>>> promedioHemoglobina(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.promedioHemoglobinaResumido(inicio, fin)));
    }

    // =======================
    // SEGUROS
    // =======================

    @GetMapping("/api/${api.version}/tipos-seguro")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> listarTiposSeguro() {
        List<Map<String, String>> tipos = Arrays.stream(TipoSeguro.values())
                .map(t -> Map.of(
                        "valor", t.name(),
                        "abreviatura", t.getAbreviatura(),
                        "nombreCompleto", t.getNombreCompleto(),
                        "etiqueta", t.getEtiqueta()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(tipos));
    }

    @GetMapping("/api/${api.version}/seguros")
    public ResponseEntity<ApiResponse<List<Seguro>>> listarSeguros() {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.listarTodosLosSeguros()));
    }

    @GetMapping("/api/${api.version}/seguros/{tipo}")
    public ResponseEntity<ApiResponse<Seguro>> buscarSeguroPorTipo(@PathVariable TipoSeguro tipo) {
        Seguro seguro = pacienteService.buscarSeguroPorTipo(tipo)
                .orElseThrow(() -> new ResourceNotFoundException("Seguro", "tipo", tipo));
        return ResponseEntity.ok(ApiResponse.success(seguro));
    }

    @GetMapping("/api/${api.version}/seguros/{tipo}/pacientes/cantidad")
    public ResponseEntity<ApiResponse<Map<String, Object>>> contarPacientesPorSeguro(@PathVariable TipoSeguro tipo) {
        long cantidad = pacienteService.contarPacientesPorTipoSeguro(tipo);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("tipoSeguro", tipo.name());
        data.put("etiqueta", tipo.getEtiqueta());
        data.put("cantidadPacientes", cantidad);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/api/${api.version}/seguros")
    public ResponseEntity<ApiResponse<Seguro>> crearSeguro(@RequestParam TipoSeguro tipo) {
        Seguro seguro = pacienteService.registrarSeguro(tipo);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(seguro));
    }

    // =======================
    // CITAS
    // =======================

    @GetMapping("/api/${api.version}/citas")
    public ResponseEntity<ApiResponse<List<Cita>>> listarCitas(
            @RequestParam(required = false) EstadoCita estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(required = false) MotivoCita motivo) {
        List<Cita> data = pacienteService.buscarCitasConFiltrosCompuestos(estado, desde, hasta, motivo);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/api/${api.version}/citas/paciente/{dniPaciente}")
    public ResponseEntity<ApiResponse<List<Cita>>> buscarCitasDePaciente(@PathVariable String dniPaciente) {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.buscarCitasDePaciente(dniPaciente)));
    }

    @GetMapping("/api/${api.version}/citas/motivo")
    public ResponseEntity<ApiResponse<List<Cita>>> buscarCitasPorMotivo(@RequestParam MotivoCita motivo) {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.buscarCitasPorMotivo(motivo)));
    }

    @GetMapping("/api/${api.version}/citas/rango")
    public ResponseEntity<ApiResponse<List<Cita>>> buscarCitasPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.buscarCitasPorRangoFechas(desde, hasta)));
    }

    @GetMapping("/api/${api.version}/citas/orden/{numeroOrden}")
    public ResponseEntity<ApiResponse<Cita>> buscarCitaPorOrden(@PathVariable Integer numeroOrden) {
        Cita cita = pacienteService.buscarCitaPorNumeroOrden(numeroOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "numeroOrden", numeroOrden));
        return ResponseEntity.ok(ApiResponse.success(cita));
    }

    @GetMapping("/api/${api.version}/citas/resumen/estado")
    public ResponseEntity<ApiResponse<Map<EstadoCita, Long>>> resumenCitasPorEstado() {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.resumenCitasPorEstado()));
    }

    @GetMapping("/api/${api.version}/citas/resumen/motivo")
    public ResponseEntity<ApiResponse<Map<MotivoCita, Long>>> resumenCitasPorMotivo() {
        return ResponseEntity.ok(ApiResponse.success(pacienteService.resumenCitasPorMotivo()));
    }

    @PutMapping("/api/${api.version}/citas/{numeroOrden}/estado")
    public ResponseEntity<ApiResponse<Cita>> cambiarEstadoCita(
            @PathVariable Integer numeroOrden,
            @RequestParam EstadoCita nuevoEstado) {
        Cita actualizada = pacienteService.cambiarEstadoCita(numeroOrden, nuevoEstado);
        return ResponseEntity.ok(ApiResponse.success(actualizada));
    }
}
