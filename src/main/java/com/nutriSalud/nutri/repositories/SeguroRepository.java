package com.nutriSalud.nutri.repositories;

import com.nutriSalud.nutri.models.Seguro;
import com.nutriSalud.nutri.models.TipoSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeguroRepository extends JpaRepository<Seguro, Long> {

    Optional<Seguro> findByTipo(TipoSeguro tipo);
}
