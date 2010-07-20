package org.pillarone.riskanalytics.application.fileimport

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.pillarone.riskanalytics.application.output.structure.DefaultResultStructureBuilder
import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.core.model.Model
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AssignableTypeFilter
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class ResultStructureImportService {

    private static Log LOG = LogFactory.getLog(ResultStructureImportService)

    public static void importDefaults() {
        List<Class> allModels = findAllModelClasses()
        def modelFilter = ApplicationHolder.application.config?.models
        if (modelFilter.size() == 0) {
            modelFilter = allModels*.simpleName
        }
        LOG.info "All available model ${allModels*.simpleName}"
        for (String modelClassName in modelFilter) {
            Class modelClass = allModels.find { it.name.endsWith(modelClassName)}
            if (ResultStructureDAO.countByModelClassName(modelClass.name) == 0) {
                LOG.info "No result structure found for model ${modelClass.simpleName} - importing default"
                DefaultResultStructureBuilder.create("Default", modelClass).save()
            }
        }
    }

    private static List<Class> findAllModelClasses() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true)
        provider.addIncludeFilter(new AssignableTypeFilter(Model))

        return provider.findCandidateComponents("models")*.beanClassName.collect { getClass().getClassLoader().loadClass(it) }
    }
}
