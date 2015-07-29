package org.ats.atrf.task;

import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;


public class TaskManager {
    private static ITaskManager defaultManager;
    
    public static interface ITaskManager {
        public SimpleTask offer(SimpleTask task, JSONObject scheduleObject);
        public SimpleTask offer(SimpleTask task, Date date);
        public SimpleTask drop(String id);
        public SimpleTask modify(String id, SimpleTask task);
        public SimpleTask query(String id);
        public List<SimpleTask> queryAll();
    }

    public static ITaskManager getManager() {
        return defaultManager;
    }
    
    public static ITaskManager forClass(Class<? extends ITaskManager> cls) {
        try {
            return defaultManager = cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
