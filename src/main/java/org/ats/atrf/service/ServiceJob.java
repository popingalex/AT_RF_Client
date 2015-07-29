package org.ats.atrf.service;

import org.ats.atrf.service.AbstractService.ServiceContent;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ServiceJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        AbstractService service = (AbstractService) context.getMergedJobDataMap().get("service");
        service.service(new ServiceContent(context.getMergedJobDataMap()));
    }

}
