package es.udc.ws.movies.thriftservice;

import es.udc.ws.movies.thrift.ThriftMovieService;
import es.udc.ws.util.servlet.ThriftHttpServletTemplate;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

public class ThriftMovieServiceServlet extends ThriftHttpServletTemplate {

    public ThriftMovieServiceServlet() {
        super(createProcessor(), createProtocolFactory());
    }

    private static TProcessor createProcessor() {

        return new ThriftMovieService.Processor<ThriftMovieService.Iface>(
            new ThriftMovieServiceImpl());

    }

    private static TProtocolFactory createProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }

}
