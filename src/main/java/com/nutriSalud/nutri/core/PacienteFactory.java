package com.nutriSalud.nutri.core;

import com.nutriSalud.nutri.models.EstadoPaciente;
import com.nutriSalud.nutri.models.Paciente;
import java.time.LocalDate;
import java.time.Period;

public class PacienteFactory {

    public static Paciente crearPaciente(String dni, String primerNombre, String apellidoPaterno, String apellidoMaterno, LocalDate fechNac, double hemoglobina) {
        EstadoPaciente estado = evaluarEstado(fechNac, hemoglobina);
        Paciente paciente = new Paciente();
        paciente.setDni(dni);
        paciente.setPrimerNombre(primerNombre);
        paciente.setApellidoPaterno(apellidoPaterno);
        paciente.setApellidoMaterno(apellidoMaterno);
        paciente.setFechNac(fechNac);
        paciente.setHemoglobina(hemoglobina);
        paciente.setEstado(estado);
        paciente.setSeguro(null);
        paciente.setActivo(true);
        return paciente;
    }

    private static EstadoPaciente evaluarEstado(LocalDate fechNac, double hemoglobina) {
        EvaluacionUmbralAnemia gestor = EvaluacionUmbralAnemia.getInstancia();
        int edad = calcularEdad(fechNac);
        double umbralNormal = gestor.obtenerUmbralNormal(edad);

        if (hemoglobina < gestor.getUmbralCritico()) {
            return EstadoPaciente.CRITICO;
        } else if (hemoglobina < umbralNormal) {
            return EstadoPaciente.MEDIO;
        } else {
            return EstadoPaciente.BUENO;
        }
    }

    private static int calcularEdad(LocalDate fechNac) {
        if (fechNac == null) return 5;
        return Period.between(fechNac, LocalDate.now()).getYears();
    }
}
