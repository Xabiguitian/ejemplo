package es.udc.ws.movies.model.movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

/**
 * A partial implementation of
 * <code>SQLMovieDAO</code> that leaves
 * <code>create(Connection, Movie)</code> as abstract.
 */
public abstract class AbstractSqlMovieDao implements SqlMovieDao {

    protected AbstractSqlMovieDao() {
    }

    @Override
    public Movie find(Connection connection, Long movieId)
            throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "SELECT title, runtime, "
                + " description, price, creationDate FROM Movie WHERE movieId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, movieId.longValue());

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(movieId,
                        Movie.class.getName());
            }

            /* Get results. */
            i = 1;
            String title = resultSet.getString(i++);
            short runtime = resultSet.getShort(i++);
            String description = resultSet.getString(i++);
            float price = resultSet.getFloat(i++);
            Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();

            /* Return movie. */
            return new Movie(movieId, title, runtime, description, price,
                    creationDate);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Movie> findByKeywords(Connection connection, String keywords) {

        /* Create "queryString". */
        String[] words = keywords != null ? keywords.split(" ") : null;
        String queryString = "SELECT movieId, title, runtime, "
                + " description, price, creationDate FROM Movie";
        if (words != null && words.length > 0) {
            queryString += " WHERE";
            for (int i = 0; i < words.length; i++) {
                if (i > 0) {
                    queryString += " AND";
                }
                queryString += " LOWER(title) LIKE LOWER(?)";
            }
        }
        queryString += " ORDER BY title";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            if (words != null) {
                /* Fill "preparedStatement". */
                for (int i = 0; i < words.length; i++) {
                    preparedStatement.setString(i + 1, "%" + words[i] + "%");
                }
            }

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            /* Read movies. */
            List<Movie> movies = new ArrayList<Movie>();

            while (resultSet.next()) {

                int i = 1;
                Long movieId = Long.valueOf(resultSet.getLong(i++));
                String title = resultSet.getString(i++);
                short runtime = resultSet.getShort(i++);
                String description = resultSet.getString(i++);
                float price = resultSet.getFloat(i++);
                Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();

                movies.add(new Movie(movieId, title, runtime, description,
                        price, creationDate));

            }

            /* Return movies. */
            return movies;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(Connection connection, Movie movie)
            throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "UPDATE Movie"
                + " SET title = ?, runtime = ?, description = ?, "
                + "price = ? WHERE movieId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setString(i++, movie.getTitle());
            preparedStatement.setShort(i++, movie.getRuntime());
            preparedStatement.setString(i++, movie.getDescription());
            preparedStatement.setFloat(i++, movie.getPrice());
            preparedStatement.setLong(i++, movie.getMovieId());

            /* Execute query. */
            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(movie.getMovieId(),
                        Movie.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void remove(Connection connection, Long movieId)
            throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "DELETE FROM Movie WHERE" + " movieId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, movieId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(movieId,
                        Movie.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
