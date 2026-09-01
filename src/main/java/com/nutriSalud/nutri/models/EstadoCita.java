package com.nutriSalud.nutri.models;

public enum EstadoCita {
    PENDIENTE("Pendiente", "Cita programada, aún no atendida"),
    EN_PROCESO("En Proceso", "Cita siendo atendida actualmente"),
    COMPLETADA("Completada", "Cita atendida y finalizada"),
    CANCELADA("Cancelada", "Cita cancelada");

    private final String etiqueta;
    private final String descripcion;

    EstadoCita(String etiqueta, String descripcion) {
        this.etiqueta = etiqueta;
        this.descripcion = descripcion;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
