package es.udc.ws.util.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static synchronized Map<String, String> readProperties(String fileName) {

        Class<PropertiesUtil>
                PropertiesUtilClass =
                PropertiesUtil.class;
        ClassLoader classLoader =
                PropertiesUtilClass.getClassLoader();
        InputStream inputStream =
                classLoader.getResourceAsStream(fileName);
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
         * We use a "HashMap" instead of a "HashTable" because HashMap's
         * methods are *not* synchronized (so they are faster), and the
         * parameters are only read.
         */
        return (Map<String, String>) new HashMap(properties);

    }
}