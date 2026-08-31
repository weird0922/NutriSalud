package com.nutriSalud.nutri.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @Column(name = "dni", nullable = false, length = 15, unique = true)
    private String dni;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechNac;

    @Column(nullable = false)
    private double hemoglobina;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoPaciente estado;

    public Paciente() {}

    public Paciente(String dni, String nombre, LocalDate fechNac, double hemoglobina, EstadoPaciente estado) {
        this.dni = dni;
        this.nombre = nombre;
        this.fechNac = fechNac;
        this.hemoglobina = hemoglobina;
        this.estado = estado;
    }

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

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    @Deprecated(forRemoval = true)
    public String getId() { return dni; }
    @Deprecated(forRemoval = true)
    public void setId(String id) { this.dni = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDate getFechNac() { return fechNac; }
    public void setFechNac(LocalDate fechNac) { this.fechNac = fechNac; }
    public double getHemoglobina() { return hemoglobina; }
    public void setHemoglobina(double hemoglobina) { this.hemoglobina = hemoglobina; }
    public EstadoPaciente getEstado() { return estado; }
    public void setEstado(EstadoPaciente estado) { this.estado = estado; }
}
