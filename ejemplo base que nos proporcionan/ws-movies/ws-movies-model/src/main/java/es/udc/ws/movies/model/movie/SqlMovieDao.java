package es.udc.ws.movies.model.movie;

import java.sql.Connection;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public interface SqlMovieDao {

    public Movie create(Connection connection, Movie movie);

    public Movie find(Connection connection, Long movieId)
            throws InstanceNotFoundException;

    public List<Movie> findByKeywords(Connection connection,
            String keywords);

    public void update(Connection connection, Movie movie)
            throws InstanceNotFoundException;

    public void remove(Connection connection, Long movieId)
            throws InstanceNotFoundException;
}
