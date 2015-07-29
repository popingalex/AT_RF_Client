package org.ats.atrf.demo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ats.atrf.demo.bean.TestcaseBean;
import org.ats.atrf.resource.JsonUtil;
import org.ats.atrf.service.ServiceServlet;

public class TestFetchTestcaseServlet extends ServiceServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doService(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String testcaseid = req.getParameter("testcaseid");

        TestcaseBean bean = new TestcaseBean();
        bean.setId(testcaseid);
        bean.setName("testcase name " + testcaseid);

        responseContent(JsonUtil.getJson(bean, 2), resp);
    }
}
