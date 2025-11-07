package es.udc.ws.app.model.respuesta;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AbstractSqlResouestaDao implements SqlRespuestaDao {


    @Override
    public Respuesta create(Connection connection, Respuesta respuesta) {
        return null;
    }

    @Override
    public void update(Connection connection, Respuesta respuesta) throws InstanceNotFoundException {

        String queryString = "UPDATE Respuesta SET emailEmpleado = ?, afirmativa = ?, fechaRespuesta = ? "
                + "WHERE respuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.setString(1, respuesta.getEmailEmpleado());
            preparedStatement.setBoolean(2, respuesta.isAfirmativa());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(respuesta.getFechaRespuesta()));
            preparedStatement.setLong(4, respuesta.getRespuestaId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new InstanceNotFoundException(respuesta.getRespuestaId(), Respuesta.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Respuesta findByEmailAndEncuestaId(Connection connection, Long encuestaId, String emailEmpleado) {

        String queryString = "SELECT respuestaId, afirmativa, fechaRespuesta "
                + "FROM Respuesta WHERE encuestaId = ? AND emailEmpleado = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            preparedStatement.setLong(1, encuestaId);
            preparedStatement.setString(2, emailEmpleado);

            ResultSet rs = preparedStatement.executeQuery();

            if (!rs.next()) {
                return null;
            }

            Long respuestaId = rs.getLong(1);
            boolean afirmativa = rs.getBoolean(2);
            LocalDateTime fechaRespuesta = rs.getTimestamp(3).toLocalDateTime();

            return new Respuesta(respuestaId, encuestaId, emailEmpleado, afirmativa, fechaRespuesta);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Respuesta> findByEncuestaId(Connection connection, Long encuestaId, boolean soloAfirmativas) {

        List<Respuesta> respuestas = new ArrayList<>();

        String queryString = "SELECT respuestaId, emailEmpleado, afirmativa, fechaRespuesta "
                + "FROM Respuesta WHERE encuestaId = ?";

        if (soloAfirmativas) {
            queryString += " AND afirmativa = true";
        }

        queryString += " ORDER BY fechaRespuesta DESC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            preparedStatement.setLong(1, encuestaId);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Long respuestaId = rs.getLong(1);
                String emailEmpleado = rs.getString(2);
                boolean afirmativa = rs.getBoolean(3);
                LocalDateTime fechaRespuesta = rs.getTimestamp(4).toLocalDateTime();

                respuestas.add(new Respuesta(respuestaId, encuestaId, emailEmpleado, afirmativa, fechaRespuesta));
            }

            return respuestas;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
