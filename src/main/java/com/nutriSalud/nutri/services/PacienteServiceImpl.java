package com.nutriSalud.nutri.services;

import com.nutriSalud.nutri.core.PacienteFactory;
import com.nutriSalud.nutri.models.Paciente;
import com.nutriSalud.nutri.repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class PacienteServiceImpl implements PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    public Paciente registrarNuevoPaciente(String dni, String nombre, LocalDate fechNac, double hemoglobina) {
        Paciente nuevo = PacienteFactory.crearPaciente(dni, nombre, fechNac, hemoglobina);
        return pacienteRepository.save(nuevo);
    }

    @Override
    public List<Paciente> obtenerTodosLosPacientes() {
        return pacienteRepository.findAll();
    }

    @Override
    public Optional<Paciente> buscarPorDni(String dni) {
        return pacienteRepository.findByDni(dni);
    }
}
