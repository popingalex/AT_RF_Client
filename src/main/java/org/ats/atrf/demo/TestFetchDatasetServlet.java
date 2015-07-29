package org.ats.atrf.demo;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ats.atrf.demo.bean.DatasetBean;
import org.ats.atrf.resource.JsonUtil;
import org.ats.atrf.service.ServiceServlet;

public class TestFetchDatasetServlet extends ServiceServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doService(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String datasetid = req.getParameter("datasetid");
        HashMap<String, String> datamap = new HashMap<String, String>();
        datamap.put("data", "value");
        
        DatasetBean bean = new DatasetBean();
        bean.setId(datasetid);
        bean.setName("dataset name "+datasetid);
        bean.setData(datamap);
        
        responseContent(JsonUtil.getJson(bean, 2), resp);
    }
}
