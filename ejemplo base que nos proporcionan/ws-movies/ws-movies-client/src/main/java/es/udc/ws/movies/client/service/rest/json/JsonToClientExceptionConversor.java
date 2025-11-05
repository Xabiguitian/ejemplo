package es.udc.ws.movies.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import es.udc.ws.movies.client.service.exceptions.ClientMovieNotRemovableException;
import es.udc.ws.movies.client.service.exceptions.ClientSaleExpirationException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;

public class JsonToClientExceptionConversor {

    public static Exception fromBadRequestErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InputValidation")) {
					return toInputValidationException(rootNode);
				} else {
					throw new ParsingException("Unrecognized error type: " + errorType);
				}
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InputValidationException toInputValidationException(JsonNode rootNode) {
        String message = rootNode.get("message").textValue();
        return new InputValidationException(message);
    }

	public static Exception fromNotFoundErrorCode(InputStream ex) throws ParsingException {
		try {
			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(ex);
			if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				String errorType = rootNode.get("errorType").textValue();
				if (errorType.equals("InstanceNotFound")) {
					return toInstanceNotFoundException(rootNode);
				} else {
					throw new ParsingException("Unrecognized error type: " + errorType);
				}
			}
		} catch (ParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}

    private static InstanceNotFoundException toInstanceNotFoundException(JsonNode rootNode) {
        String instanceId = rootNode.get("instanceId").textValue();
        String instanceType = rootNode.get("instanceType").textValue();
        return new InstanceNotFoundException(instanceId, instanceType);
    }

	public static Exception fromForbiddenErrorCode(InputStream ex) throws ParsingException {
		try {
			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(ex);
			if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				String errorType = rootNode.get("errorType").textValue();
				if (errorType.equals("MovieNotRemovable")) {
					return toMovieNotRemovableException(rootNode);
				} else {
					throw new ParsingException("Unrecognized error type: " + errorType);
				}
			}
		} catch (ParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}
    private static ClientMovieNotRemovableException toMovieNotRemovableException(JsonNode rootNode) {
        Long movieId = rootNode.get("movieId").longValue();
        return new ClientMovieNotRemovableException(movieId);
    }

	public static Exception fromGoneErrorCode(InputStream ex) throws ParsingException {
		try {
			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(ex);
			if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				String errorType = rootNode.get("errorType").textValue();
				if (errorType.equals("SaleExpiration")) {
					return toSaleExpirationException(rootNode);
				} else {
					throw new ParsingException("Unrecognized error type: " + errorType);
				}
			}
		} catch (ParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}
	private static ClientSaleExpirationException toSaleExpirationException(JsonNode rootNode) {
		Long saleId = rootNode.get("saleId").longValue();
		String expirationDateAsString = rootNode.get("expirationDate").textValue();
		LocalDateTime expirationDate = null;
		if (expirationDateAsString != null) {
			expirationDate = LocalDateTime.parse(expirationDateAsString);
		}
		return new ClientSaleExpirationException(saleId, expirationDate);
	}


}
