package es.udc.ws.app.model.encuesta;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;

public class Jdbc3CcSqlEncuestaDao extends AbstractSqlEncuestaDao {

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

            int filas = preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("No se pudo obtener el ID generado para la encuesta");
            }
            Long encuestaId = rs.getLong(1);

            Encuesta nueva = new Encuesta(
                    encuestaId,
                    encuesta.getPregunta(),
                    encuesta.getFechaCreacion(),
                    encuesta.getFechaFin(),
                    encuesta.getRespuestasPositivas(),
                    encuesta.getRespuestasNegativas(),
                    encuesta.isCancelada()
            );

            return nueva;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Encuesta find(Connection connection, Long encuestaId)
            throws InstanceNotFoundException {

        String query = "SELECT encuestaId, pregunta, fechaCreacion, fechaFin, " +
                "respuestasPositivas, respuestasNegativas, cancelada " +
                "FROM Encuesta WHERE encuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, encuestaId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (!resultSet.next()) {
                    throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
                }

                Long id = resultSet.getLong("encuestaId");
                String pregunta = resultSet.getString("pregunta");
                LocalDateTime fechaCreacion = resultSet.getTimestamp("fechaCreacion").toLocalDateTime();
                LocalDateTime fechaFin = resultSet.getTimestamp("fechaFin").toLocalDateTime();
                long respuestasPositivas = resultSet.getLong("respuestasPositivas");
                long respuestasNegativas = resultSet.getLong("respuestasNegativas");
                boolean cancelada = resultSet.getBoolean("cancelada");

                Encuesta encuesta = new Encuesta(
                        id,
                        pregunta,
                        fechaCreacion,
                        fechaFin,
                        respuestasPositivas,
                        respuestasNegativas,
                        cancelada
                );

                return encuesta;
            }

        } catch (SQLException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException("Error al buscar encuesta con ID " + encuestaId, e);
        }
    }

    @Override
    public void update(Connection connection, Encuesta encuesta)
            throws InstanceNotFoundException {

        String query = "UPDATE Encuesta SET pregunta = ?, fechaCreacion = ?, fechaFin = ?, " +
                "respuestasPositivas = ?, respuestasNegativas = ?, cancelada = ? WHERE encuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            int i = 1;
            preparedStatement.setString(i++, encuesta.getPregunta());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(encuesta.getFechaCreacion()));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(encuesta.getFechaFin()));
            preparedStatement.setLong(i++, encuesta.getRespuestasPositivas());
            preparedStatement.setLong(i++, encuesta.getRespuestasNegativas());
            preparedStatement.setBoolean(i++, encuesta.isCancelada());
            preparedStatement.setLong(i++, encuesta.getEncuestaId());

            int filas = preparedStatement.executeUpdate();
            if (filas == 0) {
                throw new InstanceNotFoundException(encuesta.getEncuestaId(), Encuesta.class.getName());
            }

        } catch (SQLException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException("Error al actualizar encuesta con ID " + encuesta.getEncuestaId(), e);
        }
    }

    @Override
    public void remove(Connection connection, Long encuestaId)
            throws InstanceNotFoundException {

        String query = "DELETE FROM Encuesta WHERE encuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, encuestaId);

            int filas = preparedStatement.executeUpdate();
            if (filas == 0) {
                throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
            }

        } catch (SQLException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException("Error al eliminar encuesta con ID " + encuestaId, e);
        }
    }
}
