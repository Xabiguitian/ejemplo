package es.udc.ws.movies.client.service.rest.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.movies.client.service.dto.ClientMovieDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

public class JsonToClientMovieDtoConversor {
	public static ObjectNode toObjectNode(ClientMovieDto movie) throws IOException {

		ObjectNode movieObject = JsonNodeFactory.instance.objectNode();

		if (movie.getMovieId() != null) {
			movieObject.put("movieId", movie.getMovieId());
		}
		movieObject.put("runtime", movie.getRuntimeHours() * 60 + movie.getRuntimeMinutes()).
			put("price", movie.getPrice()).
			put("title", movie.getTitle()).
			put("description", movie.getDescription());

		return movieObject;
	}

	public static ClientMovieDto toClientMovieDto(InputStream jsonMovie) throws ParsingException {
		try {

			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(jsonMovie);
			if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				return toClientMovieDto(rootNode);
			}
		} catch (ParsingException ex) {
			throw ex;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}

	public static List<ClientMovieDto> toClientMovieDtos(InputStream jsonMovies) throws ParsingException {
		try {

			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(jsonMovies);
			if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
				throw new ParsingException("Unrecognized JSON (array expected)");
			} else {
				ArrayNode moviesArray = (ArrayNode) rootNode;
				List<ClientMovieDto> movieDtos = new ArrayList<>(moviesArray.size());
				for (JsonNode movieNode : moviesArray) {
					movieDtos.add(toClientMovieDto(movieNode));
				}

				return movieDtos;
			}
		} catch (ParsingException ex) {
			throw ex;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}

	private static ClientMovieDto toClientMovieDto(JsonNode movieNode) throws ParsingException {
		if (movieNode.getNodeType() != JsonNodeType.OBJECT) {
			throw new ParsingException("Unrecognized JSON (object expected)");
		} else {
			ObjectNode movieObject = (ObjectNode) movieNode;

			JsonNode movieIdNode = movieObject.get("movieId");
			Long movieId = (movieIdNode != null) ? movieIdNode.longValue() : null;

			String title = movieObject.get("title").textValue().trim();
			String description = movieObject.get("description").textValue().trim();
			short runtime = movieObject.get("runtime").shortValue();
			float price = movieObject.get("price").floatValue();

			return new ClientMovieDto(movieId, title, (short) (runtime / 60), (short) (runtime % 60), description,
					price);
		}
	}

}
