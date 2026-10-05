package com.nutriSalud.nutri.models;

public enum TipoSeguro {
    SIS("SIS", "Seguro Integral de Salud"),
    ESSALUD("EsSalud", "Seguro Social de Salud"),
    EPS("EPS", "Entidad Prestadora de Salud Privada");

    private final String abreviatura;
    private final String nombreCompleto;

    TipoSeguro(String abreviatura, String nombreCompleto) {
        this.abreviatura = abreviatura;
        this.nombreCompleto = nombreCompleto;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getEtiqueta() {
        return abreviatura + " - " + nombreCompleto;
    }
}
