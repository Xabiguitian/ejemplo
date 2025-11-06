package es.udc.ws.app.model.encuesta;

import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.SURVEY_DATA_SOURCE;

public abstract class AbstractSqlEncuestaDao implements SqlEncuestaDao {

    @Override
    public Encuesta create(Connection connection, Encuesta encuesta) {

        String query = "INSERT INTO Encuesta (pregunta, fechaFin, fechaCreacion, " +
                "respuestasPositivas, respuestasNegativas, cancelada) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                query, Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            preparedStatement.setString(i++, encuesta.getPregunta());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(encuesta.getFechaFin()));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(encuesta.getFechaCreacion()));
            preparedStatement.setLong(i++, encuesta.getRespuestasPositivas());
            preparedStatement.setLong(i++, encuesta.getRespuestasNegativas());
            preparedStatement.setBoolean(i++, encuesta.isCancelada());

            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("No se pudo obtener el ID generado para la encuesta");
            }

            Long encuestaId = rs.getLong(1);
            System.out.println("[DEBUG] --> Encuesta insertada con ID: " + encuestaId);

            return new Encuesta(
                    encuestaId,
                    encuesta.getPregunta(),
                    encuesta.getFechaCreacion(),
                    encuesta.getFechaFin(),
                    encuesta.getRespuestasPositivas(),
                    encuesta.getRespuestasNegativas(),
                    encuesta.isCancelada()
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Encuesta find(Connection connection, Long encuestaId)
            throws InstanceNotFoundException {

        String query = "SELECT pregunta, fechaCreacion, fechaFin, " +
                "respuestasPositivas, respuestasNegativas, cancelada " +
                "FROM Encuesta WHERE encuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, encuestaId);
            ResultSet rs = preparedStatement.executeQuery();

            if (!rs.next()) {
                throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
            }

            String pregunta = rs.getString("pregunta");
            LocalDateTime fechaCreacion = rs.getTimestamp("fechaCreacion").toLocalDateTime();
            LocalDateTime fechaFin = rs.getTimestamp("fechaFin").toLocalDateTime();
            long respuestasPositivas = rs.getLong("respuestasPositivas");
            long respuestasNegativas = rs.getLong("respuestasNegativas");
            boolean cancelada = rs.getBoolean("cancelada");

            return new Encuesta(encuestaId, pregunta, fechaCreacion, fechaFin,
                    respuestasPositivas, respuestasNegativas, cancelada);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Encuesta> findByKeywords(String keywords, boolean soloNoFinalizadas) {

        List<Encuesta> encuestas = new ArrayList<>();

        String query = "SELECT encuestaId, pregunta, fechaCreacion, fechaFin, " +
                "respuestasPositivas, respuestasNegativas, cancelada " +
                "FROM Encuesta WHERE pregunta LIKE ?";

        if (soloNoFinalizadas) {
            query += " AND fechaFin > ?";
        }

        query += " ORDER BY fechaCreacion DESC";

        try (Connection connection = DataSourceLocator.getDataSource(SURVEY_DATA_SOURCE).getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, "%" + keywords + "%");
            if (soloNoFinalizadas) {
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Long encuestaId = rs.getLong("encuestaId");
                String pregunta = rs.getString("pregunta");
                LocalDateTime fechaCreacion = rs.getTimestamp("fechaCreacion").toLocalDateTime();
                LocalDateTime fechaFin = rs.getTimestamp("fechaFin").toLocalDateTime();
                long pos = rs.getLong("respuestasPositivas");
                long neg = rs.getLong("respuestasNegativas");
                boolean cancelada = rs.getBoolean("cancelada");

                encuestas.add(new Encuesta(encuestaId, pregunta, fechaCreacion, fechaFin, pos, neg, cancelada));
            }

            return encuestas;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Connection connection, Encuesta encuesta)
            throws InstanceNotFoundException {

        String query = "UPDATE Encuesta SET pregunta = ?, fechaFin = ?, " +
                "respuestasPositivas = ?, respuestasNegativas = ?, cancelada = ? " +
                "WHERE encuestaId = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, encuesta.getPregunta());
            ps.setTimestamp(2, Timestamp.valueOf(encuesta.getFechaFin()));
            ps.setLong(3, encuesta.getRespuestasPositivas());
            ps.setLong(4, encuesta.getRespuestasNegativas());
            ps.setBoolean(5, encuesta.isCancelada());
            ps.setLong(6, encuesta.getEncuestaId());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new InstanceNotFoundException(encuesta.getEncuestaId(), Encuesta.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Connection connection, Long encuestaId)
            throws InstanceNotFoundException {

        String query = "DELETE FROM Encuesta WHERE encuestaId = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setLong(1, encuestaId);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
