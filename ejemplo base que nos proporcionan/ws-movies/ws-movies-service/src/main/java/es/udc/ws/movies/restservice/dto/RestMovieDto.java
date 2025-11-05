package es.udc.ws.movies.restservice.dto;

public class RestMovieDto {

    private Long movieId;
    private String title;
    private short runtime;
    private String description;
    private float price;

    public RestMovieDto() {
    }

    public RestMovieDto(Long movieId, String title, short runtime,
                        String description, float price) {

        this.movieId = movieId;
        this.title = title;
        this.runtime = runtime;
        this.description = description;
        this.price = price;

    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public short getRuntime() {
        return runtime;
    }

    public void setRuntime(short runtime) {
        this.runtime = runtime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "MovieDto [movieId=" + movieId + ", title=" + title
                + ", runtime=" + runtime
                + ", description=" + description + ", price=" + price + "]";
    }

}
