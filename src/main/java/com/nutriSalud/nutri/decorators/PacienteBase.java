package com.nutriSalud.nutri.decorators;

import com.nutriSalud.nutri.models.EstadoPaciente;
import com.nutriSalud.nutri.models.Paciente;
import java.time.LocalDate;

public class PacienteBase implements PacienteEvaluable {

    private final Paciente paciente;

    public PacienteBase(Paciente paciente) {
        this.paciente = paciente;
    }

    @Override
    public String getId() { return paciente.getDni(); }

    @Override
    public String getPrimerNombre() { return paciente.getPrimerNombre(); }

    @Override
    public String getApellidoPaterno() { return paciente.getApellidoPaterno(); }

    @Override
    public String getApellidoMaterno() { return paciente.getApellidoMaterno(); }

    @Override
    public String getNombreCompleto() { return paciente.getNombreCompleto(); }

    @Override
    public LocalDate getFechNac() { return paciente.getFechNac(); }

    @Override
    public double getHemoglobina() { return paciente.getHemoglobina(); }

    @Override
    public EstadoPaciente getEstado() { return paciente.getEstado(); }

    @Override
    public int getEdad() { return paciente.getEdad(); }

    @Override
    public String getEstadoSalud() { return paciente.getEstadoSalud(); }

    @Override
    public String getInformacionAdicional() {
        return "";
    }

    public Paciente getPacienteOriginal() {
        return paciente;
    }
}
