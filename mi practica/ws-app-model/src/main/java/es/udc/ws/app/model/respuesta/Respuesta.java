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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((respuestaId == null) ? 0 : respuestaId.hashCode());
        result = prime * result + ((encuestaId == null) ? 0 : encuestaId.hashCode());
        result = prime * result + ((emailEmpleado == null) ? 0 : emailEmpleado.hashCode());
        result = prime * result + (afirmativa ? 1231 : 1237);
        result = prime * result + ((fechaRespuesta == null) ? 0 : fechaRespuesta.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Respuesta other = (Respuesta) obj;
        return respuestaId != null ? respuestaId.equals(other.respuestaId) : other.respuestaId == null;
    }

}