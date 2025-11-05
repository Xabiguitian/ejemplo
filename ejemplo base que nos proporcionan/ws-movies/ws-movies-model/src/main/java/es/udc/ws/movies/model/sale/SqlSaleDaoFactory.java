package es.udc.ws.movies.model.sale;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

/**
 * A factory to get
 * <code>SqlSaleDao</code> objects. <p> Required configuration parameters: <ul>
 * <li><code>SqlSaleDaoFactory.className</code>: it must specify the full class
 * name of the class implementing
 * <code>SqlSaleDao</code>.</li> </ul>
 */
public class SqlSaleDaoFactory {

    private final static String CLASS_NAME_PARAMETER = "SqlSaleDaoFactory.className";
    private static SqlSaleDao dao = null;

    private SqlSaleDaoFactory() {
    }

    @SuppressWarnings("rawtypes")
    private static SqlSaleDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager
                    .getParameter(CLASS_NAME_PARAMETER);
            Class daoClass = Class.forName(daoClassName);
            return (SqlSaleDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized static SqlSaleDao getDao() {

        if (dao == null) {
            dao = getInstance();
        }
        return dao;

    }
}
