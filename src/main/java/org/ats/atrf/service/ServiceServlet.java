package org.ats.atrf.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ServiceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public String             path;
    public String             name;

    @Override
    public String toString() {
        return "Servlet: [" + name + "] " + path;
    }

    // private ServiceUtil serviceUtil;

    // public final void setEventUtil(ServiceUtil eventUtil) {
    // this.serviceUtil = eventUtil;
    // }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doService(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doService(req, resp);
    }

    abstract protected void doService(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException;

    protected final void handle(Enum<?> action, Object... sources) {
        // serviceUtil.handleEvent(action, sources);
    }

    protected final void responseContent(String content, HttpServletResponse response) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
