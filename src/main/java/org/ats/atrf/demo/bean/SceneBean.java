package org.ats.atrf.demo.bean;

import java.util.Map;

public class SceneBean {
    private String              id;
    private String              name;
    private Map<String, String> data;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Map<String, String> getData() {
        return data;
    }
    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
