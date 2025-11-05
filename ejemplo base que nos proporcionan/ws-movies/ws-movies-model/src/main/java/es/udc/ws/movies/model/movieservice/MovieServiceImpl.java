package es.udc.ws.movies.model.movieservice;

import static es.udc.ws.movies.model.util.ModelConstants.BASE_URL;
import static es.udc.ws.movies.model.util.ModelConstants.MAX_PRICE;
import static es.udc.ws.movies.model.util.ModelConstants.MAX_RUNTIME;
import static es.udc.ws.movies.model.util.ModelConstants.MOVIE_DATA_SOURCE;
import static es.udc.ws.movies.model.util.ModelConstants.SALE_EXPIRATION_DAYS;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import es.udc.ws.movies.model.movie.Movie;
import es.udc.ws.movies.model.movie.SqlMovieDao;
import es.udc.ws.movies.model.movie.SqlMovieDaoFactory;
import es.udc.ws.movies.model.movieservice.exceptions.MovieNotRemovableException;
import es.udc.ws.movies.model.movieservice.exceptions.SaleExpirationException;
import es.udc.ws.movies.model.sale.Sale;
import es.udc.ws.movies.model.sale.SqlSaleDao;
import es.udc.ws.movies.model.sale.SqlSaleDaoFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;

public class MovieServiceImpl implements MovieService {
	/*
	 * IMPORTANT: Some JDBC drivers require "setTransactionIsolation" to be called
	 * before "setAutoCommit".
	 */

	private final DataSource dataSource;
	private SqlMovieDao movieDao = null;
	private SqlSaleDao saleDao = null;

	public MovieServiceImpl() {
		dataSource = DataSourceLocator.getDataSource(MOVIE_DATA_SOURCE);
		movieDao = SqlMovieDaoFactory.getDao();
		saleDao = SqlSaleDaoFactory.getDao();
	}

	private void validateMovie(Movie movie) throws InputValidationException {

		PropertyValidator.validateMandatoryString("title", movie.getTitle());
		PropertyValidator.validateLong("runtime", movie.getRuntime(), 0, MAX_RUNTIME);
		PropertyValidator.validateMandatoryString("description", movie.getDescription());
		PropertyValidator.validateDouble("price", movie.getPrice(), 0, MAX_PRICE);

	}

	@Override
	public Movie addMovie(Movie movie) throws InputValidationException {

		validateMovie(movie);
		movie.setCreationDate(LocalDateTime.now());

		try (Connection connection = dataSource.getConnection()) {

			try {

				/* Prepare connection. */
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				connection.setAutoCommit(false);

				/* Do work. */
				Movie createdMovie = movieDao.create(connection, movie);

				/* Commit. */
				connection.commit();

				return createdMovie;

			} catch (SQLException e) {
				connection.rollback();
				throw new RuntimeException(e);
			} catch (RuntimeException | Error e) {
				connection.rollback();
				throw e;
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void updateMovie(Movie movie) throws InputValidationException, InstanceNotFoundException {

		validateMovie(movie);

		try (Connection connection = dataSource.getConnection()) {

			try {

				/* Prepare connection. */
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				connection.setAutoCommit(false);

				/* Do work. */
				movieDao.update(connection, movie);

				/* Commit. */
				connection.commit();

			} catch (InstanceNotFoundException e) {
				connection.commit();
				throw e;
			} catch (SQLException e) {
				connection.rollback();
				throw new RuntimeException(e);
			} catch (RuntimeException | Error e) {
				connection.rollback();
				throw e;
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void removeMovie(Long movieId) throws InstanceNotFoundException, MovieNotRemovableException {

		try (Connection connection = dataSource.getConnection()) {

			try {

				/* Prepare connection. */
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				connection.setAutoCommit(false);

				/* Do work. */
				if (saleDao.existsByMovieId(connection, movieId)) {
					throw new MovieNotRemovableException(movieId);
				}
				movieDao.remove(connection, movieId);

				/* Commit. */
				connection.commit();

			} catch (InstanceNotFoundException | MovieNotRemovableException e) {
				connection.commit();
				throw e;
			} catch (SQLException e) {
				connection.rollback();
				throw new RuntimeException(e);
			} catch (RuntimeException | Error e) {
				connection.rollback();
				throw e;
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public Movie findMovie(Long movieId) throws InstanceNotFoundException {

		try (Connection connection = dataSource.getConnection()) {
			return movieDao.find(connection, movieId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public List<Movie> findMovies(String keywords) {

		try (Connection connection = dataSource.getConnection()) {
			return movieDao.findByKeywords(connection, keywords);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Sale buyMovie(Long movieId, String userId, String creditCardNumber)
			throws InstanceNotFoundException, InputValidationException {

		PropertyValidator.validateCreditCard(creditCardNumber);

		try (Connection connection = dataSource.getConnection()) {

			try {

				/* Prepare connection. */
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				connection.setAutoCommit(false);

				/* Do work. */
				Movie movie = movieDao.find(connection, movieId);
				LocalDateTime expirationDate = LocalDateTime.now().plusDays(SALE_EXPIRATION_DAYS);
				Sale sale = saleDao.create(connection, new Sale(movieId, userId, expirationDate, creditCardNumber,
						movie.getPrice(), getMovieUrl(movieId), LocalDateTime.now()));

				/* Commit. */
				connection.commit();

				return sale;

			} catch (InstanceNotFoundException e) {
				connection.commit();
				throw e;
			} catch (SQLException e) {
				connection.rollback();
				throw new RuntimeException(e);
			} catch (RuntimeException | Error e) {
				connection.rollback();
				throw e;
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public Sale findSale(Long saleId) throws InstanceNotFoundException, SaleExpirationException {

		try (Connection connection = dataSource.getConnection()) {

			Sale sale = saleDao.find(connection, saleId);
			LocalDateTime now = LocalDateTime.now();
			if (sale.getExpirationDate().isAfter(now)) {
				return sale;
			} else {
				throw new SaleExpirationException(saleId, sale.getExpirationDate());
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	private static String getMovieUrl(Long movieId) {
		return BASE_URL + movieId + "/" + UUID.randomUUID().toString();
	}
}
