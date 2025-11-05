package es.udc.ws.movies.model.movieservice;

import es.udc.ws.movies.model.movieservice.exceptions.MovieNotRemovableException;
import es.udc.ws.movies.model.movieservice.exceptions.SaleExpirationException;
import java.util.List;

import es.udc.ws.movies.model.movie.Movie;
import es.udc.ws.movies.model.sale.Sale;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public interface MovieService {

    public Movie addMovie(Movie movie) throws InputValidationException;

    public void updateMovie(Movie movie) throws InputValidationException,
            InstanceNotFoundException;

    public void removeMovie(Long movieId) throws InstanceNotFoundException,
            MovieNotRemovableException;

    public Movie findMovie(Long movieId) throws InstanceNotFoundException;

    public List<Movie> findMovies(String keywords);

    public Sale buyMovie(Long movieId, String userId, String creditCardNumber)
            throws InstanceNotFoundException, InputValidationException;

    public Sale findSale(Long saleId) throws InstanceNotFoundException,
            SaleExpirationException;
}
