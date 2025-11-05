package es.udc.ws.movies.model.movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class Jdbc3CcSqlMovieDao extends AbstractSqlMovieDao {

    @Override
    public Movie create(Connection connection, Movie movie) {

        /* Create "queryString". */
        String queryString = "INSERT INTO Movie"
                + " (title, runtime, description, price, creationDate)"
                + " VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                        queryString, Statement.RETURN_GENERATED_KEYS)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setString(i++, movie.getTitle());
            preparedStatement.setShort(i++, movie.getRuntime());
            preparedStatement.setString(i++, movie.getDescription());
            preparedStatement.setFloat(i++, movie.getPrice());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(movie.getCreationDate()));

            /* Execute query. */
            preparedStatement.executeUpdate();

            /* Get generated identifier. */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long movieId = resultSet.getLong(1);

            /* Return movie. */
            return new Movie(movieId, movie.getTitle(), movie.getRuntime(),
                    movie.getDescription(), movie.getPrice(),
                    movie.getCreationDate());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
