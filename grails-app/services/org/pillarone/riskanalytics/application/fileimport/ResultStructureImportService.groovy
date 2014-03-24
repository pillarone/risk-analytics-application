package org.pillarone.riskanalytics.application.fileimport

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.pillarone.riskanalytics.application.output.structure.DefaultResultStructureBuilder
import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.util.ClassPathScanner
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.springframework.core.type.filter.AssignableTypeFilter

class ResultStructureImportService extends FileImportService {

    static final String DEFAULT_NAME = "Default"

    protected ConfigObject currentConfigObject
    GrailsApplication grailsApplication

    void importDefaults() {
        List<Class> allModels = findAllModelClasses()
        def modelFilter = grailsApplication.config?.models
        if (modelFilter.size() == 0) {
            modelFilter = allModels*.simpleName
        }
        log.info "All available model ${allModels*.simpleName}"
        for (String modelClassName in modelFilter) {
            Class modelClass = allModels.find { it.simpleName == modelClassName }
            if (modelClass == null) {
                throw new IllegalStateException("Model class ${modelClassName} does not exist. Check models property in Config.groovy")
            }
            if (ResultStructureDAO.countByModelClassNameAndNameLike(modelClass.name, DEFAULT_NAME) == 0) {
                log.info "No default result structure found for model ${modelClass.simpleName} - importing default"
                DefaultResultStructureBuilder.create(DEFAULT_NAME, modelClass).save()
            }
        }
    }

    private static List<Class> findAllModelClasses() {
        ClassPathScanner provider = new ClassPathScanner()
        provider.addIncludeFilter(new AssignableTypeFilter(Model))
        return provider.findCandidateComponents("models")*.beanClassName.collect {
            getClass().classLoader.loadClass(it)
        }
    }

    @Override
    Object getDaoClass() {
        ResultStructureDAO
    }

    @Override
    String getFileSuffix() {
        "ResultTree"
    }

    @Override
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

    @Override
    protected boolean saveItemObject(String fileContent) {
        Closure mappings = currentConfigObject.mappings

        ResultStructure resultStructure = new ResultStructure(currentConfigObject.displayName, currentConfigObject.model)
        resultStructure.rootNode = TreeBuildingClosureDelegate.createStructureTree(mappings)

        if (!resultStructure.save()) {
            log.error "Could not save result structure: ${resultStructure.dao.errors}"
            return false
        }

        return true
    }

    @Override
    protected boolean lookUpItem(String itemName) {
        String modelName = modelClassName
        boolean status = daoClass.findByNameAndModelClassName(itemName, modelName) != null
        return status
    }

    @Override
    String getModelClassName() {
        return currentConfigObject['model'].name
    }
}
