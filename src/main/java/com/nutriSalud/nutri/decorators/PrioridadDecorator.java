package com.nutriSalud.nutri.decorators;

public class PrioridadDecorator extends PacienteDecorator {

    private final String nivelPrioridad;
    private final int ordenAtencion;

    public PrioridadDecorator(PacienteEvaluable pacienteDecorado) {
        super(pacienteDecorado);
        this.nivelPrioridad = calcularNivelPrioridad();
        this.ordenAtencion = calcularOrdenAtencion();
    }

    private String calcularNivelPrioridad() {
        return switch (pacienteDecorado.getEstado()) {
            case CRITICO -> "ALTA (1)";
            case MEDIO -> "MEDIA (2)";
            case BUENO -> "BAJA (3)";
        };
    }

    private int calcularOrdenAtencion() {
        return switch (pacienteDecorado.getEstado()) {
            case CRITICO -> 1;
            case MEDIO -> 2;
            case BUENO -> 3;
        };
    }

    @Override
    public String getInformacionAdicional() {
        return pacienteDecorado.getInformacionAdicional() +
               "\n" +
               "Prioridad de atención: " + nivelPrioridad +
               "\n" +
               "Orden de atención: " + ordenAtencion;
    }

    public String getNivelPrioridad() {
        return nivelPrioridad;
    }

    public int getOrdenAtencion() {
        return ordenAtencion;
    }
}
