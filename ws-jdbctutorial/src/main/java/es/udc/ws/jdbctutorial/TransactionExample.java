package es.udc.ws.jdbctutorial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class TransactionExample {

    public static void main(String[] args) {

        try (Connection connection = ConnectionManager.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setAutoCommit(false);

                /* Create data for some movies. */
                String[] movieIdentifiers = new String[]{"movie-1", "movie-2",
                    "movie-3"};
                String[] titles = new String[]{"movie-1 title", "movie-2 title",
                    "movie-3 title"};
                short[] runtimes = new short[]{90, 120, 150};

                /* Create "preparedStatement". */
                String queryString = "INSERT INTO TutMovie "
                        + "(movieId, title, runtime) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement =
                        connection.prepareStatement(queryString);

                /* Insert movies in the database. */
                for (int i = 0; i < movieIdentifiers.length; i++) {

                    /* Fill "preparedStatement". */
                    preparedStatement.setString(1, movieIdentifiers[i]);
                    preparedStatement.setString(2, titles[i]);
                    preparedStatement.setShort(3, runtimes[i]);

                    /* Execute query. */
                    int insertedRows = preparedStatement.executeUpdate();

                    if (insertedRows != 1) {
                        throw new SQLException(movieIdentifiers[i]
                                + ": problems when inserting !!!!");
                    }

                }

                /* Commit. */
                connection.commit();

                System.out.println("Movies inserted");

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

    }
}
