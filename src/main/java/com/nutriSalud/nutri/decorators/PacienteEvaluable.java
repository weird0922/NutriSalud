package com.nutriSalud.nutri.decorators;

import com.nutriSalud.nutri.models.EstadoPaciente;
import java.time.LocalDate;

public interface PacienteEvaluable {

    String getId();
    String getPrimerNombre();
    String getApellidoPaterno();
    String getApellidoMaterno();
    String getNombreCompleto();
    LocalDate getFechNac();
    double getHemoglobina();
    EstadoPaciente getEstado();
    int getEdad();
    String getEstadoSalud();

    String getInformacionAdicional();
}
