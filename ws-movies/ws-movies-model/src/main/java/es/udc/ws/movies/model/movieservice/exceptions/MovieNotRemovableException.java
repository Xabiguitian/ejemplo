package es.udc.ws.movies.model.movieservice.exceptions;

public class MovieNotRemovableException extends Exception {
    private Long movieId;

    public MovieNotRemovableException(Long movieId) {
        super("Movie with id=\"" + movieId + "\n cannot be deleted because it has sales");
        this.movieId = movieId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

}
