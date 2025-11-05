package es.udc.ws.app.model.surveyservice.exceptions;

public class EncuestaCanceladaException extends Exception {

    private Long encuestaId;

    public EncuestaCanceladaException(Long encuestaId) {
        super("La encuesta con id=" + encuestaId + " ya ha sido cancelada");
        this.encuestaId = encuestaId;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }
}