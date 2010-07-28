import grails.util.Environment

import org.pillarone.riskanalytics.application.jobs.JobScheduler
import org.pillarone.riskanalytics.application.fileimport.ResultStructureImportService
import org.codehaus.groovy.grails.commons.ApplicationHolder

class ApplicationBootStrap {

    def init = {servletContext ->

        if (Environment.current == Environment.TEST) {
            return
        }

        def modelFilter = ApplicationHolder.application.config?.models
        List models = null
        if (modelFilter) {
            models = modelFilter.collect {it - "Model"}
        }
        new ResultStructureImportService().compareFilesAndWriteToDB(models)
        ResultStructureImportService.importDefaults()
        // start a quartz job scheduler for a batch
        new JobScheduler().start()

    }

    def destroy = {
    }
}