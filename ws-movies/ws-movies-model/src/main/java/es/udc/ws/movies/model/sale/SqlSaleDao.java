package es.udc.ws.movies.model.sale;

import java.sql.Connection;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public interface SqlSaleDao {

    public Sale create(Connection connection, Sale sale);

    public Sale find(Connection connection, Long saleId)
            throws InstanceNotFoundException;

    public boolean existsByMovieId(Connection connection, Long movieId);

    public void update(Connection connection, Sale sale)
            throws InstanceNotFoundException;

    public void remove(Connection connection, Long saleId)
            throws InstanceNotFoundException;
}
