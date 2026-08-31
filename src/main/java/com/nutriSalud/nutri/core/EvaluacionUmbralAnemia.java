package com.nutriSalud.nutri.core;

public class EvaluacionUmbralAnemia {

    private static EvaluacionUmbralAnemia instanciaUnica;

    private final double umbralCritico;
    private final double umbralNormalMenores5;
    private final double umbralNormal5a11;
    private final double umbralNormal12mas;

    private EvaluacionUmbralAnemia() {
        this.umbralCritico = 7.0;
        this.umbralNormalMenores5 = 11.0;
        this.umbralNormal5a11 = 11.5;
        this.umbralNormal12mas = 12.0;
    }

    public static synchronized EvaluacionUmbralAnemia getInstancia() {
        if (instanciaUnica == null) {
            instanciaUnica = new EvaluacionUmbralAnemia();
        }
        return instanciaUnica;
    }

    public double obtenerUmbralNormal(int edad) {
        if (edad < 5) {
            return umbralNormalMenores5;
        } else if (edad < 12) {
            return umbralNormal5a11;
        } else {
            return umbralNormal12mas;
        }
    }

    public double getUmbralCritico() {
        return umbralCritico;
    }
}
