package com.nutriSalud.nutri.controllers;

import com.nutriSalud.nutri.models.Paciente;
import com.nutriSalud.nutri.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public List<Paciente> listarPacientes() {
        return pacienteService.obtenerTodosLosPacientes();
    }

    @GetMapping("/{dni}")
    public ResponseEntity<Paciente> buscarPorDni(@PathVariable String dni) {
        return pacienteService.buscarPorDni(dni)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Paciente> agregarPaciente(@RequestBody Paciente datosEntrada) {
        if (datosEntrada.getDni() == null || datosEntrada.getDni().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Paciente registrado = pacienteService.registrarNuevoPaciente(
                datosEntrada.getDni(),
                datosEntrada.getNombre(),
                datosEntrada.getFechNac(),
                datosEntrada.getHemoglobina()
        );
        return ResponseEntity.ok(registrado);
    }
}
