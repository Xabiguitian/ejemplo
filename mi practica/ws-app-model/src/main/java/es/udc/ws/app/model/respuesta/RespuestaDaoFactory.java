package es.udc.ws.app.model.respuesta;

// Esta clase es idéntica a EncuestaDaoFactory, pero para Respuesta
public class RespuestaDaoFactory {
    private final static String CLASS_NAME_PARAMETER = "RespuestaDaoFactory.className";
    private static SqlRespuestaDao dao = null;

    private RespuestaDaoFactory() {
    }

    @SuppressWarnings("unchecked")
    private static SqlRespuestaDao getInstance() {
        try {
            String daoClassName = System.getProperty(CLASS_NAME_PARAMETER);
            if (daoClassName == null) {
                // Apuntamos a la implementación SQL que crearemos en el siguiente paso
                daoClassName = "es.udc.ws.app.model.respuesta.SqlRespuestaDao";
            }
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
