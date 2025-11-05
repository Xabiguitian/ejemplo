package es.udc.ws.movies.model.movie;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

/**
 * A factory to get
 * <code>SqlMovieDao</code> objects. <p> Required configuration parameters: <ul>
 * <li><code>SqlMovieDaoFactory.className</code>: it must specify the full class
 * name of the class implementing
 * <code>SqlMovieDao</code>.</li> </ul>
 */
public class SqlMovieDaoFactory {

    private final static String CLASS_NAME_PARAMETER = "SqlMovieDaoFactory.className";
    private static SqlMovieDao dao = null;

    private SqlMovieDaoFactory() {
    }

    @SuppressWarnings("rawtypes")
    private static SqlMovieDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager
                    .getParameter(CLASS_NAME_PARAMETER);
            Class daoClass = Class.forName(daoClassName);
            return (SqlMovieDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized static SqlMovieDao getDao() {

        if (dao == null) {
            dao = getInstance();
        }
        return dao;

    }
}