import grails.util.Environment

import org.pillarone.riskanalytics.application.jobs.JobScheduler
import org.pillarone.riskanalytics.application.fileimport.ResultStructureImportService

class ApplicationBootStrap {

    def init = {servletContext ->

        if (Environment.current == Environment.TEST) {
            return
        }
        ResultStructureImportService.importDefaults()
        // start a quartz job scheduler for a batch
        new JobScheduler().start()

    }

    def destroy = {
    }
}