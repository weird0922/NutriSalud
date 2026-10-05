package com.nutriSalud.nutri.exceptions;

public class EdadNoPermitidaException extends RuntimeException {
    private final String recurso;
    private final String campo;
    private final Object valorRechazado;

    public EdadNoPermitidaException(String recurso, String campo, Object valorRechazado, String mensaje) {
        super(mensaje);
        this.recurso = recurso;
        this.campo = campo;
        this.valorRechazado = valorRechazado;
    }

    public EdadNoPermitidaException(int edadCalculada) {
        super("Solo se atiende pacientes menores de 18 años. Edad ingresada: " + edadCalculada + " años");
        this.recurso = "Paciente";
        this.campo = "fechNac";
        this.valorRechazado = edadCalculada;
    }

    public String getRecurso() { return recurso; }
    public String getCampo() { return campo; }
    public Object getValorRechazado() { return valorRechazado; }
}
