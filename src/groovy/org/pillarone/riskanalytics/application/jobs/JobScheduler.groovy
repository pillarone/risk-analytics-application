package org.pillarone.riskanalytics.application.jobs

import org.quartz.SchedulerFactory
import org.quartz.Scheduler
import org.quartz.JobDetail
import org.quartz.CronTrigger
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.apache.log4j.Logger
import org.pillarone.riskanalytics.core.output.batch.BatchRunner

/**
 * @author fouad jaada
 */

public class JobScheduler {
    String name= "Batch for SimulationRun"
    String group= "SimulationRun"

    static final Logger LOG = Logger.getLogger(JobScheduler)

    public void start() {
        LOG.info  " starting a quartz job for a batch"
        SchedulerFactory schedulerFactory = new org.quartz.impl.StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        JobDetail jobDetail = new JobDetail(name,group, BatchRunner.class);
        jobDetail.getJobDataMap().put("type", "BatchRunner");
        CronTrigger trigger = new CronTrigger(name,group);
        trigger.setCronExpression(getLocalCronExpression());
        scheduler.scheduleJob(jobDetail, trigger);
        LOG.info "Next job will be executed at  ${trigger.getNextFireTime()}"
    }

    private String getLocalCronExpression(){
		String cron =  ApplicationHolder.application.config.batchCron
		LOG.info "Batch cron : $cron "
		return cron;
	}

}