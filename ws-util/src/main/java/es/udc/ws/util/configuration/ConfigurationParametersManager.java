package es.udc.ws.util.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class ConfigurationParametersManager {

    private static final String CONFIGURATION_FILE = "ConfigurationParameters.properties";
    private static Map<String, String> parameters;

    private ConfigurationParametersManager() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static synchronized Map<String, String> getParameters() {

        if (parameters == null) {
            parameters = PropertiesUtil.readProperties(CONFIGURATION_FILE);
        }
        return parameters;

    }

    public static String getParameter(String name) {

        return getParameters().get(name);

    }
}
