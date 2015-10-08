package org.ats.atrf;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ats.atrf.net.Net;
import org.ats.atrf.resource.ConfigUtil;
import org.ats.atrf.resource.IConstants;
import org.ats.atrf.service.AbstractService;
import org.ats.atrf.service.ServiceServlet;
import org.ats.atrf.service.ServiceTask;
import org.ats.atrf.task.SimpleTask;
import org.ats.atrf.task.TaskManager;
import org.ats.atrf.task.TaskManager.ITaskManager;
import org.ats.atrf.task.impl.QuartzTaskManager;

/**
 * <div class="en">
 * 
 * Entrance of Autotest Runningframework Client <br>
 * 1.1 Load servlet configuration from {@value #XML_SERVLET} <br>
 * 1.2 Init Net Adapter <br>
 * 2.1 Load service configuration from {@value #XML_SERVICE} <br>
 * 3.1 Load schedule configuration from {@value #XML_SCHEDULE} <br>
 * 3.2 Init Task Manager<br>
 * </div>
 * 
 * <div class="cn">
 * 
 * 自动化测试 运行框架 客户端 的入口 <br>
 * 1.1 从{@value #XML_SERVLET}中加载servlet的配置 <br>
 * 1.2 初始化网络适配器 <br>
 * 2.1 从{@value #XML_SERVICE}中加载servlet的配置 <br>
 * 3.1 从{@value #XML_SCHEDULE}中加载servlet的配置 <br>
 * 3.2 初始化任务管理器<br>
 * </div>
 * 
 * @see Net
 * 
 * @version 1.0 15 JUL 2015
 * @author Alex Xu
 */
public class Client {
    private static final String XML_SERVLET  = "servlet.xml";
    private static final String XML_SERVICE  = "service.xml";
    private static final String XML_SCHEDULE = "schedule.xml";
    public Client() {
        // 加载servlet
        Map<String, ServiceServlet> servletMap = new HashMap<String, ServiceServlet>();
        {
            JSONArray servletArray = JSONArray.fromObject(ConfigUtil.loadXML(XML_SERVLET));
            try {
                for (int i = 0; i < servletArray.size(); i++) {
                    JSONObject servletObject = servletArray.getJSONObject(i);
                    Class<?> cls = Class.forName(servletObject.getString("impl"));
                    ServiceServlet servlet = ServiceServlet.class.cast(cls.newInstance());
                    servlet.name = cls.getSimpleName();
                    servletMap.put((servlet.path = servletObject.getString("path")), servlet);
                    System.out.println(servlet.toString());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        new Net(IConstants.PROPERTIES.getInt("net.port")).load(servletMap).power();

        // 加载service
        {
            JSONArray serviceArray = JSONArray.fromObject(ConfigUtil.loadXML(XML_SERVICE));
            for (int i = 0; i < serviceArray.size(); i++) {
                JSONObject serviceObject = serviceArray.getJSONObject(i);
                // serviceObject.
                String name = serviceObject.getString("name");
                String classname = serviceObject.getString("impl");
                AbstractService service = AbstractService.registerService(name, classname);
                System.out.println(service);
            }
        }
        // 加载task
        ITaskManager manager = TaskManager.forClass(QuartzTaskManager.class);
        {
            JSONObject scheduleMap = JSONObject.fromObject(ConfigUtil.loadXML(XML_SCHEDULE));
            JSONArray scheduleServiceArray = scheduleMap.getJSONArray("services");
            for (int i = 0; i < scheduleServiceArray.size(); i++) {
                JSONObject scheduleObject = scheduleServiceArray.getJSONObject(i);
                AbstractService service = AbstractService.getService(scheduleObject.getString("name"));
                SimpleTask serviceTask = manager.offer(new ServiceTask(service), scheduleObject);
                System.out.println(serviceTask);
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
