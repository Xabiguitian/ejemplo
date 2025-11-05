package es.udc.ws.app.model.surveyservice;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class SurveyServiceFactory {

    private final static String CLASS_NAME_PARAMETER = "SurveyServiceFactory.className";
    private static SurveyService service = null;

    private SurveyServiceFactory() {
    }

    @SuppressWarnings("rawtypes")
    private static SurveyService getInstance() {
        try {
            // Recupera el nombre de la clase concreta del servicio desde el fichero de configuración
            String serviceClassName = ConfigurationParametersManager
                    .getParameter(CLASS_NAME_PARAMETER);

            Class serviceClass = Class.forName(serviceClassName);
            return (SurveyService) serviceClass.getDeclaredConstructor().newInstance();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Devuelve la instancia única del servicio de encuestas (patrón Singleton).
     */
    public synchronized static SurveyService getService() {
        if (service == null) {
            service = getInstance();
        }
        return service;
    }
}
