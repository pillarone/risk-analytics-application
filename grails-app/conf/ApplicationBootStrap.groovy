import grails.util.Environment
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.pillarone.riskanalytics.application.fileimport.ResultStructureImportService
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.EnumTagType

class ApplicationBootStrap {

    def quartzScheduler

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
        Tag.withTransaction { status ->
            if (!Tag.findByName(NewCommentView.POST_LOCKING)) {
                new Tag(name: NewCommentView.POST_LOCKING, tagType: EnumTagType.COMMENT).save()
            }
        }

        // start a quartz job scheduler for a batch
        quartzScheduler.start()

    }

    def destroy = {
    }
}
