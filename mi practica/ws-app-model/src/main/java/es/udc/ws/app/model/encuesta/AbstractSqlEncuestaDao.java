package es.udc.ws.app.model.encuesta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.SURVEY_DATA_SOURCE;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;


public abstract class AbstractSqlEncuestaDao implements SqlEncuestaDao {
    protected AbstractSqlEncuestaDao(){

    }
    public Encuesta find(Long encuestaId) throws InstanceNotFoundException {

        String query = "SELECT pregunta, fechaFin, fechaCreacion, " +
                "respuestasPositivas, respuestasNegativas, cancelada " +
                "FROM Encuesta WHERE encuestaId = ?";

        try (Connection connection = DataSourceLocator.getDataSource(SURVEY_DATA_SOURCE)
                .getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, encuestaId);
            ResultSet rs = preparedStatement.executeQuery();

            if (!rs.next()) {
                throw new InstanceNotFoundException(encuestaId,
                        Encuesta.class.getName());
            }

            String pregunta = rs.getString("pregunta");
            Timestamp fechaFin = rs.getTimestamp("fechaFin");
            Timestamp fechaCreacion = rs.getTimestamp("fechaCreacion");
            int respuestasPos = rs.getInt("respuestasPositivas");
            int respuestasNeg = rs.getInt("respuestasNegativas");
            boolean cancelada = rs.getBoolean("cancelada");

            return new Encuesta(encuestaId, pregunta, fechaFin.toLocalDateTime(),
                    fechaCreacion.toLocalDateTime(), respuestasPos, respuestasNeg, cancelada);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Encuesta> findByKeyword(String keyword, boolean soloNoFinalizadas) {

        // Versión simplificada de la query base
        String baseQuery = "SELECT encuestaId, pregunta, fechaFin, fechaCreacion, " +
                "respuestasPositivas, respuestasNegativas, cancelada " +
                "FROM Encuesta WHERE pregunta LIKE ?";

        // Añadir condiciones dinámicas
        if (soloNoFinalizadas) {
            baseQuery += " AND fechaFin > ?";
        }

        baseQuery += " ORDER BY fechaCreacion DESC";

        List<Encuesta> encuestas = new ArrayList<>();

        try (Connection connection = DataSourceLocator.getDataSource("jdbc/app").getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(baseQuery)) {

            preparedStatement.setString(1, "%" + keyword + "%");
            if (soloNoFinalizadas) {
                preparedStatement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            }

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                // Recuperar datos del ResultSet
                Long encuestaId = rs.getLong("encuestaId");
                String pregunta = rs.getString("pregunta");
                LocalDateTime fechaFin = rs.getTimestamp("fechaFin").toLocalDateTime();
                LocalDateTime fechaCreacion = rs.getTimestamp("fechaCreacion").toLocalDateTime();
                int respuestasPositivas = rs.getInt("respuestasPositivas");
                int respuestasNegativas = rs.getInt("respuestasNegativas");
                boolean cancelada = rs.getBoolean("cancelada");

                // Añadir a la lista
                encuestas.add(new Encuesta(encuestaId, pregunta, fechaFin, fechaCreacion,
                        respuestasPositivas, respuestasNegativas, cancelada));
            }

            return encuestas;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Encuesta> findByKeywords(String keywords, boolean soloNoFinalizadas) {

        List<Encuesta> encuestas = new ArrayList<>();


        String queryString = "SELECT encuestaId, pregunta, fechaCreacion, fechaFin, "
                + "respuestasPositivas, respuestasNegativas, cancelada "
                + "FROM Encuesta WHERE pregunta LIKE ?";


        if (soloNoFinalizadas) {
            queryString += " AND fechaFin > ?";
        }

        queryString += " ORDER BY fechaCreacion DESC";

        try (Connection connection = DataSourceLocator.getDataSource("jdbc/ws-javaexamples-ds").getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            // Asignamos los parámetros
            preparedStatement.setString(1, "%" + keywords + "%");

            if (soloNoFinalizadas) {
                preparedStatement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            }

            ResultSet rs = preparedStatement.executeQuery();

            // Recorremos los resultados
            while (rs.next()) {
                Long encuestaId = rs.getLong(1);
                String pregunta = rs.getString(2);
                LocalDateTime fechaCreacion = rs.getTimestamp(3).toLocalDateTime();
                LocalDateTime fechaFin = rs.getTimestamp(4).toLocalDateTime();
                long pos = rs.getLong(5); // Usamos getLong
                long neg = rs.getLong(6); // Usamos getLong
                boolean cancelada = rs.getBoolean(7);

                // Añadimos la encuesta a la lista
                encuestas.add(new Encuesta(encuestaId, pregunta, fechaCreacion, fechaFin,
                        pos, neg, cancelada));
            }

            // Devolvemos la lista (estará vacía si no se encontró nada)
            return encuestas;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void update(Encuesta encuesta) throws InstanceNotFoundException {

        String queryString = "UPDATE Encuesta SET pregunta = ?, fechaFin = ?, " +
                "respuestasPositivas = ?, respuestasNegativas = ?, cancelada = ? " +
                "WHERE encuestaId = ?";

        // Usamos el JNDI name de tu pom.xml
        try (Connection connection = DataSourceLocator.getDataSource("jdbc/ws-javaexamples-ds").getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            // Asignar parámetros
            preparedStatement.setString(1, encuesta.getPregunta());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(encuesta.getFechaFin()));
            preparedStatement.setLong(3, encuesta.getRespuestasPositivas());
            preparedStatement.setLong(4, encuesta.getRespuestasNegativas());
            preparedStatement.setBoolean(5, encuesta.isCancelada());
            preparedStatement.setLong(6, encuesta.getEncuestaId());

            // Ejecutar la actualización
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                // Si no se actualizó ninguna fila, la encuesta no existía
                throw new InstanceNotFoundException(encuesta.getEncuestaId(), Encuesta.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(Long encuestaId) throws InstanceNotFoundException {

        String query = "DELETE FROM Encuesta WHERE encuestaId = ?";

        try (Connection connection = DataSourceLocator.getDataSource("jdbc/app").getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, encuestaId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

