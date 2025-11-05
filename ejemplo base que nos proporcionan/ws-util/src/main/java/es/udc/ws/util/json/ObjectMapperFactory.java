package es.udc.ws.util.json;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperFactory {
    private static ObjectMapper mapper = new ObjectMapper();
    
    private ObjectMapperFactory() { } 
    
    public static ObjectMapper instance() { 
        return mapper; 
    }

}
