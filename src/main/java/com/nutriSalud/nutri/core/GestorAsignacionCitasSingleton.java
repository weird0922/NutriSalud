package com.nutriSalud.nutri.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class GestorAsignacionCitasSingleton {

    private static GestorAsignacionCitasSingleton instanciaUnica;

    private final LocalTime horaInicioAtencion;
    private final int duracionMinutosPorCita;
    private final int maxCitasPorDia;

    private int ultimoNumeroOrden;
    private LocalDate ultimoDiaAsignado;
    private LocalDateTime ultimaFechaHoraAsignada;

    private GestorAsignacionCitasSingleton() {
        this.horaInicioAtencion = LocalTime.of(7, 0);
        this.duracionMinutosPorCita = 15;
        this.maxCitasPorDia = 40;
        this.ultimoNumeroOrden = 0;
        this.ultimoDiaAsignado = LocalDate.now().minusDays(1);
        this.ultimaFechaHoraAsignada = null;
    }

    public static synchronized GestorAsignacionCitasSingleton getInstancia() {
        if (instanciaUnica == null) {
            instanciaUnica = new GestorAsignacionCitasSingleton();
        }
        return instanciaUnica;
    }

    public synchronized int siguienteNumeroOrden() {
        ultimoNumeroOrden = ultimoNumeroOrden + 1;
        return ultimoNumeroOrden;
    }

    public synchronized LocalDateTime siguienteFechaHora() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime propuesta;

        if (ultimaFechaHoraAsignada == null || !ultimoDiaAsignado.equals(hoy)) {
            ultimoDiaAsignado = hoy;
            propuesta = LocalDateTime.of(hoy, horaInicioAtencion);
        } else {
            int citasHoy = contarCitasDelDia();
            if (citasHoy >= maxCitasPorDia) {
                ultimoDiaAsignado = hoy.plusDays(1);
                propuesta = LocalDateTime.of(ultimoDiaAsignado, horaInicioAtencion);
                resetearNumeroOrdenParaNuevoDia();
            } else {
                propuesta = ultimaFechaHoraAsignada.plusMinutes(duracionMinutosPorCita);
            }
        }

        ultimaFechaHoraAsignada = propuesta;
        return propuesta;
    }

    private int contarCitasDelDia() {
        LocalTime ultimoSlot = ultimaFechaHoraAsignada.toLocalTime();
        int minutos = ultimoSlot.toSecondOfDay() / 60;
        int minutosInicio = horaInicioAtencion.toSecondOfDay() / 60;
        if (minutos < minutosInicio) return 0;
        return ((minutos - minutosInicio) / duracionMinutosPorCita) + 1;
    }

    private void resetearNumeroOrdenParaNuevoDia() {
        // Mantener global, no reiniciar por día.
    }

    public void sincronizarConBaseDeDatos(int maxOrdenExistente, LocalDateTime ultimaFechaAsignada) {
        if (maxOrdenExistente > this.ultimoNumeroOrden) {
            this.ultimoNumeroOrden = maxOrdenExistente;
        }
        if (ultimaFechaAsignada != null) {
            this.ultimaFechaHoraAsignada = ultimaFechaAsignada;
            this.ultimoDiaAsignado = ultimaFechaAsignada.toLocalDate();
        }
    }

    public int getDuracionMinutosPorCita() {
        return duracionMinutosPorCita;
    }

    public LocalTime getHoraInicioAtencion() {
        return horaInicioAtencion;
    }

    public int getMaxCitasPorDia() {
        return maxCitasPorDia;
    }
}
