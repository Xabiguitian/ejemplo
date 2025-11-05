package es.udc.ws.movies.model.movieservice.exceptions;

import java.time.LocalDateTime;

@SuppressWarnings("serial")
public class SaleExpirationException extends Exception {

    private Long saleId;
    private LocalDateTime expirationDate;

    public SaleExpirationException(Long saleId, LocalDateTime expirationDate) {
        super("Sale with id=\"" + saleId + 
              "\" has expired (expirationDate = \"" + 
              expirationDate + "\")");
        this.saleId = saleId;
        this.expirationDate = expirationDate;
    }

    public Long getSaleId() {
        return saleId;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }
}