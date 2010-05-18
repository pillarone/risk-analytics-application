import grails.util.Environment

import org.pillarone.riskanalytics.application.jobs.JobScheduler

class ApplicationBootStrap {

    def init = {servletContext ->

        if (Environment.current == Environment.TEST) {
            return
        }

        // start a quartz job scheduler for a batch
        new JobScheduler().start()

    }

    def destroy = {
    }
}