package com.nutriSalud.nutri.models;

public enum MotivoCita {
    CONSULTA("Consulta General"),
    SEGUIMIENTO("Seguimiento de Anemia"),
    TRATAMIENTO("Tratamiento y Control");

    private final String etiqueta;

    MotivoCita(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
