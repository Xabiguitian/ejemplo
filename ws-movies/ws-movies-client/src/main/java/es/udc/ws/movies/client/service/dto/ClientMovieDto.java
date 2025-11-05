package es.udc.ws.movies.client.service.dto;


public class ClientMovieDto {

    private Long movieId;
    private String title;
    private short runtimeHours;
    private short runtimeMinutes;
    private String description;
    private float price;

    public ClientMovieDto() {
    }    
    
    public ClientMovieDto(Long movieId, String title, short runtimeHours, short runtimeMinutes,
        String description, float price) {

        this.movieId = movieId;
        this.title = title;
        this.runtimeHours = runtimeHours;
        this.runtimeMinutes = runtimeMinutes;
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

    public short getRuntimeHours() {
        return runtimeHours;
    }

    public void setRuntimeHours(short runtimeHours) {
        this.runtimeHours = runtimeHours;
    }

    public short getRuntimeMinutes() {
        return runtimeMinutes;
    }

    public void setRuntimeMinutes(short runtimeMinutes) {
        this.runtimeMinutes = runtimeMinutes;
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
                + ", runtime=" + runtimeHours + " hours, " + runtimeMinutes + " minutes, "
                + ", description=" + description + ", price=" + price + "]";
    }



}
