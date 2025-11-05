package es.udc.ws.movies.restservice.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import es.udc.ws.movies.model.movieservice.exceptions.MovieNotRemovableException;
import es.udc.ws.movies.model.movieservice.exceptions.SaleExpirationException;
import es.udc.ws.movies.restservice.dto.RestMovieDto;
import es.udc.ws.movies.model.movie.Movie;
import es.udc.ws.movies.model.movieservice.MovieServiceFactory;
import es.udc.ws.movies.restservice.json.JsonToRestMovieDtoConversor;
import es.udc.ws.movies.restservice.dto.MovieToRestMovieDtoConversor;
import es.udc.ws.movies.restservice.json.MoviesExceptionToJsonConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;

@SuppressWarnings("serial")
public class MoviesServlet extends RestHttpServletTemplate {

	@Override
	protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
			InputValidationException {
		ServletUtils.checkEmptyPath(req);

		RestMovieDto movieDto = JsonToRestMovieDtoConversor.toRestMovieDto(req.getInputStream());
		Movie movie = MovieToRestMovieDtoConversor.toMovie(movieDto);

		movie = MovieServiceFactory.getService().addMovie(movie);

		movieDto = MovieToRestMovieDtoConversor.toRestMovieDto(movie);
		String movieURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + movie.getMovieId();
		Map<String, String> headers = new HashMap<>(1);
		headers.put("Location", movieURL);
		ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
				JsonToRestMovieDtoConversor.toObjectNode(movieDto), headers);
	}

	@Override
	protected void processPut(HttpServletRequest req, HttpServletResponse resp) throws IOException,
			InputValidationException, InstanceNotFoundException {
		Long movieId = ServletUtils.getIdFromPath(req, "movie");

		RestMovieDto movieDto = JsonToRestMovieDtoConversor.toRestMovieDto(req.getInputStream());
		if (!movieId.equals(movieDto.getMovieId())) {
			throw new InputValidationException("Invalid Request: invalid movieId");
		}
		Movie movie = MovieToRestMovieDtoConversor.toMovie(movieDto);

		MovieServiceFactory.getService().updateMovie(movie);

		ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NO_CONTENT, null, null);
	}

	@Override
	protected void processDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException,
			InputValidationException, InstanceNotFoundException {
		Long movieId = ServletUtils.getIdFromPath(req, "movie");

		try {
			MovieServiceFactory.getService().removeMovie(movieId);
		} catch (MovieNotRemovableException ex) {
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
					MoviesExceptionToJsonConversor.toMovieNotRemovableException(ex),
					null);
			return;
		}

		ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NO_CONTENT, null, null);
	}

	@Override
	protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
			InputValidationException {
		ServletUtils.checkEmptyPath(req);
		String keyWords = req.getParameter("keywords");

		List<Movie> movies = MovieServiceFactory.getService().findMovies(keyWords);

		List<RestMovieDto> movieDtos = MovieToRestMovieDtoConversor.toRestMovieDtos(movies);
		ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
				JsonToRestMovieDtoConversor.toArrayNode(movieDtos), null);
	}

}
