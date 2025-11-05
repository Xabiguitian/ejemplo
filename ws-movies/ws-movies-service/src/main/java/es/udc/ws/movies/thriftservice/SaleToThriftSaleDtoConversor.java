package es.udc.ws.movies.thriftservice;

import es.udc.ws.movies.model.sale.Sale;
import es.udc.ws.movies.thrift.ThriftSaleDto;

public class SaleToThriftSaleDtoConversor {

    public static ThriftSaleDto toThriftSaleDto(Sale sale) {

        return new ThriftSaleDto(sale.getSaleId(), sale.getSaleId(), sale.getExpirationDate().toString(),
                sale.getMovieUrl());

    }
}
