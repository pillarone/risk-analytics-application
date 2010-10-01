package org.pillarone.riskanalytics.application.output.structure.item

import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.application.output.structure.StructureMapping
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

class ResultStructure extends ModellingItem {

    VersionNumber versionNumber
    Map<String, String> mappings = new HashMap<String, String>()
    String language

    public ResultStructure(String name) {
        super(name);
        versionNumber = new VersionNumber("1")
        language = LocaleResources.getLanguage()
    }

    public ResultStructure(String name, Class modelClass) {
        this(name);
        this.modelClass = modelClass
        this.language = LocaleResources.getLanguage()
    }

    public ResultStructure(String name, Class modelClass, String language) {
        this(name);
        this.modelClass = modelClass
        this.language = language
    }

    protected Object createDao() {
        return new ResultStructureDAO(name: name, modelClassName: modelClass.name, language: language)
    }

    Object getDaoClass() {
        ResultStructureDAO
    }

    protected void mapToDao(Object dao) {
        ResultStructureDAO resultStructureDAO = dao as ResultStructureDAO

        resultStructureDAO.name = name
        resultStructureDAO.modelClassName = modelClass.name
        resultStructureDAO.itemVersion = versionNumber.toString()
        resultStructureDAO.language = language

        for (Map.Entry<String, String> entry in mappings.entrySet()) {
            StructureMapping mapping = resultStructureDAO.structureMappings.find { it.artificialPath == entry.key }
            if (mapping == null) {
                mapping = new StructureMapping(artificialPath: entry.key)
                resultStructureDAO.addToStructureMappings(mapping)
            }
            mapping.resultPath = entry.value
        }
    }

    protected void mapFromDao(Object dao, boolean completeLoad) {
        ResultStructureDAO resultStructureDAO = dao as ResultStructureDAO

        name = resultStructureDAO.name
        modelClass = getClass().getClassLoader().loadClass(resultStructureDAO.modelClassName)
        language = resultStructureDAO.language

        mappings.clear()
        for (StructureMapping structureMapping in resultStructureDAO.structureMappings) {
            mappings.put(structureMapping.artificialPath, structureMapping.resultPath)
        }
    }

    protected Object loadFromDB() {
        def criteria = ResultStructureDAO.createCriteria()
        return criteria.get {
            eq("name", name)
            eq("itemVersion", versionNumber.toString())
            eq("language", language)
            if (modelClass != null) {
                eq("modelClassName", modelClass.name)
            }
        }
    }


}
