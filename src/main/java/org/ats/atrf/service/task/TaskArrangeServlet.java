package org.ats.atrf.service.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ats.atrf.net.Poster;
import org.ats.atrf.resource.IConstants;
import org.ats.atrf.service.AbstractService;
import org.ats.atrf.service.ServiceServlet;
import org.ats.atrf.service.ServiceTask;
import org.ats.atrf.service.AbstractService.ServiceContent;
import org.ats.atrf.task.SimpleTask;
import org.ats.atrf.task.TaskManager;

public class TaskArrangeServlet extends ServiceServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doService(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuffer responseContent = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
        for (String temp = reader.readLine(); temp != null;) {
            responseContent.append(temp).append(IConstants.LINE_SEPARATOR);
            temp = reader.readLine();
        }

        JSONArray taskList = JSONArray.fromObject(responseContent.toString());
        // JSONArray taskList = JsonUtil.getJava(responseContent.toString(),
        // JSONArray.class);
        for (int i = 0; i < taskList.size(); i++) {
            JSONObject taskObject = taskList.getJSONObject(i);
            System.out.println(taskObject.toString(2));

            Date date = null;
            // Integer interval;
            // Integer reptimes;

            String testcaseid = taskObject.getString("testcaseid");
            String datasetid = taskObject.getString("datasetid");
            String sceneid = taskObject.getString("sceneid");
            String taskid = taskObject.getString("taskid");
            String schedule = taskObject.getString("schedule");

            System.out.println(taskid);
            System.out.println(sceneid);
            System.out.println(datasetid);
            System.out.println(testcaseid);

            try {
                date = IConstants.DATE_FORMAT_JSON.parse(schedule);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // interval = taskObject.getInt("interval");
            // frequency = taskObject.getInt("reptimes");
            AbstractService service = AbstractService.getService("autotest_process");

            SimpleTask serviceTask = new ServiceTask(service, taskid);
            serviceTask.id = taskid;
            // TODO taskId 是不能重复的! taskId + time?
            // serviceTask.datamap.put("taskid", taskid);
            // serviceTask.datamap.put("sceneid", sceneid);
            // serviceTask.datamap.put("datasetid", datasetid);
            // serviceTask.datamap.put("testcaseid", testcaseid);

            // 取数据
            Poster poster;
            poster = new Poster("http://localhost:9997/test/scene");
            poster.query("sceneid", sceneid);
            JSONObject scene = JSONObject.fromObject(poster.post().getContent());

            poster = new Poster("http://localhost:9997/test/dataset");
            poster.query("datasetid", datasetid);
            JSONObject dataset = JSONObject.fromObject(poster.post().getContent());

            poster = new Poster("http://localhost:9997/test/testcase");
            poster.query("testcaseid", testcaseid);
            JSONObject testcase = JSONObject.fromObject(poster.post().getContent());
            
            // 生成 task file
            // 生成 case file
            // 作为输入数据传进去~
            // 为processor提供启动命令
            serviceTask.datamap.put("commands", new String[] { "ruby", "worker/test.rb" });
            serviceTask.datamap.put("commands", new String[] { "java", "-jar", "worker/iodemo.jar" });
            // 为processor提供启动参数
            serviceTask.datamap.put("sources", new Object[] { "param1", "param2", "param3" });
            TaskManager.getManager().offer(serviceTask, date);
            
            ServiceContent content = new ServiceContent();
            content.put("path", "C:\\Users\\Administrator\\git\\AT_RF_Client\\source\\话单文件名称及各记录字段统计.xlsx");
            service = new BillingGenerateService();
            service.service(content);
        }
    }
}