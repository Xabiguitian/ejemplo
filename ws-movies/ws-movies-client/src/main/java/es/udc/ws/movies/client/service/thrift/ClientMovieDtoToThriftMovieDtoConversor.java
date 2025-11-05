package es.udc.ws.movies.client.service.thrift;

import es.udc.ws.movies.client.service.dto.ClientMovieDto;
import es.udc.ws.movies.thrift.ThriftMovieDto;

import java.util.ArrayList;
import java.util.List;

public class ClientMovieDtoToThriftMovieDtoConversor {

    public static ThriftMovieDto toThriftMovieDto(
        ClientMovieDto clientMovieDto) {

        Long movieId = clientMovieDto.getMovieId();

        return new ThriftMovieDto(
            movieId == null ? -1 : movieId.longValue(),
            clientMovieDto.getTitle(),
            (short) (clientMovieDto.getRuntimeHours() * 60 + clientMovieDto.getRuntimeMinutes()),
            clientMovieDto.getDescription(),
            clientMovieDto.getPrice());

    }

    public static List<ClientMovieDto> toClientMovieDtos(List<ThriftMovieDto> movies) {

        List<ClientMovieDto> clientMovieDtos = new ArrayList<>(movies.size());

        for (ThriftMovieDto movie : movies) {
            clientMovieDtos.add(toClientMovieDto(movie));
        }
        return clientMovieDtos;

    }

    private static ClientMovieDto toClientMovieDto(ThriftMovieDto movie) {

        return new ClientMovieDto(
            movie.getMovieId(),
            movie.getTitle(),
            (short) (movie.getRuntime() / 60),
            (short) (movie.getRuntime() % 60),
            movie.getDescription(),
            Double.valueOf(movie.getPrice()).floatValue());

    }

}
