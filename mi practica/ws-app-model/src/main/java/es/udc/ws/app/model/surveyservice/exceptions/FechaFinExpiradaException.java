package es.udc.ws.app.model.surveyservice.exceptions;

import java.time.LocalDateTime;

public class FechaFinExpiradaException extends Exception {

    private LocalDateTime fechaFin;

    public FechaFinExpiradaException(LocalDateTime fechaFin) {
        super("No se puede crear una encuesta cuya fecha de finalización (" +
                fechaFin + ") ya ha expirado.");
        this.fechaFin = fechaFin;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }
}