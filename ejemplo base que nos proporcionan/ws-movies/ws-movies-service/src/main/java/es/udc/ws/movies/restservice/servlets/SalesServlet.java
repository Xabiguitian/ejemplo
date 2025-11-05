package es.udc.ws.movies.restservice.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import es.udc.ws.movies.restservice.dto.RestSaleDto;
import es.udc.ws.movies.model.movieservice.MovieServiceFactory;
import es.udc.ws.movies.model.movieservice.exceptions.SaleExpirationException;
import es.udc.ws.movies.model.sale.Sale;
import es.udc.ws.movies.restservice.json.MoviesExceptionToJsonConversor;
import es.udc.ws.movies.restservice.json.JsonToRestSaleDtoConversor;
import es.udc.ws.movies.restservice.dto.SaleToRestSaleDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;

@SuppressWarnings("serial")
public class SalesServlet extends RestHttpServletTemplate {

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        ServletUtils.checkEmptyPath(req);
        Long movieId = ServletUtils.getMandatoryParameterAsLong(req,"movieId");
        String userId = ServletUtils.getMandatoryParameter(req,"userId");
        String creditCardNumber = ServletUtils.getMandatoryParameter(req,"creditCardNumber");

        Sale sale = MovieServiceFactory.getService().buyMovie(movieId, userId, creditCardNumber);

        RestSaleDto saleDto = SaleToRestSaleDtoConversor.toRestSaleDto(sale);
        String saleURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + sale.getSaleId().toString();
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", saleURL);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                JsonToRestSaleDtoConversor.toObjectNode(saleDto), headers);
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        Long saleId = ServletUtils.getIdFromPath(req, "sale");

        Sale sale;
        try {
            sale = MovieServiceFactory.getService().findSale(saleId);
        } catch (SaleExpirationException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_GONE,
                    MoviesExceptionToJsonConversor.toSaleExpirationException(ex),
                    null);
            return;
        }

        RestSaleDto saleDto = SaleToRestSaleDtoConversor.toRestSaleDto(sale);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                JsonToRestSaleDtoConversor.toObjectNode(saleDto), null);
    }
}
