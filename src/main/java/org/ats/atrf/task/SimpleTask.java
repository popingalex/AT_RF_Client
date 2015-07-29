package org.ats.atrf.task;

import java.util.HashMap;
import java.util.Map;

import org.ats.atrf.service.ServiceJob;
import org.quartz.Job;

public class SimpleTask {
    public String               id;
    public String               name;

    public Map<String, Object>  datamap  = new HashMap<String, Object>();
    public Class<? extends Job> jobclass = ServiceJob.class;
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + ": [" + id + "] " + ((null == name) ? "Unknown" : name);
    }
}
