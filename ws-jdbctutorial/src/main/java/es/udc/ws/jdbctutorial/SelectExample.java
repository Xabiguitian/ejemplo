package es.udc.ws.jdbctutorial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class SelectExample {

    public static void main (String[] args) {

    	try (Connection connection = ConnectionManager.getConnection()) {

            /* Create "preparedStatement". */
            String queryString = "SELECT movieId, title, runtime FROM TutMovie";
            PreparedStatement preparedStatement = connection.prepareStatement(queryString);

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            /* Iterate over matched rows. */
            while (resultSet.next()) {
                String movieIdentifier = resultSet.getString(1);
                String title = resultSet.getString(2);
                short runtime = resultSet.getShort(3);
                System.out.println("movieIdentifier = " + movieIdentifier +
                    " | title =  " + title + " | runtime =  " + runtime);
            }

    	} catch (Exception e) {
            e.printStackTrace(System.err);
        }

    }

}
