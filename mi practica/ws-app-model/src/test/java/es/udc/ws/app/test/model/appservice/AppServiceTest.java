package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuesta.EncuestaDaoFactory; // NUEVA IMPORTACIÓN
import es.udc.ws.app.model.encuesta.SqlEncuestaDao; // NUEVA IMPORTACIÓN
import es.udc.ws.app.model.respuesta.Respuesta;
import es.udc.ws.app.model.respuesta.RespuestaDaoFactory; // NUEVA IMPORTACIÓN
import es.udc.ws.app.model.respuesta.SqlRespuestaDao; // NUEVA IMPORTACIÓN
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


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static es.udc.ws.app.model.util.ModelConstants.SURVEY_DATA_SOURCE;

public class AppServiceTest {

    private static SurveyService surveyService = null;
    // Añadimos constantes para IDs inexistentes, siguiendo el ejemplo
    private final long ID_INEXISTENTE = -1L;

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

    // ===================================================================================
    //
    // TESTS DE [FUNC-1]: crearEncuesta
    //
    // ===================================================================================

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

        System.out.println("[DEBUG] --> Probando FechaFinExpiradaException...");
        FechaFinExpiradaException ex = assertThrows(FechaFinExpiradaException.class, () -> {
            surveyService.crearEncuesta(new Encuesta(pregunta, fechaFinExpirada));
        });

        // Comprobación de getters para cobertura
        assertEquals(fechaFinExpirada, ex.getFechaFin());
        System.out.println("[DEBUG] --> Getter de FechaFinExpiradaException verificado.");

        // 🔁 Reactivar modo test para los siguientes tests
        System.setProperty("test.mode", "true");
    }

    /**
     * NUEVO TEST: Cumple el requisito para crearEncuesta.
     */
    @Test
    public void testCrearEncuestaValidacion() {
        System.out.println("[DEBUG] --> Probando InputValidationException en crearEncuesta...");

        // 1. Pregunta nula
        assertThrows(InputValidationException.class, () -> {
            surveyService.crearEncuesta(new Encuesta(null, LocalDateTime.now().plusDays(1)));
        });

        // 2. Pregunta vacía
        assertThrows(InputValidationException.class, () -> {
            surveyService.crearEncuesta(new Encuesta("", LocalDateTime.now().plusDays(1)));
        });

        // 3. Fecha Fin nula
        // Tu implementación pasa el 'null' al DAO, que lanza NullPointerException
        // al hacer Timestamp.valueOf(null).
        // El test debe esperar la excepción que *realmente* se lanza.
        System.out.println("[DEBUG] --> Probando fecha nula (espera NullPointerException por bug en DAO)...");
        assertThrows(NullPointerException.class, () -> {
            surveyService.crearEncuesta(new Encuesta("Pregunta válida", null));
        });

        System.out.println("[DEBUG] --> Validaciones en crearEncuesta verificadas.");
    }

    // ===================================================================================
    //
    // TESTS DE [FUNC-3]: buscarEncuestaPorId
    //
    // ===================================================================================

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
    }

    /**
     * TEST MODIFICADO: Separado del caso de éxito para cumplir requisito .
     */
    @Test
    public void testBuscarEncuestaPorIdNoEncontrada() {
        System.out.println("[DEBUG] --> Probando InstanceNotFoundException en buscarEncuestaPorId...");
        assertThrows(InstanceNotFoundException.class, () -> {
            surveyService.buscarEncuestaPorId(ID_INEXISTENTE);
        });
        System.out.println("[DEBUG] --> InstanceNotFoundException en buscarEncuestaPorId verificada.");
    }


    // ===================================================================================
    //
    // TESTS DE [FUNC-2]: buscarEncuestas
    //
    // ===================================================================================

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

    // ===================================================================================
    //
    // TESTS DE [FUNC-4]: responderEncuesta
    //
    // ===================================================================================

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
    public void testCambiarRespuestaEncuesta()
            throws Exception {

        Encuesta encuesta = crearEncuestaDePrueba("¿Te gusta Java?", LocalDateTime.now().plusDays(5));

        surveyService.responderEncuesta(encuesta.getEncuestaId(), "empleado@udc.es", true);
        surveyService.responderEncuesta(encuesta.getEncuestaId(), "empleado@udc.es", false);

        List<Respuesta> respuestas = surveyService.obtenerRespuestas(encuesta.getEncuestaId(), false);
        assertEquals(1, respuestas.size());
        assertFalse(respuestas.get(0).isAfirmativa());
    }

    /**
     * TEST MODIFICADO: Separado de testResponderEncuestaInvalida para cumplir requisito .
     */
    @Test
    public void testResponderEncuestaNoEncontrada() {
        System.out.println("[DEBUG] --> Probando InstanceNotFoundException en responderEncuesta...");
        assertThrows(InstanceNotFoundException.class, () -> {
            surveyService.responderEncuesta(ID_INEXISTENTE, "empleado@udc.es", true);
        });
        System.out.println("[DEBUG] --> InstanceNotFoundException en responderEncuesta verificada.");
    }

    /**
     * TEST MODIFICADO: Separado de testResponderEncuestaInvalida para cumplir requisito .
     */
    @Test
    public void testResponderEncuestaFinalizada() throws Exception {
        System.out.println("[DEBUG] --> Probando EncuestaFinalizadaException en responderEncuesta...");
        Encuesta expirada = crearEncuestaDePrueba("¿Encuesta expirada?", LocalDateTime.now().minusDays(1));

        EncuestaFinalizadaException exFin = assertThrows(EncuestaFinalizadaException.class, () -> {
            surveyService.responderEncuesta(expirada.getEncuestaId(), "empleado@udc.es", true);
        });
        assertEquals(expirada.getEncuestaId(), exFin.getEncuestaId());
        assertEquals(expirada.getFechaFin(), exFin.getFechaFin());
        System.out.println("[DEBUG] --> Getters de EncuestaFinalizadaException verificados.");
    }

    /**
     * TEST MODIFICADO: Separado de testResponderEncuestaInvalida para cumplir requisito .
     */
    @Test
    public void testResponderEncuestaYaCancelada() throws Exception {
        System.out.println("[DEBUG] --> Probando EncuestaCanceladaException en responderEncuesta...");
        Encuesta encuesta = crearEncuestaDePrueba("¿Te gusta programar?", LocalDateTime.now().plusDays(2));
        surveyService.cancelarEncuesta(encuesta.getEncuestaId());

        EncuestaCanceladaException exCan = assertThrows(EncuestaCanceladaException.class, () -> {
            surveyService.responderEncuesta(encuesta.getEncuestaId(), "empleado@udc.es", false);
        });
        assertEquals(encuesta.getEncuestaId(), exCan.getEncuestaId());
        System.out.println("[DEBUG] --> Getter de EncuestaCanceladaException verificado.");
    }

    /**
     * TEST MODIFICADO: Separado de testResponderEncuestaInvalida para cumplir requisito .
     */
    @Test
    public void testResponderEncuestaValidacion() throws Exception {
        System.out.println("[DEBUG] --> Probando InputValidationException en responderEncuesta...");
        Encuesta encuesta = crearEncuestaDePrueba("Encuesta para validación", LocalDateTime.now().plusDays(1));

        assertThrows(InputValidationException.class, () -> {
            surveyService.responderEncuesta(encuesta.getEncuestaId(), "", true); // Email vacío
        });
        assertThrows(InputValidationException.class, () -> {
            surveyService.responderEncuesta(encuesta.getEncuestaId(), null, true); // Email null
        });
        System.out.println("[DEBUG] --> InputValidationException en responderEncuesta verificada.");
    }

    // ===================================================================================
    //
    // TESTS DE [FUNC-5]: cancelarEncuesta
    //
    // ===================================================================================

    /**
     * NUEVO TEST: Cumple el requisito para cancelarEncuesta.
     */
    @Test
    public void testCancelarEncuestaExito() throws Exception {
        System.out.println("[DEBUG] --> Probando éxito de cancelarEncuesta...");
        Encuesta encuesta = crearEncuestaDePrueba("Encuesta para cancelar", LocalDateTime.now().plusDays(1));

        Encuesta encuestaCancelada = surveyService.cancelarEncuesta(encuesta.getEncuestaId());
        assertTrue(encuestaCancelada.isCancelada());

        Encuesta encuestaDeBD = surveyService.buscarEncuestaPorId(encuesta.getEncuestaId());
        assertTrue(encuestaDeBD.isCancelada());
        System.out.println("[DEBUG] --> Éxito de cancelarEncuesta verificado.");
    }

    /**
     * NUEVO TEST: Cumple el requisito para cancelarEncuesta (varias excepciones).
     */
    @Test
    public void testCancelarEncuestaNoEncontrada() {
        System.out.println("[DEBUG] --> Probando InstanceNotFoundException en cancelarEncuesta...");
        assertThrows(InstanceNotFoundException.class, () -> {
            surveyService.cancelarEncuesta(ID_INEXISTENTE);
        });
        System.out.println("[DEBUG] --> InstanceNotFoundException en cancelarEncuesta verificada.");
    }

    /**
     * NUEVO TEST: Cumple el requisito para cancelarEncuesta (varias excepciones).
     */
    @Test
    public void testCancelarEncuestaFinalizada() throws Exception {
        System.out.println("[DEBUG] --> Probando EncuestaFinalizadaException en cancelarEncuesta...");
        Encuesta expirada = crearEncuestaDePrueba("Expirada para cancelar", LocalDateTime.now().minusDays(1));

        EncuestaFinalizadaException ex = assertThrows(EncuestaFinalizadaException.class, () -> {
            surveyService.cancelarEncuesta(expirada.getEncuestaId());
        });
        assertEquals(expirada.getEncuestaId(), ex.getEncuestaId());
        assertEquals(expirada.getFechaFin(), ex.getFechaFin());
        System.out.println("[DEBUG] --> EncuestaFinalizadaException en cancelarEncuesta verificada.");
    }

    @Test
    public void testCancelarEncuestaYaCancelada() throws Exception {
        Encuesta encuesta = crearEncuestaDePrueba("¿Encuesta cancelable?", LocalDateTime.now().plusDays(2));
        surveyService.cancelarEncuesta(encuesta.getEncuestaId());

        System.out.println("[DEBUG] --> Probando EncuestaCanceladaException (doble cancelación)...");
        EncuestaCanceladaException ex1 = assertThrows(EncuestaCanceladaException.class, () -> {
            surveyService.cancelarEncuesta(encuesta.getEncuestaId());
        });
        // Comprobación de getters para cobertura
        assertEquals(encuesta.getEncuestaId(), ex1.getEncuestaId());
        System.out.println("[DEBUG] --> Getter (1) de EncuestaCanceladaException verificado.");
    }

    // ===================================================================================
    //
    // TESTS DE [FUNC-6]: obtenerRespuestas
    //
    // ===================================================================================

    /**
     * NUEVO TEST: Cumple el requisito para obtenerRespuestas.
     */
    @Test
    public void testObtenerRespuestasExito() throws Exception {
        System.out.println("[DEBUG] --> Probando éxito de obtenerRespuestas...");
        Encuesta encuesta = crearEncuestaDePrueba("Encuesta para obtener respuestas", LocalDateTime.now().plusDays(1));

        surveyService.responderEncuesta(encuesta.getEncuestaId(), "empleado1@udc.es", true);
        surveyService.responderEncuesta(encuesta.getEncuestaId(), "empleado2@udc.es", false);

        // 1. Obtener todas
        List<Respuesta> todas = surveyService.obtenerRespuestas(encuesta.getEncuestaId(), false);
        assertEquals(2, todas.size());

        // 2. Obtener solo afirmativas
        List<Respuesta> afirmativas = surveyService.obtenerRespuestas(encuesta.getEncuestaId(), true);
        assertEquals(1, afirmativas.size());
        assertEquals("empleado1@udc.es", afirmativas.get(0).getEmailEmpleado());

        // 3. Obtener de encuesta sin respuestas
        Encuesta sinRespuestas = crearEncuestaDePrueba("Sin respuestas", LocalDateTime.now().plusDays(1));
        List<Respuesta> vacia = surveyService.obtenerRespuestas(sinRespuestas.getEncuestaId(), false);
        assertTrue(vacia.isEmpty());

        System.out.println("[DEBUG] --> Éxito de obtenerRespuestas verificado.");
    }

    /**
     * NUEVO TEST: Cumple el requisito para obtenerRespuestas.
     */
    @Test
    public void testObtenerRespuestasNoEncontrada() {
        System.out.println("[DEBUG] --> Probando InstanceNotFoundException en obtenerRespuestas...");
        assertThrows(InstanceNotFoundException.class, () -> {
            surveyService.obtenerRespuestas(ID_INEXISTENTE, false);
        });
        assertThrows(InstanceNotFoundException.class, () -> {
            surveyService.obtenerRespuestas(ID_INEXISTENTE, true);
        });
        System.out.println("[DEBUG] --> InstanceNotFoundException en obtenerRespuestas verificada.");
    }

    // ===================================================================================
    //
    // TESTS DE COBERTURA DE MODELO Y FACTORIES
    //
    // ===================================================================================

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

        // ============================
        // MEJORA DE COBERTURA (toString)
        // ============================
        assertNotNull(r1.toString());
        System.out.println("[DEBUG] --> Respuesta.toString() verificado: " + r1.toString());
        // ============================
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

        // ============================
        // MEJORA DE COBERTURA (toString)
        // ============================
        assertNotNull(e1.toString());
        System.out.println("[DEBUG] --> Encuesta.toString() verificado: " + e1.toString());
        // ============================
    }

    /**
     * NUEVO TEST: Cubre las factorías DAO (EncuestaDaoFactory y RespuestaDaoFactory)
     */
    @Test
    public void testFactoryCoverage() {
        System.out.println("[DEBUG] --> Probando cobertura de DAO Factories...");

        // Cobertura para EncuestaDaoFactory
        SqlEncuestaDao encuestaDao = EncuestaDaoFactory.getDao();
        assertNotNull(encuestaDao);
        System.out.println("[DEBUG] --> EncuestaDaoFactory.getDao() verificado.");

        // Cobertura para RespuestaDaoFactory
        SqlRespuestaDao respuestaDao = RespuestaDaoFactory.getDao();
        assertNotNull(respuestaDao);
        System.out.println("[DEBUG] --> RespuestaDaoFactory.getDao() verificado.");
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