package org.pillarone.riskanalytics.application.fileimport

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.pillarone.riskanalytics.application.output.structure.DefaultResultStructureBuilder
import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.util.ClassPathScanner
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.springframework.core.type.filter.AssignableTypeFilter

class ResultStructureImportService extends FileImportService {

    private static Log LOG = LogFactory.getLog(ResultStructureImportService)

    public static final String DEFAULT_NAME = "Default"

    protected ConfigObject currentConfigObject

    public static void importDefaults() {
        List<Class> allModels = findAllModelClasses()
        def modelFilter = ApplicationHolder.application.config?.models
        if (modelFilter.size() == 0) {
            modelFilter = allModels*.simpleName
        }
        LOG.info "All available model ${allModels*.simpleName}"
        for (String modelClassName in modelFilter) {
            Class modelClass = allModels.find { it.simpleName == modelClassName}
            if(modelClass == null) {
                throw new IllegalStateException("Model class ${modelClassName} does not exist. Check models property in Config.groovy")
            }
            if (ResultStructureDAO.countByModelClassNameAndNameLike(modelClass.name, DEFAULT_NAME) == 0) {
                LOG.info "No default result structure found for model ${modelClass.simpleName} - importing default"
                DefaultResultStructureBuilder.create(DEFAULT_NAME, modelClass).save()
            }
        }
    }

    private static List<Class> findAllModelClasses() {
        ClassPathScanner provider = new ClassPathScanner()
        provider.addIncludeFilter(new AssignableTypeFilter(Model))

        return provider.findCandidateComponents("models")*.beanClassName.collect { getClass().getClassLoader().loadClass(it) }
    }

    Object getDaoClass() {
        ResultStructureDAO
    }

    String getFileSuffix() {
        "ResultTree"
    }

    String prepare(URL file, String itemName) {
        GroovyUtils.parseGroovyScript readFromURL(file), { ConfigObject config ->
            currentConfigObject = config
        }
        String name = itemName - ".groovy"
        if (currentConfigObject.containsKey('displayName')) {
            name = currentConfigObject.displayName
        } else {
            currentConfigObject.displayName = name
        }
        return name
    }

    protected boolean saveItemObject(String fileContent) {
        Closure mappings = currentConfigObject.mappings

        ResultStructure resultStructure = new ResultStructure(currentConfigObject.displayName, currentConfigObject.model)
        resultStructure.rootNode = TreeBuildingClosureDelegate.createStructureTree(mappings)

        if (!resultStructure.save()) {
            LOG.error "Could not save result structure: ${resultStructure.dao.errors}"
            return false
        }

        return true
    }

    protected boolean lookUpItem(String itemName) {
        String modelName = getModelClassName()
        boolean status = getDaoClass().findByNameAndModelClassName(itemName, modelName) != null
        return status
    }

    String getModelClassName() {
        return currentConfigObject.get("model").name
    }


}
