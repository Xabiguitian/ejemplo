package es.udc.ws.app.model.surveyservice;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.respuesta.Respuesta;
import es.udc.ws.app.model.surveyservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.surveyservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.surveyservice.exceptions.FechaFinExpiradaException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.util.List;

public interface SurveyService {


    public Encuesta crearEncuesta(Encuesta encuesta)
            throws InputValidationException, FechaFinExpiradaException;


    public Encuesta buscarEncuestaPorId(Long encuestaId)
            throws InstanceNotFoundException;

    public List<Encuesta> buscarEncuestas(String keywords);


    public Respuesta responderEncuesta(Long encuestaId, String email, boolean respuestaPositiva)
            throws InstanceNotFoundException, EncuestaFinalizadaException, EncuestaCanceladaException, InputValidationException;


    public Encuesta cancelarEncuesta(Long encuestaId)
            throws InstanceNotFoundException, EncuestaFinalizadaException, EncuestaCanceladaException;

    public List<Respuesta> obtenerRespuestas(Long encuestaId, boolean soloAfirmativas)
            throws InstanceNotFoundException;
}
