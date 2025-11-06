package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.respuesta.Respuesta;
import es.udc.ws.app.model.surveyservice.SurveyService;
import es.udc.ws.app.model.surveyservice.SurveyServiceFactory;
import es.udc.ws.app.model.surveyservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.surveyservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.surveyservice.exceptions.FechaFinExpiradaException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.SimpleDataSource;
import es.udc.ws.util.sql.DataSourceLocator;
import java.sql.Connection;
import es.udc.ws.app.model.encuesta.Jdbc3CcSqlEncuestaDao;
import es.udc.ws.util.exceptions.InstanceNotFoundException;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static es.udc.ws.app.model.util.ModelConstants.SURVEY_DATA_SOURCE;

public class AppServiceTest {

    private static SurveyService surveyService = null;

    @BeforeAll
    public static void init() {
        System.setProperty("test.mode", "true");
        DataSource dataSource = new SimpleDataSource();
        DataSourceLocator.addDataSource(SURVEY_DATA_SOURCE, dataSource);
        surveyService = SurveyServiceFactory.getService();
        System.out.println("[DEBUG] --> DataSource y SurveyService inicializados correctamente.");
    }

    private Encuesta crearEncuestaDePrueba(String pregunta, LocalDateTime fechaFin)
            throws InputValidationException, FechaFinExpiradaException {
        System.out.println("[DEBUG] --> Creando encuesta de prueba: " + pregunta);
        Encuesta e = surveyService.crearEncuesta(new Encuesta(pregunta, fechaFin.withNano(0)));
        System.out.println("[DEBUG] --> Encuesta creada con ID: " + e.getEncuestaId());
        return e;
    }

    @Test
    public void testCrearEncuestaBasico()
            throws InputValidationException, FechaFinExpiradaException, InstanceNotFoundException {

        String pregunta = "Pregunta de prueba básica";
        LocalDateTime fechaFin = LocalDateTime.now().plusDays(10);

        Encuesta encuestaCreada = crearEncuestaDePrueba(pregunta, fechaFin);
        Encuesta encuestaDeBD = surveyService.buscarEncuestaPorId(encuestaCreada.getEncuestaId());

        System.out.println("[DEBUG] --> Encuesta creada: " + encuestaCreada);
        System.out.println("[DEBUG] --> Encuesta recuperada de BD: " + encuestaDeBD);

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
        String pregunta = "Pregunta con fecha expirada";
        LocalDateTime fechaFinExpirada = LocalDateTime.now().minusSeconds(1);

        // 🔧 Desactivar modo test temporalmente
        System.setProperty("test.mode", "false");

        assertThrows(FechaFinExpiradaException.class, () -> {
            surveyService.crearEncuesta(new Encuesta(pregunta, fechaFinExpirada));
        });

        // 🔁 Reactivar modo test para los siguientes tests
        System.setProperty("test.mode", "true");
    }


    @Test
    public void testBuscarEncuestaPorId()
            throws InputValidationException, FechaFinExpiradaException, InstanceNotFoundException {

        String pregunta = "Pregunta para buscar";
        LocalDateTime fechaFin = LocalDateTime.now().plusHours(1);
        Encuesta encuestaCreada = crearEncuestaDePrueba(pregunta, fechaFin);
        Encuesta encuestaEncontrada = surveyService.buscarEncuestaPorId(encuestaCreada.getEncuestaId());

        System.out.println("[DEBUG] --> Buscando encuesta con ID: " + encuestaCreada.getEncuestaId());
        System.out.println("[DEBUG] --> Encuesta encontrada: " + encuestaEncontrada);

        assertEquals(encuestaCreada, encuestaEncontrada);
        assertEquals(pregunta, encuestaEncontrada.getPregunta());
        assertThrows(InstanceNotFoundException.class, () -> {
            surveyService.buscarEncuestaPorId(encuestaCreada.getEncuestaId() + 999);
        });
    }

    @Test
    public void testBuscarEncuestas()
            throws InputValidationException, FechaFinExpiradaException {

        crearEncuestaDePrueba("¿Te gusta el café?", LocalDateTime.now().plusDays(10));
        crearEncuestaDePrueba("¿Te gusta el té?", LocalDateTime.now().plusDays(5));
        crearEncuestaDePrueba("¿Te gusta el café con leche?", LocalDateTime.now().minusDays(1)); // expirada

        List<Encuesta> encontradasCafe = surveyService.buscarEncuestas("café");
        System.out.println("[DEBUG] --> Encuestas encontradas con palabra 'café': " + encontradasCafe.size());
        assertTrue(encontradasCafe.stream().allMatch(e -> e.getPregunta().contains("café")));

        List<Encuesta> todas = surveyService.buscarEncuestas("");
        System.out.println("[DEBUG] --> Total encuestas activas encontradas: " + todas.size());
        assertTrue(todas.size() >= 2);
    }

    @Test
    public void testResponderEncuesta()
            throws Exception {

        Encuesta encuesta = crearEncuestaDePrueba("¿Te gusta el café?", LocalDateTime.now().plusDays(2));

        Respuesta respuesta = surveyService.responderEncuesta(encuesta.getEncuestaId(), "empleado@udc.es", true);

        System.out.println("[DEBUG] --> Respuesta creada: " + respuesta);

        assertNotNull(respuesta.getRespuestaId());
        assertEquals(encuesta.getEncuestaId(), respuesta.getEncuestaId());
        assertEquals("empleado@udc.es", respuesta.getEmailEmpleado());
        assertTrue(respuesta.isAfirmativa());
        assertNotNull(respuesta.getFechaRespuesta());
    }

    @Test
    public void testResponderEncuestaInvalida()
            throws Exception {

        Encuesta encuesta = crearEncuestaDePrueba("¿Te gusta programar?", LocalDateTime.now().plusDays(2));

        assertThrows(InstanceNotFoundException.class, () -> {
            surveyService.responderEncuesta(-1L, "empleado@udc.es", true);
        });

        Encuesta expirada = crearEncuestaDePrueba("¿Encuesta expirada?", LocalDateTime.now().minusDays(1));
        assertThrows(EncuestaFinalizadaException.class, () -> {
            surveyService.responderEncuesta(expirada.getEncuestaId(), "empleado@udc.es", true);
        });

        surveyService.cancelarEncuesta(encuesta.getEncuestaId());
        assertThrows(EncuestaCanceladaException.class, () -> {
            surveyService.responderEncuesta(encuesta.getEncuestaId(), "empleado@udc.es", false);
        });

        assertThrows(InputValidationException.class, () -> {
            surveyService.responderEncuesta(encuesta.getEncuestaId(), "", true);
        });
    }

    @Test
    public void testCambiarRespuestaEncuesta()
            throws Exception {

        Encuesta encuesta = crearEncuestaDePrueba("¿Te gusta Java?", LocalDateTime.now().plusDays(5));

        surveyService.responderEncuesta(encuesta.getEncuestaId(), "empleado@udc.es", true);
        surveyService.responderEncuesta(encuesta.getEncuestaId(), "empleado@udc.es", false);

        List<Respuesta> respuestas = surveyService.obtenerRespuestas(encuesta.getEncuestaId(), false);
        assertEquals(1, respuestas.size());
        assertFalse(respuestas.get(0).isAfirmativa());
    }

    @Test
    public void testEncuestaExpirada() throws Exception {
        Encuesta expirada = crearEncuestaDePrueba("¿Encuesta expirada?", LocalDateTime.now().minusHours(1));
        assertThrows(EncuestaFinalizadaException.class, () -> {
            surveyService.responderEncuesta(expirada.getEncuestaId(), "empleado@udc.es", true);
        });
    }

    @Test
    public void testEncuestaCancelada() throws Exception {
        Encuesta encuesta = crearEncuestaDePrueba("¿Encuesta cancelable?", LocalDateTime.now().plusDays(2));
        surveyService.cancelarEncuesta(encuesta.getEncuestaId());

        assertThrows(EncuestaCanceladaException.class, () -> {
            surveyService.cancelarEncuesta(encuesta.getEncuestaId());
        });

        assertThrows(EncuestaCanceladaException.class, () -> {
            surveyService.responderEncuesta(encuesta.getEncuestaId(), "empleado@udc.es", true);
        });
    }

    @Test
    public void testBuscarEncuestasNoIncluyeCanceladasNiExpiradas()
            throws Exception {

        Encuesta activa = crearEncuestaDePrueba("Activa", LocalDateTime.now().plusDays(3));
        Encuesta cancelada = crearEncuestaDePrueba("Cancelada", LocalDateTime.now().plusDays(3));
        Encuesta expirada = crearEncuestaDePrueba("Expirada", LocalDateTime.now().minusDays(1));

        surveyService.cancelarEncuesta(cancelada.getEncuestaId());

        List<Encuesta> encontradas = surveyService.buscarEncuestas("");
        System.out.println("[DEBUG] --> Encuestas activas encontradas (filtro): " + encontradas.size());

        assertTrue(encontradas.contains(activa));
        assertFalse(encontradas.contains(expirada));
        assertFalse(encontradas.contains(cancelada));
    }

    @Test
    public void testRespuestaModelCoverage() {
        // --- Constructores ---
        Respuesta r1 = new Respuesta(1L, 2L, "user@udc.es", true, LocalDateTime.now().withNano(0));
        assertEquals(1L, r1.getRespuestaId());
        assertEquals(2L, r1.getEncuestaId());
        assertEquals("user@udc.es", r1.getEmailEmpleado());
        assertTrue(r1.isAfirmativa());
        assertNotNull(r1.getFechaRespuesta());

        Respuesta r2 = new Respuesta(2L, "test@udc.es", false);
        assertNull(r2.getRespuestaId());
        assertEquals(2L, r2.getEncuestaId());
        assertEquals("test@udc.es", r2.getEmailEmpleado());
        assertFalse(r2.isAfirmativa());
        assertNotNull(r2.getFechaRespuesta());


        r2.setRespuestaId(10L);
        r2.setEncuestaId(20L);
        r2.setEmailEmpleado("nuevo@udc.es");
        r2.setAfirmativa(true);
        r2.setFechaRespuesta(LocalDateTime.now().minusDays(1));
        assertEquals(10L, r2.getRespuestaId());
        assertEquals(20L, r2.getEncuestaId());
        assertEquals("nuevo@udc.es", r2.getEmailEmpleado());
        assertTrue(r2.isAfirmativa());


        Respuesta r3 = new Respuesta(1L, 2L, "user@udc.es", true, r1.getFechaRespuesta());
        assertTrue(r1.equals(r3));
        assertFalse(r1.equals(null));
        assertFalse(r1.equals("string"));
        r3.setRespuestaId(999L);
        assertFalse(r1.equals(r3));
        r1.setRespuestaId(null);
        r3.setRespuestaId(null);
        assertTrue(r1.equals(r3));


        int hc1 = r1.hashCode();
        int hc2 = r3.hashCode();
        assertEquals(hc1, hc2);
        r3.setEmailEmpleado(null);
        r3.setEncuestaId(null);
        r3.setFechaRespuesta(null);
        r3.setAfirmativa(false);
        assertNotEquals(r1.hashCode(), r3.hashCode());


        assertTrue(r1.equals(r1));


        Respuesta r4 = new Respuesta(r1.getRespuestaId(), r1.getEncuestaId(),
                r1.getEmailEmpleado(), r1.isAfirmativa(),
                r1.getFechaRespuesta());
        assertTrue(r1.equals(r4));

        assertFalse(r1.equals(null));

    }

    @Test
    public void testHashCodeConCamposNulos() {
        Respuesta r = new Respuesta(
                null,
                null,
                null,
                false,
                null
        );
        int hash = r.hashCode();
        assertNotEquals(0, hash);
    }

    @Test
    public void testEncuestaModelCoverage() {
        LocalDateTime ahora = LocalDateTime.now().withNano(0);
        LocalDateTime fin = ahora.plusDays(5);

        Encuesta e1 = new Encuesta("¿Te gusta Java?", fin);
        e1.setEncuestaId(1L);
        e1.setFechaCreacion(ahora);
        e1.setRespuestasPositivas(5);
        e1.setRespuestasNegativas(3);
        e1.setCancelada(false);

        
        assertEquals(1L, e1.getEncuestaId());
        assertEquals("¿Te gusta Java?", e1.getPregunta());
        assertEquals(ahora, e1.getFechaCreacion());
        assertEquals(fin, e1.getFechaFin());
        assertEquals(5, e1.getRespuestasPositivas());
        assertEquals(3, e1.getRespuestasNegativas());
        assertFalse(e1.isCancelada());
        assertEquals(8, e1.getTotalRespuestas());


        Encuesta e2 = new Encuesta(1L, "¿Te gusta Java?", ahora, fin, 5, 3, false);
        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());


        Encuesta e3 = new Encuesta(2L, "¿Te gusta Kotlin?", ahora, fin, 5, 3, false);
        assertNotEquals(e1, e3);


        assertNotEquals(e1, null);
        assertNotEquals(e1, "no es una encuesta");


    }

    @Test
    public void testAbstractSqlEncuestaDaoCoverage() throws Exception {

        var ds = DataSourceLocator.getDataSource(SURVEY_DATA_SOURCE);
        try (Connection conn = ds.getConnection()) {
            Jdbc3CcSqlEncuestaDao dao = new Jdbc3CcSqlEncuestaDao();


            Encuesta nueva = new Encuesta("Encuesta DAO", LocalDateTime.now().plusDays(2));
            Encuesta creada = dao.create(conn, nueva);
            assertNotNull(creada.getEncuestaId());


            Encuesta encontrada = dao.find(conn, creada.getEncuestaId());
            assertEquals(creada.getPregunta(), encontrada.getPregunta());


            assertThrows(InstanceNotFoundException.class, () -> {
                dao.find(conn, -999L);
            });


            encontrada.setPregunta("Encuesta DAO modificada");
            dao.update(conn, encontrada);
            Encuesta actualizada = dao.find(conn, encontrada.getEncuestaId());
            assertEquals("Encuesta DAO modificada", actualizada.getPregunta());


            Encuesta falsa = new Encuesta(99999L, "Falsa", LocalDateTime.now(), LocalDateTime.now().plusDays(1), 0, 0, false);
            assertThrows(InstanceNotFoundException.class, () -> dao.update(conn, falsa));


            var todas = dao.findByKeywords("DAO", false);
            assertTrue(todas.stream().anyMatch(e -> e.getPregunta().contains("DAO")));


            var noFinalizadas = dao.findByKeywords("DAO", true);
            assertTrue(noFinalizadas.stream().allMatch(e -> e.getFechaFin().isAfter(LocalDateTime.now())));


            dao.remove(conn, creada.getEncuestaId());
            assertThrows(InstanceNotFoundException.class, () -> dao.find(conn, creada.getEncuestaId()));

            assertThrows(InstanceNotFoundException.class, () -> dao.remove(conn, -1L));
        }
    }




}
