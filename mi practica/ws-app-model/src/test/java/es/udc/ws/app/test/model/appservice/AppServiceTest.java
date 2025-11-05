package es.udc.ws.app.test.model.appservice;

// Imports de la práctica
import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.surveyservice.SurveyService;
import es.udc.ws.app.model.surveyservice.SurveyServiceFactory;
import es.udc.ws.app.model.surveyservice.exceptions.FechaFinExpiradaException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
// NOTA: El import de JdbcUtils no es necesario en este fichero, solo en el DAO.

// Imports de JUnit
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

// Imports de Java
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp; // Necesario para el test de [FUNC-2]
import java.time.LocalDateTime;
import java.util.List; // Necesario para el test de [FUNC-2]

// Import estático para los Assertions
import static org.junit.jupiter.api.Assertions.*;

public class AppServiceTest {

    private static SurveyService surveyService = null;

    @BeforeAll
    public static void init() {

        surveyService = SurveyServiceFactory.getService();

    }


    private void clearTables() {
        try (Connection connection = DataSourceLocator.getDataSource("jdbc/ws-javaexamples-ds").getConnection()) {
            connection.setAutoCommit(true);
            connection.createStatement().executeUpdate("DELETE FROM Respuesta");
            connection.createStatement().executeUpdate("DELETE FROM Encuesta");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private Encuesta crearEncuestaDePrueba(String pregunta, LocalDateTime fechaFin)
            throws InputValidationException, FechaFinExpiradaException {
        return surveyService.crearEncuesta(new Encuesta(pregunta, fechaFin.withNano(0)));
    }



    @Test
    public void testCrearEncuestaBasico()
            throws InputValidationException, FechaFinExpiradaException, InstanceNotFoundException {


        clearTables();


        String pregunta = "Pregunta de prueba básica";
        LocalDateTime fechaFin = LocalDateTime.now().plusDays(10);
        Encuesta encuestaCreada = crearEncuestaDePrueba(pregunta, fechaFin);
        Encuesta encuestaDeBD = surveyService.buscarEncuestaPorId(encuestaCreada.getEncuestaId());
        assertEquals(encuestaCreada, encuestaDeBD);

        assertNotNull(encuestaDeBD.getEncuestaId());
        assertEquals(pregunta, encuestaDeBD.getPregunta());
        assertEquals(fechaFin.withNano(0), encuestaDeBD.getFechaFin());
        assertEquals(0, encuestaDeBD.getRespuestasPositivas());
        assertEquals(0, encuestaDeBD.getRespuestasNegativas());
        assertFalse(encuestaDeBD.isCancelada());
        assertNotNull(encuestaDeBD.getFechaCreacion());
    }

    @Test
    public void testCrearEncuestaFechaExpirada() {

        clearTables();


        String pregunta = "Pregunta con fecha expirada";

        LocalDateTime fechaFinExpirada = LocalDateTime.now().minusSeconds(1);

        assertThrows(FechaFinExpiradaException.class, () -> {
            surveyService.crearEncuesta(new Encuesta(pregunta, fechaFinExpirada));
        });
    }


    @Test
    public void testBuscarEncuestaPorId()
            throws InputValidationException, FechaFinExpiradaException, InstanceNotFoundException {

        clearTables();
        String pregunta = "Pregunta para buscar";
        LocalDateTime fechaFin = LocalDateTime.now().plusHours(1);
        Encuesta encuestaCreada = crearEncuestaDePrueba(pregunta, fechaFin);
        Encuesta encuestaEncontrada = surveyService.buscarEncuestaPorId(encuestaCreada.getEncuestaId());

        assertEquals(encuestaCreada, encuestaEncontrada);
        assertEquals(pregunta, encuestaEncontrada.getPregunta());
        assertThrows(InstanceNotFoundException.class, () -> {
            surveyService.buscarEncuestaPorId(encuestaCreada.getEncuestaId() + 1);
        });
    }


    @Test
    public void testBuscarEncuestas()
            throws InputValidationException, FechaFinExpiradaException, InterruptedException {

        clearTables();

        // 1. CREAMOS DATOS DE PRUEBA
        Encuesta e1 = crearEncuestaDePrueba("¿Te gusta el café?", LocalDateTime.now().plusDays(10));
        Encuesta e2 = crearEncuestaDePrueba("¿Te gusta el té?", LocalDateTime.now().plusDays(5));
        Encuesta e3 = crearEncuestaDePrueba("¿Te gusta el café con leche?", LocalDateTime.now().minusDays(1));

        //PRUEBA DE BÚSQUEDA POR KEYWORD (debe devolver e1 y e3, en orden e3, e1)
        List<Encuesta> encontradas1 = surveyService.buscarEncuestas("café", false);
        assertEquals(2, encontradas1.size());
        assertEquals(e3, encontradas1.get(0)); // e3 es la más reciente con "café"
        assertEquals(e1, encontradas1.get(1));

        // 3. PRUEBA DE BÚSQUEDA POR KEYWORD (CON FILTRO DE FECHA)
        List<Encuesta> encontradas2 = surveyService.buscarEncuestas("café", true);
        assertEquals(1, encontradas2.size());
        assertEquals(e1, encontradas2.get(0)); // Solo debe devolver e1 (e3 ha finalizado)

        // PRUEBA DE BÚSQUEDA VACÍA
        List<Encuesta> encontradas3 = surveyService.buscarEncuestas("", false);
        assertEquals(3, encontradas3.size());
        assertEquals(e3, encontradas3.get(0));
        assertEquals(e2, encontradas3.get(1));
        assertEquals(e1, encontradas3.get(2));

        // 5. PRUEBA DE BÚSQUEDA VACÍA (CON FILTRO DE FECHA)
        List<Encuesta> encontradas4 = surveyService.buscarEncuestas("", true);
        assertEquals(2, encontradas4.size());
        assertEquals(e2, encontradas4.get(0)); // e2 es la más reciente no finalizada
        assertEquals(e1, encontradas4.get(1));

        // 6. PRUEBA SIN RESULTADOS
        List<Encuesta> encontradas5 = surveyService.buscarEncuestas("palabraquenoexiste", false);
        assertEquals(0, encontradas5.size());
    }

}

