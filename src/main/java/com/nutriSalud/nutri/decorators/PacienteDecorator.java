package com.nutriSalud.nutri.decorators;

import com.nutriSalud.nutri.models.EstadoPaciente;
import java.time.LocalDate;

public abstract class PacienteDecorator implements PacienteEvaluable {

    protected final PacienteEvaluable pacienteDecorado;

    protected PacienteDecorator(PacienteEvaluable pacienteDecorado) {
        this.pacienteDecorado = pacienteDecorado;
    }

    @Override
    public String getId() {
        return pacienteDecorado.getId();
    }

    @Override
    public String getNombre() {
        return pacienteDecorado.getNombre();
    }

    @Override
    public LocalDate getFechNac() {
        return pacienteDecorado.getFechNac();
    }

    @Override
    public double getHemoglobina() {
        return pacienteDecorado.getHemoglobina();
    }

    @Override
    public EstadoPaciente getEstado() {
        return pacienteDecorado.getEstado();
    }

    @Override
    public int getEdad() {
        return pacienteDecorado.getEdad();
    }

    @Override
    public String getEstadoSalud() {
        return pacienteDecorado.getEstadoSalud();
    }
}
