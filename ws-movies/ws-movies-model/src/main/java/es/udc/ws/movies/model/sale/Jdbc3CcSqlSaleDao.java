package es.udc.ws.movies.model.sale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class Jdbc3CcSqlSaleDao extends AbstractSqlSaleDao {

    @Override
    public Sale create(Connection connection, Sale sale) {

        /* Create "queryString". */
        String queryString = "INSERT INTO Sale"
                + " (movieId, userId, expirationDate, creditCardNumber,"
                + " price, movieUrl, saleDate) VALUES (?, ?, ?, ?, ?, ?, ?)";


        try (PreparedStatement preparedStatement = connection.prepareStatement(
                        queryString, Statement.RETURN_GENERATED_KEYS)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, sale.getMovieId());
            preparedStatement.setString(i++, sale.getUserId());
			Timestamp date = sale.getExpirationDate() != null ? Timestamp.valueOf(sale.getExpirationDate()) : null;
            preparedStatement.setTimestamp(i++, date);
            preparedStatement.setString(i++, sale.getCreditCardNumber());
            preparedStatement.setFloat(i++, sale.getPrice());
            preparedStatement.setString(i++, sale.getMovieUrl());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(sale.getSaleDate()));

            /* Execute query. */
            preparedStatement.executeUpdate();

            /* Get generated identifier. */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long saleId = resultSet.getLong(1);

            /* Return sale. */
            return new Sale(saleId, sale.getMovieId(), sale.getUserId(),
                    sale.getExpirationDate(), sale.getCreditCardNumber(),
                    sale.getPrice(), sale.getMovieUrl(), sale.getSaleDate());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
