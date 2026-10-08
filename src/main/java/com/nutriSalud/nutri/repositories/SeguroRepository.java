package com.nutriSalud.nutri.repositories;

import com.nutriSalud.nutri.models.Seguro;
import com.nutriSalud.nutri.models.TipoSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeguroRepository extends JpaRepository<Seguro, Long> {

    Optional<Seguro> findByTipo(TipoSeguro tipo);

    @Query("SELECT s FROM Seguro s WHERE s.tipo = :tipo")
    Optional<Seguro> buscarPorTipoJPQL(@Param("tipo") TipoSeguro tipo);

    @Query("SELECT s FROM Seguro s ORDER BY s.tipo")
    List<Seguro> listarTodosOrdenadosPorTipo();

    @Query("SELECT COUNT(p) FROM Paciente p WHERE p.seguro.tipo = :tipo")
    long contarPacientesPorTipoSeguro(@Param("tipo") TipoSeguro tipo);
}
