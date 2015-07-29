package org.ats.atrf.service;

import org.ats.atrf.task.SimpleTask;

public class ServiceTask extends SimpleTask {

    public ServiceTask(AbstractService service) {
        this(service, null);
    }

    public ServiceTask(AbstractService service, String id) {
        this.datamap.put("service", service);
        this.name = service.name;
        this.id = (id == null ? this.name : id);
    }
}
