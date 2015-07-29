package org.ats.atrf.service;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractService {
    public String name = null;

    public abstract void service(ServiceContent content);

    public static class ServiceContent extends HashMap<String, Object> {
        private static final long serialVersionUID = 1L;

        public ServiceContent() {
            super();
        }
        
        public ServiceContent(Map<? extends String, ? extends Object> map) {
            super(map);
        }
    }

    private static Map<String, AbstractService> serviceMap = new HashMap<String, AbstractService>();

    public static AbstractService registerService(String name, String classname) {
        AbstractService service = null;
        try {
            service = Class.forName(classname).asSubclass(AbstractService.class).newInstance();
            service.name = name;
            serviceMap.put(name, service);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return service;
    }

    public static AbstractService getService(String name) {
        return serviceMap.get(name);
    }

    @Override
    public String toString() {
        return "Service: [" + getClass().getSimpleName() + "] " + ((null == name) ? "Unknown" : name);
    }

}
