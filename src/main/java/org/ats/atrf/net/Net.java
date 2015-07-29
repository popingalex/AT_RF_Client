package org.ats.atrf.net;

import java.util.Map;
import java.util.Map.Entry;

import org.ats.atrf.service.ServiceServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Net {
    private Server                server;
    private ServletContextHandler servletHandler;
    public Net(Integer port) {
        server = new Server(port);
        servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    }

    public Net load(Map<String, ServiceServlet> servletMap) {
        for (Entry<String, ServiceServlet> entry : servletMap.entrySet()) {
            servletHandler.addServlet(new ServletHolder(entry.getValue()), entry.getKey());
        }
        server.setHandler(servletHandler);
        return this;
    }

    public void power() {
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
