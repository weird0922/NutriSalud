package com.nutriSalud.nutri.decorators;

public class AlertaDecorator extends PacienteDecorator {

    private final String mensajeAlerta;

    public AlertaDecorator(PacienteEvaluable pacienteDecorado) {
        super(pacienteDecorado);
        this.mensajeAlerta = generarAlertaSegunEstado();
    }

    private String generarAlertaSegunEstado() {
        return switch (pacienteDecorado.getEstado()) {
            case CRITICO -> "⚠ ALERTA: Paciente crítico - notificar a médico de guardia inmediatamente";
            case MEDIO -> "⚠ Seguimiento: Programar control en 15 días";
            case BUENO -> "✓ Sin alertas - Próximo control anual";
        };
    }

    @Override
    public String getInformacionAdicional() {
        return pacienteDecorado.getInformacionAdicional() + "\n" + mensajeAlerta;
    }

    public String getMensajeAlerta() {
        return mensajeAlerta;
    }
}
