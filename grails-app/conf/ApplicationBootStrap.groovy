import grails.util.Environment
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.pillarone.riskanalytics.application.fileimport.ResultStructureImportService
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.EnumTagType
import org.pillarone.riskanalytics.application.output.structure.StructureMapping
import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO

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

        // PMO-1752: Clear the views (structure_mapping, result_structuredao) on startup
        if (!Boolean.getBoolean("skipResultStructureImport")) {
            StructureMapping.withTransaction {
                StructureMapping.list()*.delete()
                ResultStructureDAO.list()*.delete()
            }

            new ResultStructureImportService().compareFilesAndWriteToDB(models)
            ResultStructureImportService.importDefaults()
        }
        Tag.withTransaction { status ->
            if (!Tag.findByName(NewCommentView.POST_LOCKING)) {
                new Tag(name: NewCommentView.POST_LOCKING, tagType: EnumTagType.COMMENT).save()
            }
            if (!Tag.findByName(NewCommentView.SHARED_COMMENTS)) {
                new Tag(name: NewCommentView.SHARED_COMMENTS, tagType: EnumTagType.COMMENT).save()
            }
            if (!Tag.findByName(NewCommentView.VERSION_COMMENT)) {
                new Tag(name: NewCommentView.VERSION_COMMENT, tagType: EnumTagType.COMMENT).save()
            }
            if (!Tag.findByName(NewCommentView.REPORT)) {
                new Tag(name: NewCommentView.REPORT, tagType: EnumTagType.COMMENT).save()
            }

        }

        // start a quartz job scheduler for a batch
        quartzScheduler.start()

    }

    def destroy = {
    }
}
