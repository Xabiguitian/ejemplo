package es.udc.ws.movies.client.service.dto;

import java.time.LocalDateTime;

public class ClientSaleDto {

    private Long saleId;
    private Long movieId;
    private LocalDateTime expirationDate;
    private String movieUrl;

    public ClientSaleDto() {
    }    
    
    public ClientSaleDto(Long saleId, Long movieId, LocalDateTime expirationDate,
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

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

}
