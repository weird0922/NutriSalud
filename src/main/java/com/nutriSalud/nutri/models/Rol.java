package com.nutriSalud.nutri.models;

public enum Rol {
    CIUDADANO("Ciudadano", "Usuario final del sistema, puede ver su propia informacion clinica."),
    PERSONAL_SALUD("Personal de Salud", "Medicos y enfermeria; pueden registrar y editar datos clinicos.");

    private final String etiqueta;
    private final String descripcion;

    Rol(String etiqueta, String descripcion) {
        this.etiqueta = etiqueta;
        this.descripcion = descripcion;
    }

    public String getEtiqueta() { return etiqueta; }
    public String getDescripcion() { return descripcion; }
}
