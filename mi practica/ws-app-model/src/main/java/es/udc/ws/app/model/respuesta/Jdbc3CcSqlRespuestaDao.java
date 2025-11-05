package es.udc.ws.app.model.respuesta;

import es.udc.ws.util.sql.DataSourceLocator;

import java.sql.*;

public class Jdbc3CcSqlRespuestaDao extends AbstractSqlResouestaDao {
    @Override
    public Respuesta create(Connection connection, Respuesta respuesta) {

        String queryString = "INSERT INTO Respuesta (encuestaId, emailEmpleado, afirmativa, fechaRespuesta)"
                + " VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)) {

            // Asignar parámetros
            preparedStatement.setLong(1, respuesta.getEncuestaId());
            preparedStatement.setString(2, respuesta.getEmailEmpleado());
            preparedStatement.setBoolean(3, respuesta.isAfirmativa());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(respuesta.getFechaRespuesta()));

            preparedStatement.executeUpdate();

            // Obtener el ID generado
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("No se pudo obtener el ID generado para la respuesta");
            }
            Long respuestaId = rs.getLong(1);

            // Devolver la respuesta actualizada con su ID
            respuesta.setRespuestaId(respuestaId);
            return respuesta;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}