package es.udc.ws.app.model.surveyservice.exceptions;

import java.time.LocalDateTime;

public class EncuestaFinalizadaException extends Exception {

    private Long encuestaId;
    private LocalDateTime fechaFin;

    public EncuestaFinalizadaException(Long encuestaId, LocalDateTime fechaFin) {
        super("La encuesta con id=" + encuestaId +
                " ya ha finalizado (fecha fin: " + fechaFin + ")");
        this.encuestaId = encuestaId;
        this.fechaFin = fechaFin;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }
}