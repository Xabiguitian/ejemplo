package es.udc.ws.app.model.encuesta;

import java.time.LocalDateTime;

public class Encuesta {

    private Long encuestaId;
    private String pregunta;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaFin;
    private long respuestasPositivas;
    private long respuestasNegativas;
    private boolean cancelada;

    public Encuesta(String pregunta, LocalDateTime fechaFin) {
        this.pregunta = pregunta;
        this.fechaCreacion = LocalDateTime.now().withNano(0);
        this.fechaFin = fechaFin;
        this.respuestasPositivas = 0;
        this.respuestasNegativas = 0;
        this.cancelada = false;
    }

    public Encuesta(Long encuestaId, String pregunta, LocalDateTime fechaCreacion,
                    LocalDateTime fechaFin, long respuestasPositivas,
                    long respuestasNegativas, boolean cancelada) {
        this.encuestaId = encuestaId;
        this.pregunta = pregunta;
        this.fechaCreacion = fechaCreacion;
        this.fechaFin = fechaFin;
        this.respuestasPositivas = respuestasPositivas;
        this.respuestasNegativas = respuestasNegativas;
        this.cancelada = cancelada;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public long getRespuestasPositivas() {
        return respuestasPositivas;
    }

    public void setRespuestasPositivas(long respuestasPositivas) {
        this.respuestasPositivas = respuestasPositivas;
    }

    public long getRespuestasNegativas() {
        return respuestasNegativas;
    }

    public void setRespuestasNegativas(long respuestasNegativas) {
        this.respuestasNegativas = respuestasNegativas;
    }

    public boolean isCancelada() {
        return cancelada;
    }

    public void setCancelada(boolean cancelada) {
        this.cancelada = cancelada;
    }

    public long getTotalRespuestas() {
        return this.respuestasPositivas + this.respuestasNegativas;
    }
}