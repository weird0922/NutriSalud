package com.nutriSalud.nutri.models;

public enum EstadoPaciente {
    CRITICO("Crítico", "Anemia severa - Requiere atención urgente"),
    MEDIO("Medio", "Con indicios de anemia - Niveles bajos de hemoglobina"),
    BUENO("Bueno", "Niveles normales de hemoglobina");

    private final String etiqueta;
    private final String descripcion;

    EstadoPaciente(String etiqueta, String descripcion) {
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
