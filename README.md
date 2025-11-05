# ws-javaexamples

## Installing the development environment

- [Instructions for Unix-like operating systems (spanish)] (https://github.com/udc-fic-isd/isd-entorno/blob/main/LEEME_UNIX.md).
- [Instructions for Windows (spanish)] (https://github.com/udc-fic-isd/isd-entorno/blob/main/LEEME_WINDOWS.md).

## Initializing the database and building the examples

	mvn sql:execute install


## Running the ws-movies example

`ws-movies` requires the database server to be running.

### Running the ws-movies service with Maven/Jetty

	cd ws-movies/ws-movies-service
	mvn jetty:run

### Running the ws-movies service with Tomcat

- Copy the `.war` file (e.g. `ws-movies/ws-movies-service/target/ws-movies-service.war`) 
  to Tomcat's `webapps` directory.

- Start Tomcat:

      cd <TOMCAT_HOME>/bin
      startup.sh

- Shutdown Tomcat:

      shutdown.sh

### Running the ws-movies client application

Configure `ws-movies/ws-movies-client/src/main/resources/ConfigurationParameters.properties`
  to specify the client implementation (REST or Thrift) to be used and 
  the port number of the web server in the `endpointAddress` property 
  (9090 for Jetty, 8080 for Tomcat). Remember to process the resources 
  of the client module every time you change the configuration file 
  (for example, executing `mvn compile`).

> NOTE: In Windows use a terminal different from PowerShell

	cd ws-movies/ws-movies-client

- AddMovie

      mvn exec:java -Dexec.mainClass="es.udc.ws.movies.client.ui.MovieServiceClient" -Dexec.args="-a 'Star Wars V' 2 32 'Star Wars V - The empire strikes back' 6"
		
- UpdateMovie

      mvn exec:java -Dexec.mainClass="es.udc.ws.movies.client.ui.MovieServiceClient" -Dexec.args="-u 1 'Star Wars V' 2 32 'Star Wars V - The empire strikes back' 6"
	
- FindMovies

      mvn exec:java -Dexec.mainClass="es.udc.ws.movies.client.ui.MovieServiceClient" -Dexec.args="-f 'Star Wars'"

- BuyMovie

      mvn exec:java -Dexec.mainClass="es.udc.ws.movies.client.ui.MovieServiceClient" -Dexec.args="-b 1 'isd-user' '1234567891011213'"
		
- GetMovieURL

      mvn exec:java -Dexec.mainClass="es.udc.ws.movies.client.ui.MovieServiceClient" -Dexec.args="-g 1"

- RemoveMovie

      mvn exec:java -Dexec.mainClass="es.udc.ws.movies.client.ui.MovieServiceClient" -Dexec.args="-r 1"

- Examples to access to ws-movies resources from a REST client (9090 for Jetty, 8080 for Tomcat)
    - movies:   GET [http://localhost:9090/ws-movies-service/movies](http://localhost:9090/ws-movies-service/movies)
    - sale #1:   GET [http://localhost:9090/ws-movies-service/sales/1](http://localhost:9090/ws-movies-service/sales/1)