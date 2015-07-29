package org.ats.atrf.demo.bean;

import java.util.Date;

public class TaskBean {
    private String  name;
    private String  taskid;

    private String  sceneid;
    private String  datasetid;
    private String  testcaseid;

    private Date    schedule;
    private Integer interval = 0;
    private Integer reptimes = 1;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTaskid() {
        return taskid;
    }
    public void setTaskid(String id) {
        this.taskid = id;
    }
    public String getSceneid() {
        return sceneid;
    }
    public void setSceneid(String sceneid) {
        this.sceneid = sceneid;
    }
    public String getDatasetid() {
        return datasetid;
    }
    public void setDatasetid(String datasetid) {
        this.datasetid = datasetid;
    }
    public String getTestcaseid() {
        return testcaseid;
    }
    public void setTestcaseid(String testcaseid) {
        this.testcaseid = testcaseid;
    }
    public Date getSchedule() {
        return schedule;
    }
    public void setSchedule(Date schedule) {
        this.schedule = schedule;
    }
    public Integer getInterval() {
        return interval;
    }
    public void setInterval(Integer interval) {
        this.interval = interval;
    }
    public Integer getReptimes() {
        return reptimes;
    }
    public void setReptimes(Integer frequency) {
        this.reptimes = frequency;
    }
}
