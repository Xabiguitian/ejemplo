namespace java es.udc.ws.movies.thrift

struct ThriftMovieDto {
    1: i64 movieId
    2: string title
    3: i16 runtime
    4: string description
    5: double price
}

struct ThriftSaleDto {
    1: i64 saleId
    2: i64 movieId
    3: string expirationDate
    4: string movieUrl
}

exception ThriftInputValidationException {
    1: string message
}

exception ThriftInstanceNotFoundException {
    1: string instanceId
    2: string instanceType
}

exception ThriftSaleExpirationException {
    1: i64 saleId
    2: string expirationDate
}

exception ThriftMovieNotRemovableException {
    1: i64 movieId
}
service ThriftMovieService {

   ThriftMovieDto addMovie(1: ThriftMovieDto movieDto) throws (1: ThriftInputValidationException e)

   void updateMovie(1: ThriftMovieDto movieDto) throws (1: ThriftInputValidationException e, 2: ThriftInstanceNotFoundException ee)

   void removeMovie(1: i64 movieId) throws (1: ThriftInstanceNotFoundException e, 2: ThriftMovieNotRemovableException ee)

   list<ThriftMovieDto> findMovies(1: string keywords)

   ThriftSaleDto buyMovie(1: i64 movieId, 2: string userId, 3: string creditCardNumber) throws (1: ThriftInputValidationException e, 2: ThriftInstanceNotFoundException ee)

   ThriftSaleDto findSale(1: i64 saleId) throws (1: ThriftInstanceNotFoundException e, 2: ThriftSaleExpirationException ee)
}