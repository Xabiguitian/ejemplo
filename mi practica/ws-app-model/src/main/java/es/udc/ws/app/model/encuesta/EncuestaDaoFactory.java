package es.udc.ws.app.model.encuesta;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class EncuestaDaoFactory {
    private final static String CLASS_NAME_PARAMETER = "EncuestaDaoFactory.className";
    private static SqlEncuestaDao dao = null;

    private EncuestaDaoFactory() {
    }

    private static SqlEncuestaDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager.getParameter(CLASS_NAME_PARAMETER);

            Class<?> daoClass = Class.forName(daoClassName);
            dao = (SqlEncuestaDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dao;
    }

    public synchronized static SqlEncuestaDao getDao() {
        if (dao == null) {
            dao = getInstance();
        }
        return dao;
    }
}
