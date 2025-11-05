package es.udc.ws.util.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.json.ObjectMapperFactory;

public class ServletUtils {

    public static final int DEFAULT_BUFFER_SIZE = 1024 * 8;

    public static void writeServiceResponse(HttpServletResponse response, int responseCode, JsonNode rootNode,
                                            Map<String, String> headers) throws IOException {

        writeResponse(response, responseCode, "application/json", headers);

        if (rootNode != null) {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(response.getOutputStream(), rootNode);
        }
    }

    private static void writeResponse(HttpServletResponse response, int responseCode, String contentType,
                                      Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                response.setHeader(key, value);
            }
        }
        response.setStatus(responseCode);
        if (contentType != null) {
            response.setContentType(contentType);
        }
    }

    public static String normalizePath(String url) {
        if (url != null && url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        } else {
            return url;
        }
    }

    public static String getMandatoryParameter(HttpServletRequest req, String paramName)
            throws InputValidationException {
        String paramValue = req.getParameter(paramName);
        if (paramValue == null) {
            throw new InputValidationException("Invalid Request: " + "parameter " + paramName + " is mandatory");
        }
        return paramValue;
    }

    public static Long getMandatoryParameterAsLong(HttpServletRequest req, String paramName)
            throws InputValidationException {
        String paramValue;
        Long paramValueAsLong = null;
        if ((paramValue = getMandatoryParameter(req, paramName)) != null) {
            try {
                paramValueAsLong = Long.valueOf(paramValue);
            } catch (NumberFormatException ex) {
                throw new InputValidationException("Invalid Request: " + "parameter '" + paramName + "' is invalid '" +
                        paramValue + "'");
            }
        }
        return paramValueAsLong;
    }

    public static void checkEmptyPath(HttpServletRequest req) throws InputValidationException {
        String path = ServletUtils.normalizePath(req.getPathInfo());
        if (path != null && path.length() > 0) {
            throw new InputValidationException("Invalid Request: " + "invalid path " + path);
        }
    }

    public static Long getIdFromPath(HttpServletRequest req, String resourceName) throws IOException,
            InputValidationException {
        Long id = null;
        String path = ServletUtils.normalizePath(req.getPathInfo());
        if (path == null || path.length() == 0) {
            throw new InputValidationException("Invalid Request: " + "invalid " + resourceName + " id");
        }
        String idAsString = path.substring(1);
        try {
            id = Long.valueOf(idAsString);
        } catch (NumberFormatException ex) {
            throw new InputValidationException("Invalid Request: invalid " + resourceName + " id '" + idAsString + "'");
        }
        return id;
    }
}
