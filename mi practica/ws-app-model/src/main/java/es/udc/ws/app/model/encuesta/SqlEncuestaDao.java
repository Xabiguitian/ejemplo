package es.udc.ws.app.model.encuesta;

import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.sql.Connection;
import java.util.List;

public interface SqlEncuestaDao {

    public Encuesta create(Connection connection, Encuesta encuesta);

    public Encuesta find(Connection connection, Long encuestaId)
            throws InstanceNotFoundException;


    public List<Encuesta> findByKeywords(String keywords, boolean soloNoFinalizadas);

    public void update(Connection connection, Encuesta encuesta)
            throws InstanceNotFoundException;

    public void remove(Connection connection, Long encuestaId)
            throws InstanceNotFoundException;
}
