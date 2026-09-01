package com.nutriSalud.nutri.repositories;

import com.nutriSalud.nutri.models.Cita;
import com.nutriSalud.nutri.models.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    List<Cita> findByEstado(EstadoCita estado);

    List<Cita> findByPacienteDni(String dni);

    List<Cita> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    Optional<Cita> findTopByOrderByNumeroOrdenDesc();

    Optional<Cita> findTopByOrderByFechaHoraDesc();

    @Query("SELECT MAX(c.numeroOrden) FROM Cita c")
    Optional<Integer> findMaxNumeroOrden();
}
