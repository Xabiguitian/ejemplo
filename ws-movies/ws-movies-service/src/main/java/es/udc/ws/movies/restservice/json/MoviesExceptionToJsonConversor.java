package es.udc.ws.movies.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.movies.model.movieservice.exceptions.MovieNotRemovableException;
import es.udc.ws.movies.model.movieservice.exceptions.SaleExpirationException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public class MoviesExceptionToJsonConversor {

    public static ObjectNode toSaleExpirationException(SaleExpirationException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "SaleExpiration");
        exceptionObject.put("saleId", (ex.getSaleId() != null) ? ex.getSaleId() : null);
        if (ex.getExpirationDate() != null) {
            exceptionObject.put("expirationDate", ex.getExpirationDate().toString());
        } else {
            exceptionObject.set("expirationDate", null);
        }

        return exceptionObject;
    }

    public static ObjectNode toMovieNotRemovableException(MovieNotRemovableException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "MovieNotRemovable");
        exceptionObject.put("movieId", (ex.getMovieId() != null) ? ex.getMovieId() : null);

        return exceptionObject;
    }

}
