package com.nutriSalud.nutri.repositories;

import com.nutriSalud.nutri.models.Cita;
import com.nutriSalud.nutri.models.EstadoCita;
import com.nutriSalud.nutri.models.MotivoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT c FROM Cita c " +
            "JOIN FETCH c.paciente p " +
            "WHERE c.estado = :estado " +
            "ORDER BY c.fechaHora ASC")
    List<Cita> buscarPorEstadoConPaciente(@Param("estado") EstadoCita estado);

    @Query("SELECT c FROM Cita c " +
            "JOIN FETCH c.paciente p " +
            "WHERE p.dni = :dniPaciente " +
            "ORDER BY c.fechaHora DESC")
    List<Cita> buscarCitasDePaciente(@Param("dniPaciente") String dniPaciente);

    @Query("SELECT c FROM Cita c " +
            "JOIN FETCH c.paciente p " +
            "WHERE c.fechaHora BETWEEN :inicio AND :fin " +
            "ORDER BY c.fechaHora ASC")
    List<Cita> buscarPorRangoFechasConPaciente(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT c FROM Cita c " +
            "JOIN FETCH c.paciente p " +
            "WHERE c.motivo = :motivo " +
            "ORDER BY c.fechaHora ASC")
    List<Cita> buscarPorMotivo(@Param("motivo") MotivoCita motivo);

    @Query("SELECT c.estado, COUNT(c) FROM Cita c GROUP BY c.estado")
    List<Object[]> contarCitasPorEstado();

    @Query("SELECT c.motivo, COUNT(c) FROM Cita c GROUP BY c.motivo")
    List<Object[]> contarCitasPorMotivo();

    @Query("SELECT c FROM Cita c " +
            "JOIN FETCH c.paciente p " +
            "WHERE c.estado = :estado AND c.fechaHora BETWEEN :inicio AND :fin " +
            "ORDER BY c.fechaHora ASC")
    List<Cita> buscarPorEstadoYRangoFechas(
            @Param("estado") EstadoCita estado,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}
