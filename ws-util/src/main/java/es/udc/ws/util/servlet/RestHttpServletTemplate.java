package es.udc.ws.util.servlet;

import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ExceptionToJsonConversor;
import es.udc.ws.util.json.exceptions.ParsingException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestHttpServletTemplate extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            processGet(req, resp);
        } catch (InstanceNotFoundException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    ExceptionToJsonConversor.toInstanceNotFoundException(ex), null);
        } catch (InputValidationException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ExceptionToJsonConversor.toInputValidationException(ex), null);
        } catch (ParsingException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ExceptionToJsonConversor.toInputValidationException(new InputValidationException(ex.getMessage())),
                    null);
        }
    }

    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InstanceNotFoundException, InputValidationException {
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_IMPLEMENTED,null,null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            processPost(req, resp);
        } catch (InstanceNotFoundException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    ExceptionToJsonConversor.toInstanceNotFoundException(ex), null);
        } catch (InputValidationException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ExceptionToJsonConversor.toInputValidationException(ex), null);
        } catch (ParsingException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ExceptionToJsonConversor.toInputValidationException(new InputValidationException(ex.getMessage())),
                    null);
        }
    }

    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InstanceNotFoundException, InputValidationException {
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_IMPLEMENTED,null,null);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processPut(req, resp);
        } catch (InstanceNotFoundException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    ExceptionToJsonConversor.toInstanceNotFoundException(ex), null);
        } catch (InputValidationException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ExceptionToJsonConversor.toInputValidationException(ex), null);
        } catch (ParsingException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ExceptionToJsonConversor.toInputValidationException(new InputValidationException(ex.getMessage())),
                    null);
        }
    }

    protected void processPut(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InstanceNotFoundException, InputValidationException {
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_IMPLEMENTED,null,null);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            processDelete(req, resp);
        } catch (InstanceNotFoundException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    ExceptionToJsonConversor.toInstanceNotFoundException(ex), null);
        } catch (InputValidationException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ExceptionToJsonConversor.toInputValidationException(ex), null);
        } catch (ParsingException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ExceptionToJsonConversor.toInputValidationException(new InputValidationException(ex.getMessage())),
                    null);
        }
    }

    protected void processDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InstanceNotFoundException, InputValidationException {
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_IMPLEMENTED,null,null);
    }

}