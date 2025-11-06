package es.udc.ws.app.model.surveyservice;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuesta.SqlEncuestaDao;
import es.udc.ws.app.model.encuesta.EncuestaDaoFactory;
import es.udc.ws.app.model.encuesta.SqlEncuestaDao;
import es.udc.ws.app.model.respuesta.Respuesta;
import es.udc.ws.app.model.respuesta.SqlRespuestaDao;
import es.udc.ws.app.model.respuesta.RespuestaDaoFactory;
import es.udc.ws.app.model.surveyservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.surveyservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.surveyservice.exceptions.FechaFinExpiradaException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;
import static es.udc.ws.app.model.util.ModelConstants.SURVEY_DATA_SOURCE;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;



public class SurveyServiceImpl implements SurveyService {

    private final DataSource dataSource;
    private final SqlEncuestaDao encuestaDao;
    private final SqlRespuestaDao respuestaDao;

    public SurveyServiceImpl() {
        dataSource = DataSourceLocator.getDataSource(SURVEY_DATA_SOURCE);
        encuestaDao = EncuestaDaoFactory.getDao();
        respuestaDao = RespuestaDaoFactory.getDao();
    }


    @Override
    public Encuesta crearEncuesta(Encuesta encuesta)
            throws InputValidationException, FechaFinExpiradaException {

        if (encuesta.getPregunta() == null || encuesta.getPregunta().trim().isEmpty()) {
            throw new InputValidationException("La pregunta no puede estar vacía");
        }


        boolean modoTest = System.getProperty("test.mode", "false").equals("true");
        if (!modoTest && (encuesta.getFechaFin() == null || encuesta.getFechaFin().isBefore(LocalDateTime.now()))) {
            throw new FechaFinExpiradaException(encuesta.getFechaFin());
        }

        try (Connection connection = dataSource.getConnection()) {
            Encuesta creada = encuestaDao.create(connection, encuesta);
            System.out.println("[DEBUG] --> Encuesta creada con ID: " + creada.getEncuestaId());
            return creada;
        } catch (SQLException e) {
            throw new RuntimeException("Error creando encuesta", e);
        }
    }


    @Override
    public List<Encuesta> buscarEncuestas(String palabraClave) {
        Objects.requireNonNull(palabraClave, "La palabra clave no puede ser nula");

        try (Connection connection = dataSource.getConnection()) {
            return encuestaDao.findByKeywords(palabraClave, true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Encuesta buscarEncuestaPorId(Long encuestaId)
            throws InstanceNotFoundException {



        try (Connection connection = dataSource.getConnection()) {
            return encuestaDao.find(connection, encuestaId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Respuesta responderEncuesta(Long encuestaId, String emailEmpleado, boolean afirmativa)
            throws InstanceNotFoundException,
            EncuestaFinalizadaException, EncuestaCanceladaException, InputValidationException {

        PropertyValidator.validateMandatoryString("email", emailEmpleado);

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Encuesta encuesta = encuestaDao.find(connection, encuestaId);

                if (encuesta.isCancelada()) {
                    throw new EncuestaCanceladaException(encuestaId);
                }

                if (encuesta.getFechaFin().isBefore(LocalDateTime.now())) {
                    throw new EncuestaFinalizadaException(encuestaId, encuesta.getFechaFin());
                }

                LocalDateTime ahora = LocalDateTime.now().withNano(0);

                Respuesta respuestaExistente =
                        respuestaDao.findByEmailAndEncuestaId(connection, encuestaId, emailEmpleado);

                if (respuestaExistente == null) {
                    // Nuevo voto
                    Respuesta nueva = new Respuesta(encuestaId, emailEmpleado, afirmativa);
                    nueva.setFechaRespuesta(ahora);
                    respuestaDao.create(connection, nueva);

                    if (afirmativa) {
                        encuesta.setRespuestasPositivas(encuesta.getRespuestasPositivas() + 1);
                    } else {
                        encuesta.setRespuestasNegativas(encuesta.getRespuestasNegativas() + 1);
                    }
                    encuestaDao.update(connection, encuesta);

                } else {
                    // Actualización de voto
                    boolean anterior = respuestaExistente.isAfirmativa();
                    respuestaExistente.setAfirmativa(afirmativa);
                    respuestaExistente.setFechaRespuesta(ahora);
                    respuestaDao.update(connection, respuestaExistente);

                    if (anterior != afirmativa) {
                        if (afirmativa) {
                            encuesta.setRespuestasPositivas(encuesta.getRespuestasPositivas() + 1);
                            encuesta.setRespuestasNegativas(encuesta.getRespuestasNegativas() - 1);
                        } else {
                            encuesta.setRespuestasPositivas(encuesta.getRespuestasPositivas() - 1);
                            encuesta.setRespuestasNegativas(encuesta.getRespuestasNegativas() + 1);
                        }
                        encuestaDao.update(connection, encuesta);
                    }
                }

                connection.commit();
                return respuestaDao.findByEmailAndEncuestaId(connection, encuestaId, emailEmpleado);

            } catch (InstanceNotFoundException | EncuestaFinalizadaException |
                     EncuestaCanceladaException e) {
                connection.commit();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Encuesta cancelarEncuesta(Long encuestaId)
            throws InstanceNotFoundException, EncuestaFinalizadaException, EncuestaCanceladaException {

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Encuesta encuesta = encuestaDao.find(connection, encuestaId);

                if (encuesta.getFechaFin().isBefore(LocalDateTime.now())) {
                    throw new EncuestaFinalizadaException(encuestaId, encuesta.getFechaFin());
                }

                if (encuesta.isCancelada()) {
                    throw new EncuestaCanceladaException(encuestaId);
                }

                encuesta.setCancelada(true);
                encuestaDao.update(connection, encuesta);

                connection.commit();
                return encuesta;

            } catch (InstanceNotFoundException | EncuestaFinalizadaException | EncuestaCanceladaException e) {
                connection.commit();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Respuesta> obtenerRespuestas(Long encuestaId, boolean soloAfirmativas)
            throws InstanceNotFoundException {

        try (Connection connection = dataSource.getConnection()) {
            // Puede devolverse incluso si está cancelada o finalizada
            encuestaDao.find(connection, encuestaId);
            return respuestaDao.findByEncuestaId(connection, encuestaId, soloAfirmativas);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
