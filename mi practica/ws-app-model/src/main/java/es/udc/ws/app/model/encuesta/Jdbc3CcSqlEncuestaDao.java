package es.udc.ws.app.model.encuesta;

import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;

import java.sql.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;


public class Jdbc3CcSqlEncuestaDao extends AbstractSqlEncuestaDao{

    @Override
    public Encuesta create(Connection connection, Encuesta encuesta) {

        String query = "INSERT INTO Encuesta (pregunta, fechaFin, fechaCreacion, " +
                "respuestasPositivas, respuestasNegativas, cancelada) " +
                "VALUES (?, ?, ?, ?, ?, ?)";


        try (PreparedStatement preparedStatement = connection.prepareStatement(
                query, Statement.RETURN_GENERATED_KEYS)) {

            // Asignar parámetros
            preparedStatement.setString(1, encuesta.getPregunta());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(encuesta.getFechaFin()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(encuesta.getFechaCreacion()));
            preparedStatement.setInt(4, (int) encuesta.getRespuestasPositivas());
            preparedStatement.setInt(5, (int) encuesta.getRespuestasNegativas());
            preparedStatement.setBoolean(6, encuesta.isCancelada());

            // Ejecutar la inserción
            preparedStatement.executeUpdate();

            // Obtener el ID generado
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("No se pudo obtener el ID generado para la encuesta");
            }
            Long encuestaId = rs.getLong(1);

            // Devolver la encuesta actualizada con su ID
            encuesta.setEncuestaId(encuestaId);
            return encuesta;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Encuesta find(Connection connection, Long encuestaId) throws InstanceNotFoundException {
        return null;
    }

    @Override
    public void update(Connection connection, Encuesta encuesta) throws InstanceNotFoundException {

    }

    @Override
    public void remove(Connection connection, Long encuestaId) throws InstanceNotFoundException {

    }
}
