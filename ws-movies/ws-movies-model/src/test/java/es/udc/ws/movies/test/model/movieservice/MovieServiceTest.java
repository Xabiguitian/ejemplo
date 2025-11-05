package es.udc.ws.movies.test.model.movieservice;

import static es.udc.ws.movies.model.util.ModelConstants.BASE_URL;
import static es.udc.ws.movies.model.util.ModelConstants.MAX_PRICE;
import static es.udc.ws.movies.model.util.ModelConstants.MAX_RUNTIME;
import static es.udc.ws.movies.model.util.ModelConstants.MOVIE_DATA_SOURCE;
import static es.udc.ws.movies.model.util.ModelConstants.SALE_EXPIRATION_DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import es.udc.ws.movies.model.movieservice.exceptions.MovieNotRemovableException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import es.udc.ws.movies.model.movie.Movie;
import es.udc.ws.movies.model.movieservice.MovieService;
import es.udc.ws.movies.model.movieservice.MovieServiceFactory;
import es.udc.ws.movies.model.movieservice.exceptions.SaleExpirationException;
import es.udc.ws.movies.model.sale.Sale;
import es.udc.ws.movies.model.sale.SqlSaleDao;
import es.udc.ws.movies.model.sale.SqlSaleDaoFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;


public class MovieServiceTest {

	private final long NON_EXISTENT_MOVIE_ID = -1;
	private final long NON_EXISTENT_SALE_ID = -1;
	private final String USER_ID = "ws-user";

	private final String VALID_CREDIT_CARD_NUMBER = "1234567890123456";
	private final String INVALID_CREDIT_CARD_NUMBER = "";

	private static MovieService movieService = null;

	private static SqlSaleDao saleDao = null;

	@BeforeAll
	public static void init() {

		/*
		 * Create a simple data source and add it to "DataSourceLocator" (this
		 * is needed to test "es.udc.ws.movies.model.movieservice.MovieService"
		 */
		DataSource dataSource = new SimpleDataSource();

		/* Add "dataSource" to "DataSourceLocator". */
		DataSourceLocator.addDataSource(MOVIE_DATA_SOURCE, dataSource);

		movieService = MovieServiceFactory.getService();

		saleDao = SqlSaleDaoFactory.getDao();

	}

	private Movie getValidMovie(String title) {
		return new Movie(title, (short) 85, "Movie description", 19.95F);
	}

	private Movie getValidMovie() {
		return getValidMovie("Movie title");
	}

	private Movie createMovie(Movie movie) {

		Movie addedMovie = null;
		try {
			addedMovie = movieService.addMovie(movie);
		} catch (InputValidationException e) {
			throw new RuntimeException(e);
		}
		return addedMovie;

	}

	private void removeMovie(Long movieId) {

		try {
			movieService.removeMovie(movieId);
		} catch (InstanceNotFoundException | MovieNotRemovableException e) {
			throw new RuntimeException(e);
		}

	}

	private void removeSale(Long saleId) {

		DataSource dataSource = DataSourceLocator.getDataSource(MOVIE_DATA_SOURCE);

		try (Connection connection = dataSource.getConnection()) {

			try {

				/* Prepare connection. */
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				connection.setAutoCommit(false);

				/* Do work. */
				saleDao.remove(connection, saleId);

				/* Commit. */
				connection.commit();

			} catch (InstanceNotFoundException e) {
				connection.commit();
				throw new RuntimeException(e);
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

	private void updateSale(Sale sale) {

		DataSource dataSource = DataSourceLocator.getDataSource(MOVIE_DATA_SOURCE);

		try (Connection connection = dataSource.getConnection()) {

			try {

				/* Prepare connection. */
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				connection.setAutoCommit(false);

				/* Do work. */
				saleDao.update(connection, sale);

				/* Commit. */
				connection.commit();

			} catch (InstanceNotFoundException e) {
				connection.commit();
				throw new RuntimeException(e);
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

	@Test
	public void testAddMovieAndFindMovie() throws InputValidationException, InstanceNotFoundException {

		Movie movie = getValidMovie();
		Movie addedMovie = null;

		try {
			
			// Create Movie
			LocalDateTime beforeCreationDate = LocalDateTime.now().withNano(0);
			
			addedMovie = movieService.addMovie(movie);
			
			LocalDateTime afterCreationDate = LocalDateTime.now().withNano(0);
			
			// Find Movie
			Movie foundMovie = movieService.findMovie(addedMovie.getMovieId());

			assertEquals(addedMovie, foundMovie);
			assertEquals(foundMovie.getTitle(),movie.getTitle());
			assertEquals(foundMovie.getRuntime(),movie.getRuntime());
			assertEquals(foundMovie.getDescription(),movie.getDescription());
			assertEquals(foundMovie.getPrice(),movie.getPrice());
			assertTrue((foundMovie.getCreationDate().compareTo(beforeCreationDate) >= 0)
					&& (foundMovie.getCreationDate().compareTo(afterCreationDate) <= 0));
			
		} finally {
			// Clear Database
			if (addedMovie!=null) {
				removeMovie(addedMovie.getMovieId());
			}
		}
	}

	@Test
	public void testAddInvalidMovie() {

		// Check movie title not null
		assertThrows(InputValidationException.class, () -> {
			Movie movie = getValidMovie();
			movie.setTitle(null);
			Movie addedMovie = movieService.addMovie(movie);
			removeMovie(addedMovie.getMovieId());
		});

		// Check movie title not empty
		assertThrows(InputValidationException.class, () -> {
			Movie movie = getValidMovie();
			movie.setTitle("");
			Movie addedMovie = movieService.addMovie(movie);
			removeMovie(addedMovie.getMovieId());
		});

		// Check movie runtime >= 0
		assertThrows(InputValidationException.class, () -> {
			Movie movie = getValidMovie();
			movie.setRuntime((short) -1);
			Movie addedMovie = movieService.addMovie(movie);
			removeMovie(addedMovie.getMovieId());
		});

		// Check movie runtime <= MAX_RUNTIME
		assertThrows(InputValidationException.class, () -> {
			Movie movie = getValidMovie();
			movie.setRuntime((short) (MAX_RUNTIME + 1));
			Movie addedMovie = movieService.addMovie(movie);
			removeMovie(addedMovie.getMovieId());
		});

		// Check movie description not null
		assertThrows(InputValidationException.class, () -> {
			Movie movie = getValidMovie();
			movie.setDescription(null);
			Movie addedMovie = movieService.addMovie(movie);
			removeMovie(addedMovie.getMovieId());
		});

		// Check movie description not null
		assertThrows(InputValidationException.class, () -> {
			Movie movie = getValidMovie();
			movie.setDescription("");
			Movie addedMovie = movieService.addMovie(movie);
			removeMovie(addedMovie.getMovieId());
		});

		// Check movie price >= 0
		assertThrows(InputValidationException.class, () -> {
			Movie movie = getValidMovie();
			movie.setPrice((short) -1);
			Movie addedMovie = movieService.addMovie(movie);
			removeMovie(addedMovie.getMovieId());
		});

		// Check movie price <= MAX_PRICE
		assertThrows(InputValidationException.class, () -> {
			Movie movie = getValidMovie();
			movie.setPrice((short) (MAX_PRICE + 1));
			Movie addedMovie = movieService.addMovie(movie);
			removeMovie(addedMovie.getMovieId());
		});

	}

	@Test
	public void testFindNonExistentMovie() {
		assertThrows(InstanceNotFoundException.class, () -> movieService.findMovie(NON_EXISTENT_MOVIE_ID));
	}

	@Test
	public void testUpdateMovie() throws InputValidationException, InstanceNotFoundException {

		Movie movie = createMovie(getValidMovie());
		try {
			Movie movieToUpdate = new Movie(movie.getMovieId(), "new title", (short) 80, "new description", 20);

			movieService.updateMovie(movieToUpdate);

			Movie updatedMovie = movieService.findMovie(movie.getMovieId());
			
			movieToUpdate.setCreationDate(movie.getCreationDate());
			assertEquals(movieToUpdate, updatedMovie);

		} finally {
			// Clear Database
			removeMovie(movie.getMovieId());
		}

	}

	@Test
	public void testUpdateInvalidMovie() throws InstanceNotFoundException {

		Long movieId = createMovie(getValidMovie()).getMovieId();
		try {
			// Check movie title not null
			Movie movie = movieService.findMovie(movieId);
			movie.setTitle(null);
			assertThrows(InputValidationException.class, () -> movieService.updateMovie(movie));
		} finally {
			// Clear Database
			removeMovie(movieId);
		}

	}

	@Test
	public void testUpdateNonExistentMovie() {

		Movie movie = getValidMovie();
		movie.setMovieId(NON_EXISTENT_MOVIE_ID);
		movie.setCreationDate(LocalDateTime.now());

		assertThrows(InstanceNotFoundException.class, () -> movieService.updateMovie(movie));

	}

	@Test
	public void testRemoveMovie() throws InstanceNotFoundException, MovieNotRemovableException {

		Movie movie = createMovie(getValidMovie());

		movieService.removeMovie(movie.getMovieId());

		assertThrows(InstanceNotFoundException.class, () -> movieService.findMovie(movie.getMovieId()));

	}

	@Test
	public void testRemoveNonExistentMovie() {
		assertThrows(InstanceNotFoundException.class, () -> movieService.removeMovie(NON_EXISTENT_MOVIE_ID));
	}

	@Test
	public void testRemoveMovieWithSales() throws InputValidationException, InstanceNotFoundException {
		Movie movie = createMovie(getValidMovie());
		Sale sale = null;
		try {
			sale = movieService.buyMovie(movie.getMovieId(), USER_ID, VALID_CREDIT_CARD_NUMBER);

			assertThrows(MovieNotRemovableException.class, () -> movieService.removeMovie(movie.getMovieId()));
		} finally {
			// Clear Database (sale if it was created and movie)
			if (sale != null) {
				removeSale(sale.getSaleId());
			}
			removeMovie(movie.getMovieId());
		}
	}

	@Test
	public void testFindMovies() {

		// Add movies
		List<Movie> movies = new LinkedList<Movie>();
		Movie movie1 = createMovie(getValidMovie("movie title 1"));
		movies.add(movie1);
		Movie movie2 = createMovie(getValidMovie("movie title 2"));
		movies.add(movie2);
		Movie movie3 = createMovie(getValidMovie("movie title 3"));
		movies.add(movie3);

		try {
			List<Movie> foundMovies = movieService.findMovies("moVie Title");
			assertEquals(movies, foundMovies);

			foundMovies = movieService.findMovies("Mo Title 2");
			assertEquals(1, foundMovies.size());
			assertEquals(movies.get(1), foundMovies.get(0));

			foundMovies = movieService.findMovies("title 5");
			assertEquals(0, foundMovies.size());
		} finally {
			// Clear Database
			for (Movie movie : movies) {
				removeMovie(movie.getMovieId());
			}
		}

	}

	@Test
	public void testBuyMovieAndFindSale()
			throws InstanceNotFoundException, InputValidationException, SaleExpirationException {

		Movie movie = createMovie(getValidMovie());
		Sale sale = null;

		try {

			// Buy movie
			LocalDateTime beforeBuyDate = LocalDateTime.now().withNano(0);

			sale = movieService.buyMovie(movie.getMovieId(), USER_ID, VALID_CREDIT_CARD_NUMBER);

			LocalDateTime afterBuyDate = LocalDateTime.now().withNano(0);

			// Find sale
			Sale foundSale = movieService.findSale(sale.getSaleId());

			// Check sale
			assertEquals(sale, foundSale);
			assertEquals(VALID_CREDIT_CARD_NUMBER, foundSale.getCreditCardNumber());
			assertEquals(USER_ID, foundSale.getUserId());
			assertEquals(movie.getMovieId(), foundSale.getMovieId());
			assertTrue(movie.getPrice() == foundSale.getPrice());
			assertTrue((foundSale.getExpirationDate().compareTo(beforeBuyDate.plusDays(SALE_EXPIRATION_DAYS)) >= 0)
					&& (foundSale.getExpirationDate().compareTo(afterBuyDate.plusDays(SALE_EXPIRATION_DAYS)) <= 0));
			assertTrue((foundSale.getSaleDate().compareTo(beforeBuyDate) >= 0)
					&& (foundSale.getSaleDate().compareTo(afterBuyDate) <= 0));
			assertTrue(foundSale.getMovieUrl().startsWith(BASE_URL + sale.getMovieId()));

		} finally {
			// Clear database: remove sale (if created) and movie
			if (sale != null) {
				removeSale(sale.getSaleId());
			}
			removeMovie(movie.getMovieId());
		}

	}

	@Test
	public void testBuyMovieWithInvalidCreditCard() {

		Movie movie = createMovie(getValidMovie());
		try {
			assertThrows(InputValidationException.class, () -> {
				Sale sale = movieService.buyMovie(movie.getMovieId(), USER_ID, INVALID_CREDIT_CARD_NUMBER);
				removeSale(sale.getSaleId());
			});
		} finally {
			// Clear database
			removeMovie(movie.getMovieId());
		}

	}

	@Test
	public void testBuyNonExistentMovie() {

		assertThrows(InstanceNotFoundException.class, () -> {
			Sale sale = movieService.buyMovie(NON_EXISTENT_MOVIE_ID, USER_ID, VALID_CREDIT_CARD_NUMBER);
			removeSale(sale.getSaleId());
		});

	}

	@Test
	public void testFindNonExistentSale() {
		assertThrows(InstanceNotFoundException.class, () -> movieService.findSale(NON_EXISTENT_SALE_ID));
	}

	@Test
	public void testGetExpiredMovieUrl()
			throws InputValidationException, InstanceNotFoundException {

		Movie movie = createMovie(getValidMovie());
		Sale sale = null;
		try {
			sale = movieService.buyMovie(movie.getMovieId(), USER_ID, VALID_CREDIT_CARD_NUMBER);

			Long saleId = sale.getSaleId();

			sale.setExpirationDate(sale.getExpirationDate().minusDays(SALE_EXPIRATION_DAYS + 1));
			updateSale(sale);
			assertThrows(SaleExpirationException.class, () -> movieService.findSale(saleId));
		} finally {
			// Clear Database (sale if it was created and movie)
			if (sale != null) {
				removeSale(sale.getSaleId());
			}
			removeMovie(movie.getMovieId());
		}

	}

}
