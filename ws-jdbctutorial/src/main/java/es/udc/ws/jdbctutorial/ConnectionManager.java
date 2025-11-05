package es.udc.ws.jdbctutorial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private final static String DRIVER_URL = "jdbc:mysql://localhost/ws?useSSL=false&serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=true";
    private final static String USER = "ws";
    private final static String PASSWORD = "ws";

    private ConnectionManager() {
    }

    public final static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DRIVER_URL, USER, PASSWORD);
    }
}
