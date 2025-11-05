package es.udc.ws.movies.restservice.dto;

import java.util.ArrayList;
import java.util.List;

import es.udc.ws.movies.model.movie.Movie;

public class MovieToRestMovieDtoConversor {

	public static List<RestMovieDto> toRestMovieDtos(List<Movie> movies) {
		List<RestMovieDto> movieDtos = new ArrayList<>(movies.size());
		for (int i = 0; i < movies.size(); i++) {
			Movie movie = movies.get(i);
			movieDtos.add(toRestMovieDto(movie));
		}
		return movieDtos;
	}

	public static RestMovieDto toRestMovieDto(Movie movie) {
		return new RestMovieDto(movie.getMovieId(), movie.getTitle(), movie.getRuntime(), movie.getDescription(),
				movie.getPrice());
	}

	public static Movie toMovie(RestMovieDto movie) {
		return new Movie(movie.getMovieId(), movie.getTitle(), movie.getRuntime(), movie.getDescription(),
				movie.getPrice());
	}

}
