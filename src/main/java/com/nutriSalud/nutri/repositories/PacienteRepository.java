package com.nutriSalud.nutri.repositories;

import com.nutriSalud.nutri.models.EstadoPaciente;
import com.nutriSalud.nutri.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, String> {

    Optional<Paciente> findByDni(String dni);

    List<Paciente> findByEstado(EstadoPaciente estado);

    List<Paciente> findByNombreContainingIgnoreCase(String nombre);

    long countByEstado(EstadoPaciente estado);
}
