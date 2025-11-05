package es.udc.ws.movies.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.movies.restservice.dto.RestSaleDto;

public class JsonToRestSaleDtoConversor {

	public static ObjectNode toObjectNode(RestSaleDto sale) {

		ObjectNode saleNode = JsonNodeFactory.instance.objectNode();

        if (sale.getSaleId() != null) {
        	saleNode.put("saleId", sale.getSaleId());
        }
        saleNode.put("movieId", sale.getMovieId()).
        	put("movieUrl", sale.getMovieUrl()).
        	put("expirationDate", sale.getExpirationDate());
        
        return saleNode;
    }

}
