package org.pillarone.riskanalytics.application.output.structure.item

import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.application.output.structure.StructureMapping

class ResultStructure extends ModellingItem {

    VersionNumber versionNumber
    Map<String, String> mappings = new HashMap<String, String>()

    public ResultStructure(String name) {
        super(name);
        versionNumber = new VersionNumber("1")
    }

    public ResultStructure(String name, Class modelClass) {
        this(name);
        this.modelClass = modelClass
    }

    protected Object createDao() {
        return new ResultStructureDAO(name: name, modelClassName: modelClass.name)
    }

    Object getDaoClass() {
        ResultStructureDAO
    }

    protected void mapToDao(Object dao) {
        ResultStructureDAO resultStructureDAO = dao as ResultStructureDAO

        resultStructureDAO.name = name
        resultStructureDAO.modelClassName = modelClass.name
        resultStructureDAO.itemVersion = versionNumber.toString()

        for (Map.Entry<String, String> entry in mappings.entrySet()) {
            StructureMapping mapping = resultStructureDAO.structureMappings.find { it.resultPath == entry.value }
            if (mapping == null) {
                mapping = new StructureMapping(resultPath: entry.value)
                resultStructureDAO.addToStructureMappings(mapping)
            }
            mapping.artificialPath = entry.key
        }
    }

    protected void mapFromDao(Object dao, boolean completeLoad) {
        ResultStructureDAO resultStructureDAO = dao as ResultStructureDAO

        name = resultStructureDAO.name
        modelClass = getClass().getClassLoader().loadClass(resultStructureDAO.modelClassName)

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
            if (modelClass != null) {
                eq("modelClassName", modelClass.name)
            }
        }
    }


}
