package com.nutriSalud.nutri.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cita {

    @Id
    @Column(name = "numero_orden")
    private Integer numeroOrden;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCita estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo", nullable = false, length = 20)
    private MotivoCita motivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_dni", referencedColumnName = "dni")
    private Paciente paciente;

    public Cita(Integer numeroOrden, LocalDateTime fechaHora, MotivoCita motivo, Paciente paciente) {
        this.numeroOrden = numeroOrden;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = EstadoCita.PENDIENTE;
        this.paciente = paciente;
    }

    public String getPacienteDni() {
        return paciente != null ? paciente.getDni() : null;
    }
}
