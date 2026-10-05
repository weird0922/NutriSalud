package com.nutriSalud.nutri.repositories;

import com.nutriSalud.nutri.models.Rol;
import com.nutriSalud.nutri.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.username = :username AND u.activo = true")
    Optional<Usuario> findActivoPorUsername(@Param("username") String username);

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol ORDER BY u.username")
    List<Usuario> listarPorRol(@Param("rol") Rol rol);
}
