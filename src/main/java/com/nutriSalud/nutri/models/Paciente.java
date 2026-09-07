package com.nutriSalud.nutri.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "pacientes")
@NamedQueries({
    @NamedQuery(
            name = "Paciente.buscarPorDni",
            query = "SELECT p FROM Paciente p WHERE p.dni = :dni"
    ),
    @NamedQuery(
            name = "Paciente.buscarPorApellido",
            query = "SELECT p FROM Paciente p " +
                    "WHERE LOWER(p.apellidoPaterno) LIKE LOWER(CONCAT('%', :apellido, '%')) " +
                    "   OR LOWER(p.apellidoMaterno) LIKE LOWER(CONCAT('%', :apellido, '%')) " +
                    "ORDER BY p.apellidoPaterno, p.apellidoMaterno, p.primerNombre"
    ),
    @NamedQuery(
            name = "Paciente.listarTodosActivos",
            query = "SELECT p FROM Paciente p ORDER BY p.apellidoPaterno, p.primerNombre"
    ),
    @NamedQuery(
            name = "Paciente.buscarPorEstadoSalud",
            query = "SELECT p FROM Paciente p WHERE p.estado = :estado ORDER BY p.apellidoPaterno, p.primerNombre"
    )
})
@SQLRestriction("activo = 1")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Paciente {

    @Id
    @Column(name = "dni", nullable = false, length = 15, unique = true)
    private String dni;

    @Column(name = "primer_nombre", nullable = false, length = 80)
    private String primerNombre;

    @Column(name = "apellido_paterno", nullable = false, length = 80)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = true, length = 80)
    private String apellidoMaterno;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechNac;

    @Column(nullable = false)
    private double hemoglobina;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoPaciente estado;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "seguro_id")
    private Seguro seguro;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public String getEstadoSalud() {
        if (estado == null) return "Sin evaluar";
        return estado.getEtiqueta();
    }

    public int getEdad() {
        if (this.fechNac != null) {
            return Period.between(this.fechNac, LocalDate.now()).getYears();
        }
        return 0;
    }

    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder();
        if (primerNombre != null) sb.append(primerNombre);
        if (apellidoPaterno != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(apellidoPaterno);
        }
        if (apellidoMaterno != null && !apellidoMaterno.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(apellidoMaterno);
        }
        return sb.toString();
    }

    @Deprecated(forRemoval = true)
    public String getId() { return dni; }

    @Deprecated(forRemoval = true)
    public void setId(String id) { this.dni = id; }
}
