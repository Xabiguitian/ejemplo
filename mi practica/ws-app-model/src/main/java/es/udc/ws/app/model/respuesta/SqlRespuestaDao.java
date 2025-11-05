package es.udc.ws.app.model.respuesta;

import java.sql.Connection;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public interface SqlRespuestaDao {

    public Respuesta create(Connection connection, Respuesta respuesta);

    public void update(Connection connection, Respuesta respuesta) throws InstanceNotFoundException;

    public Respuesta findByEmailAndEncuestaId(Connection connection, Long encuestaId, String emailEmpleado);

    public List<Respuesta> findByEncuestaId(Connection connection, Long encuestaId, boolean soloAfirmativas);
}