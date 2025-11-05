package es.udc.ws.app.model.respuesta;

import java.time.LocalDateTime;

public class Respuesta {

    private Long respuestaId;
    private Long encuestaId;
    private String emailEmpleado;
    private boolean afirmativa;
    private LocalDateTime fechaRespuesta;

    public Respuesta(Long encuestaId, String emailEmpleado, boolean afirmativa) {
        this.encuestaId = encuestaId;
        this.emailEmpleado = emailEmpleado;
        this.afirmativa = afirmativa;
        this.fechaRespuesta = LocalDateTime.now().withNano(0);
    }

    public Respuesta(Long respuestaId, Long encuestaId, String emailEmpleado,
                     boolean afirmativa, LocalDateTime fechaRespuesta) {
        this.respuestaId = respuestaId;
        this.encuestaId = encuestaId;
        this.emailEmpleado = emailEmpleado;
        this.afirmativa = afirmativa;
        this.fechaRespuesta = fechaRespuesta;
    }

    public Long getRespuestaId() {
        return respuestaId;
    }

    public void setRespuestaId(Long respuestaId) {
        this.respuestaId = respuestaId;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }

    public String getEmailEmpleado() {
        return emailEmpleado;
    }

    public void setEmailEmpleado(String emailEmpleado) {
        this.emailEmpleado = emailEmpleado;
    }

    public boolean isAfirmativa() {
        return afirmativa;
    }

    public void setAfirmativa(boolean afirmativa) {
        this.afirmativa = afirmativa;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
}