package es.udc.ws.util.sql;

import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.configuration.PropertiesUtil;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * A non-pooled implementation of a
 * <code>DataSource</code>. <p> <b>WARNING:</b> The only method implemented of
 * <code>DataSource</code> is
 * <code>getConnection()</code>. Rest of methods throw the SQLException 
 * <code>SQLFeatureNotSupportedException</code>. <p> Required configuration
 * properties: <ul> <li><code>SimpleDataSource/driverClassName</code>: it must
 * specify the full class name of the JBDC driver.</li> <li><code>SimpleDataSource/url</code>:
 * the database URL (without user and password).</li> <li><code>SimpleDataSource/user</code>:
 * the database user.</li> <li><code>SimpleDataSource/password</code>: the
 * user's password.</li> </ul>
 */
public class SimpleDataSource implements DataSource {

    private static final String CONFIGURATION_FILE = "SimpleDataSource.properties";
    private static final String URL_PARAMETER = "SimpleDataSource.url";
    private static final String USER_PARAMETER = "SimpleDataSource.user";
    private static final String PASSWORD_PARAMETER = "SimpleDataSource.password";
    private static String url;
    private static String user;
    private static String password;

    private synchronized void readConfiguration() {
        if (url == null) {
            try {
                Map<String, String> parameters = PropertiesUtil.readProperties(CONFIGURATION_FILE);

                /*
                 * Read configuration parameters.
                 */
                url = parameters.get(URL_PARAMETER);
                user = parameters.get(USER_PARAMETER);
                password = parameters.get(PASSWORD_PARAMETER);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        readConfiguration();
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Connection getConnection(String username, String password)
            throws SQLException {

        throw new SQLFeatureNotSupportedException("Not implemented");

    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented");
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Not implemented");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented");
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented");
    }
}
