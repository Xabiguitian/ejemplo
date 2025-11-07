package es.udc.ws.app.model.surveyservice;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class SurveyServiceFactory {

    private final static String CLASS_NAME_PARAMETER = "SurveyServiceFactory.className";
    private static SurveyService service = null;

    private SurveyServiceFactory() {
    }

    private static SurveyService getInstance() {
        try {
            String serviceClassName = ConfigurationParametersManager
                    .getParameter(CLASS_NAME_PARAMETER);
            Class<?> serviceClass = Class.forName(serviceClassName);
            return (SurveyService) serviceClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error creando instancia de SurveyService", e);
        }
    }

    public synchronized static SurveyService getService() {
        if (service == null) {
            service = getInstance();
        }
        return service;
    }
}
