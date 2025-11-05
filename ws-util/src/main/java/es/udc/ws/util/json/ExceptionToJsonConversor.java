package es.udc.ws.util.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public class ExceptionToJsonConversor {
    public static ObjectNode toInputValidationException(InputValidationException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "InputValidation");
        exceptionObject.put("message", ex.getMessage());

        return exceptionObject;
    }

    public static ObjectNode toInstanceNotFoundException(InstanceNotFoundException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();
        ObjectNode dataObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "InstanceNotFound");
        exceptionObject.put("instanceId", (ex.getInstanceId() != null) ?
                ex.getInstanceId().toString() : null);
        exceptionObject.put("instanceType",
                ex.getInstanceType().substring(ex.getInstanceType().lastIndexOf('.') + 1));

        return exceptionObject;
    }

}
