package es.udc.ws.movies.client.service.exceptions;

import java.time.LocalDateTime;

@SuppressWarnings("serial")
public class ClientSaleExpirationException extends Exception {

    private Long saleId;
    private LocalDateTime expirationDate;

    public ClientSaleExpirationException(Long saleId, LocalDateTime expirationDate) {
        super("Sale with id=\"" + saleId
                + "\" has expired (expirationDate = \""
                + expirationDate + "\")");
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
