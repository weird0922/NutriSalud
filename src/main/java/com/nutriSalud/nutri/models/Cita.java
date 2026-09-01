package com.nutriSalud.nutri.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
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

    public Cita() {}

    public Cita(Integer numeroOrden, LocalDateTime fechaHora, MotivoCita motivo, Paciente paciente) {
        this.numeroOrden = numeroOrden;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = EstadoCita.PENDIENTE;
        this.paciente = paciente;
    }

    public Integer getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(Integer numeroOrden) { this.numeroOrden = numeroOrden; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }

    public MotivoCita getMotivo() { return motivo; }
    public void setMotivo(MotivoCita motivo) { this.motivo = motivo; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public String getPacienteDni() {
        return paciente != null ? paciente.getDni() : null;
    }
}
