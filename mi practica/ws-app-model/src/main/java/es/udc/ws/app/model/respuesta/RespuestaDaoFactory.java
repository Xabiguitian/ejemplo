package es.udc.ws.app.model.respuesta;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class RespuestaDaoFactory {
    private final static String CLASS_NAME_PARAMETER = "RespuestaDaoFactory.className";
    private static SqlRespuestaDao dao = null;

    private RespuestaDaoFactory() {
    }


    private static SqlRespuestaDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager.getParameter(CLASS_NAME_PARAMETER);
            Class<?> daoClass = Class.forName(daoClassName);
            dao = (SqlRespuestaDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dao;
    }

    public synchronized static SqlRespuestaDao getDao() {
        if (dao == null) {
            dao = getInstance();
        }
        return dao;
    }
}
