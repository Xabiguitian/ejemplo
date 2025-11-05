package es.udc.ws.movies.client.service.exceptions;

public class ClientMovieNotRemovableException extends Exception {
    private Long movieId;

    public ClientMovieNotRemovableException(Long movieId) {
        super("Movie with id=\"" + movieId + "\" cannot be deleted because it has sales");
        this.movieId = movieId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
}
