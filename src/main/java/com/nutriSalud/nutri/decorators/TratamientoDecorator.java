package com.nutriSalud.nutri.decorators;

public class TratamientoDecorator extends PacienteDecorator {

    private final String recomendacion;
    private final String suplementoSugerido;

    public TratamientoDecorator(PacienteEvaluable pacienteDecorado) {
        super(pacienteDecorado);
        this.recomendacion = generarRecomendacion();
        this.suplementoSugerido = generarSuplemento();
    }

    private String generarRecomendacion() {
        return switch (pacienteDecorado.getEstado()) {
            case CRITICO -> "Dieta rica en hierro + transfusión si médico lo indica";
            case MEDIO -> "Dieta rica en hierro + ácido fólico + vitamina C";
            case BUENO -> "Mantener alimentación variada y completa";
        };
    }

    private String generarSuplemento() {
        return switch (pacienteDecorado.getEstado()) {
            case CRITICO -> "Sulfato ferroso - Dosis según médico";
            case MEDIO -> "Sulfato ferroso 2-3 mg/kg/día";
            case BUENO -> "Sin suplementos, solo multivitamínico si el médico lo sugiere";
        };
    }

    @Override
    public String getInformacionAdicional() {
        return pacienteDecorado.getInformacionAdicional() +
               "\n" +
               "Recomendación: " + recomendacion +
               "\n" +
               "Suplemento: " + suplementoSugerido;
    }

    public String getRecomendacion() {
        return recomendacion;
    }

    public String getSuplementoSugerido() {
        return suplementoSugerido;
    }
}
