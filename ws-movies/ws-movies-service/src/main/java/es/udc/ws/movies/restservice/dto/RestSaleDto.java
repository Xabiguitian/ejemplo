package es.udc.ws.movies.restservice.dto;

public class RestSaleDto {

    private Long saleId;
    private Long movieId;
    private String expirationDate;
    private String movieUrl;

    public RestSaleDto() {
    }

    public RestSaleDto(Long saleId, Long movieId, String expirationDate,
                       String movieUrl) {
        this.expirationDate = expirationDate;
        this.movieId = movieId;
        this.movieUrl = movieUrl;
        this.saleId = saleId;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

}
