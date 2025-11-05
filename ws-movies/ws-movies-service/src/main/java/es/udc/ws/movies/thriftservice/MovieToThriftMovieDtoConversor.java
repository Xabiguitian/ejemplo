package es.udc.ws.movies.thriftservice;

import es.udc.ws.movies.model.movie.Movie;
import es.udc.ws.movies.thrift.ThriftMovieDto;

import java.util.ArrayList;
import java.util.List;

public class MovieToThriftMovieDtoConversor {

    public static Movie toMovie(ThriftMovieDto movie) {
        return new Movie(movie.getMovieId(), movie.getTitle(), movie.getRuntime(),
            movie.getDescription(), Double.valueOf(movie.getPrice()).floatValue());
    }

    public static List<ThriftMovieDto> toThriftMovieDtos(List<Movie> movies) {

        List<ThriftMovieDto> dtos = new ArrayList<>(movies.size());

        for (Movie movie : movies) {
            dtos.add(toThriftMovieDto(movie));
        }
        return dtos;

    }

    public static ThriftMovieDto toThriftMovieDto(Movie movie) {

        return new ThriftMovieDto(movie.getMovieId(), movie.getTitle(),
            movie.getRuntime(), movie.getDescription(),
            movie.getPrice());

    }

}
