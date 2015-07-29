package org.ats.atrf.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ats.atrf.demo.bean.TaskBean;
import org.ats.atrf.net.Poster;
import org.ats.atrf.resource.JsonUtil;
import org.ats.atrf.service.ServiceServlet;

public class TestTaskArrangeServlet extends ServiceServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doService(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ArrayList<TaskBean> taskList = new ArrayList<TaskBean>();
        TaskBean task;
        task = new TaskBean();
        task.setName("task_001_name");
        task.setTaskid("task_001_id");

        task.setSceneid("scene_001_id");
        task.setDatasetid("dataset_001_id");
        task.setTestcaseid("testcase_001_id");
        task.setSchedule(new Date());
        
        taskList.add(task);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        task = new TaskBean();
        task.setName("task_002_name");
        task.setTaskid("task_002_id");
        
        task.setInterval(2);
        task.setReptimes(2);

        task.setSceneid("scene_002_id");
        task.setDatasetid("dataset_002_id");
        task.setTestcaseid("testcase_002_id");
        task.setSchedule(new Date());

//        taskList.add(task);
        Poster poster = new Poster("http://localhost:9997/task/arrange");
        poster.post(JsonUtil.getJson(taskList, 2));
    }
    
}
