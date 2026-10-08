package com.nutriSalud.nutri.repositories;

import com.nutriSalud.nutri.models.EstadoPaciente;
import com.nutriSalud.nutri.models.Paciente;
import com.nutriSalud.nutri.models.TipoSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, String> {

    @Query(name = "Paciente.buscarPorDni")
    Optional<Paciente> buscarPorDniConNamedQuery(@Param("dni") String dni);

    @Query(name = "Paciente.buscarPorApellido")
    List<Paciente> buscarPorApellidoConNamedQuery(@Param("apellido") String apellido);

    @Query(name = "Paciente.listarTodosActivos")
    List<Paciente> listarTodosActivosConNamedQuery();

    @Query(name = "Paciente.buscarPorEstadoSalud")
    List<Paciente> buscarPorEstadoSaludConNamedQuery(@Param("estado") EstadoPaciente estado);

    Optional<Paciente> findByDni(String dni);

    @Query("SELECT p FROM Paciente p WHERE p.dni = :dni")
    Optional<Paciente> findByDniIncluyendoInactivos(@Param("dni") String dni);

    List<Paciente> findByEstado(EstadoPaciente estado);

    @Query("SELECT p FROM Paciente p WHERE p.estado = :estado ORDER BY p.apellidoPaterno, p.apellidoMaterno, p.primerNombre")
    List<Paciente> buscarPorEstadoOrdenadosPorNombreCompleto(@Param("estado") EstadoPaciente estado);

    @Query("SELECT p FROM Paciente p " +
            "WHERE LOWER(p.apellidoPaterno) LIKE LOWER(CONCAT('%', :apellido, '%')) " +
            "   OR LOWER(p.apellidoMaterno) LIKE LOWER(CONCAT('%', :apellido, '%')) " +
            "ORDER BY p.apellidoPaterno, p.apellidoMaterno, p.primerNombre")
    List<Paciente> buscarPorApellidoConteniendo(@Param("apellido") String apellido);

    @Query("SELECT p FROM Paciente p " +
            "WHERE LOWER(p.primerNombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
            "   OR LOWER(p.apellidoPaterno) LIKE LOWER(CONCAT('%', :texto, '%')) " +
            "   OR LOWER(p.apellidoMaterno) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Paciente> buscarPorTextoEnNombresJPQL(@Param("texto") String texto);

    default List<Paciente> buscarPorTextoEnNombres(String texto) {
        return buscarPorTextoEnNombresJPQL(texto);
    }

    @Query("SELECT p FROM Paciente p " +
            "WHERE p.hemoglobina BETWEEN :min AND :max " +
            "ORDER BY p.hemoglobina ASC")
    List<Paciente> buscarPorRangoHemoglobina(@Param("min") double min, @Param("max") double max);

    @Query("SELECT p FROM Paciente p " +
            "JOIN FETCH p.seguro s " +
            "WHERE s.tipo = :tipoSeguro " +
            "ORDER BY p.apellidoPaterno, p.primerNombre")
    List<Paciente> buscarPorTipoSeguro(@Param("tipoSeguro") TipoSeguro tipoSeguro);

    @Query("SELECT p FROM Paciente p " +
            "WHERE p.fechNac BETWEEN :inicio AND :fin " +
            "ORDER BY p.fechNac ASC")
    List<Paciente> buscarPorRangoFechaNacimiento(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    @Query("SELECT p FROM Paciente p WHERE p.hemoglobina < :umbral ORDER BY p.hemoglobina ASC")
    List<Paciente> buscarConHemoglobinaInferiorA(@Param("umbral") double umbral);

    @Modifying
    @Query("UPDATE Paciente p SET p.activo = false WHERE p.dni = :dni")
    int desactivarPorDni(@Param("dni") String dni);

    @Modifying
    @Query("UPDATE Paciente p SET p.activo = true WHERE p.dni = :dni")
    int reactivarPorDni(@Param("dni") String dni);

    @Query("SELECT COUNT(p) FROM Paciente p WHERE p.activo = false")
    long contarInactivos();

    @Query("SELECT p FROM Paciente p WHERE p.activo = false ORDER BY p.apellidoPaterno, p.primerNombre")
    List<Paciente> listarTodosInactivos();

    @Query("SELECT p.estado, COUNT(p) FROM Paciente p GROUP BY p.estado")
    List<Object[]> contarPacientesPorEstado();

    @Query("SELECT AVG(p.hemoglobina) FROM Paciente p WHERE p.fechNac BETWEEN :inicio AND :fin")
    Optional<Double> promedioHemoglobinaPorRangoEdad(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    long countByEstado(EstadoPaciente estado);
}
