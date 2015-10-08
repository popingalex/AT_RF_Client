package org.ats.atrf.task.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.ats.atrf.task.SimpleTask;
import org.ats.atrf.task.TaskManager.ITaskManager;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.MutableTrigger;

public class QuartzTaskManager implements ITaskManager {
    private Scheduler scheduler;

    public QuartzTaskManager() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

//    @Override
    public SimpleTask offer(SimpleTask task, JSONObject scheduleObject) {
        Integer dotimes = scheduleObject.getInt("reptimes");
        Integer interval = scheduleObject.getInt("interval");

        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
        if (dotimes < 0) {
            scheduleBuilder.repeatForever();
        } else {
            scheduleBuilder.withRepeatCount(Math.max(dotimes, 0));
        }
        scheduleBuilder.withIntervalInMinutes(interval);

        MutableTrigger trigger = scheduleBuilder.build();
        trigger.setStartTime(new Date());
        return offer(task, trigger);
    }

//    @Override
    public SimpleTask offer(SimpleTask task, Date date) {
        MutableTrigger trigger = SimpleScheduleBuilder.simpleSchedule().build();
        trigger.setStartTime(new Date());
        return offer(task, trigger);
    }

    public SimpleTask offer(SimpleTask task, MutableTrigger trigger) {
        String id = task.id;
        String group = null;// TODO unset group attribute
        Class<? extends Job> jobClass = task.jobclass;

        JobBuilder jobBuilder = JobBuilder.newJob(jobClass);
        jobBuilder.withIdentity(JobKey.jobKey(id, group));
        jobBuilder.setJobData(new JobDataMap(task.datamap));
        JobDetail jobDetail = jobBuilder.build();
        trigger.setKey(TriggerKey.triggerKey(jobDetail.getKey().getName(), jobDetail.getKey().getGroup()));
        // TODO ���һ��Quartz������ִ��ͬ�����. �Ƿ���Է�������ͬ���Ͳ���
        try {
            // check exist
            if (!scheduler.checkExists(jobDetail.getKey())) {
                scheduler.scheduleJob(jobDetail, trigger);
            } else {
                System.out.println("TASK EXIST");
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return task;
    }

//    @Override
    public SimpleTask drop(String id) {
        // return scheduler.deleteJob(JobKey.jobKey(id));
        return null;
    }

//    @Override
    public SimpleTask modify(String id, SimpleTask task) {
        // TODO Auto-generated method stub
        return task;
    }

//    @Override
    public SimpleTask query(String id) {
        try {
            JobDetail detail = scheduler.getJobDetail(JobKey.jobKey(id));
            SimpleTask task = (SimpleTask) detail.getJobDataMap().get("Task");
            return task;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Override
    public List<SimpleTask> queryAll() {
        // TODO Auto-generated method stub
        return new ArrayList<SimpleTask>();
    }
}
