package es.udc.ws.movies.restservice.dto;

import es.udc.ws.movies.restservice.dto.RestSaleDto;
import es.udc.ws.movies.model.sale.Sale;

public class SaleToRestSaleDtoConversor {

    public static RestSaleDto toRestSaleDto(Sale sale) {
        return new RestSaleDto(sale.getSaleId(), sale.getMovieId(), sale
                .getExpirationDate().toString(), sale.getMovieUrl());
    }

}
