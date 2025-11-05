package es.udc.ws.movies.model.movieservice;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class MovieServiceFactory {

    private final static String CLASS_NAME_PARAMETER = "MovieServiceFactory.className";
    private static MovieService service = null;

    private MovieServiceFactory() {
    }

    @SuppressWarnings("rawtypes")
    private static MovieService getInstance() {
        try {
            String serviceClassName = ConfigurationParametersManager
                    .getParameter(CLASS_NAME_PARAMETER);
            Class serviceClass = Class.forName(serviceClassName);
            return (MovieService) serviceClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized static MovieService getService() {

        if (service == null) {
            service = getInstance();
        }
        return service;

    }
}
