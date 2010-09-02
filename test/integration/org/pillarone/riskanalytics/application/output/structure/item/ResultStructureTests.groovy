package org.pillarone.riskanalytics.application.output.structure.item

import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.application.output.structure.StructureMapping
import models.application.ApplicationModel

class ResultStructureTests extends GroovyTestCase {

    void testInsertNew() {
        int structureCount = ResultStructureDAO.count()
        int mappingCount = StructureMapping.count()

        ResultStructure resultStructure = new ResultStructure("TEST")
        resultStructure.modelClass = ApplicationModel

        resultStructure.mappings.put("a", "1")
        resultStructure.mappings.put("b", "2")

        resultStructure.save()

        assertEquals structureCount + 1, ResultStructureDAO.count()
        assertEquals mappingCount + 2, StructureMapping.count()

        ResultStructureDAO resultStructureDAO = ResultStructureDAO.findByName("TEST")
        assertNotNull resultStructureDAO

        assertEquals ApplicationModel.name, resultStructureDAO.modelClassName
        assertEquals 2, resultStructureDAO.structureMappings.size()

        assertEquals "a", resultStructureDAO.structureMappings.find { it.resultPath == "1"}.artificialPath
        assertEquals "b", resultStructureDAO.structureMappings.find { it.resultPath == "2"}.artificialPath
    }

    void testLoad() {
        ResultStructure resultStructure = new ResultStructure("TEST")
        resultStructure.modelClass = ApplicationModel

        resultStructure.mappings.put("1", "a")
        resultStructure.mappings.put("2", "b")

        resultStructure.save()

        ResultStructure resultStructure2 = new ResultStructure("TEST")
        resultStructure2.load()

        assertEquals ApplicationModel, resultStructure2.modelClass
        assertEquals 2, resultStructure2.mappings.size()
        assertEquals "a", resultStructure2.mappings.get("1")
        assertEquals "b", resultStructure2.mappings.get("2")
    }

    void testUpdate() {


        ResultStructure resultStructure = new ResultStructure("TEST")
        resultStructure.modelClass = ApplicationModel

        resultStructure.mappings.put("1", "a")
        resultStructure.mappings.put("2", "b")

        resultStructure.save()

        int structureCount = ResultStructureDAO.count()
        int mappingCount = StructureMapping.count()

        ResultStructure resultStructure2 = new ResultStructure("TEST")
        resultStructure2.load()

        resultStructure2.mappings.put("1", "x")
        resultStructure2.mappings.put("2", "y")
        resultStructure2.save()

        assertEquals structureCount, ResultStructureDAO.count()
        assertEquals mappingCount, StructureMapping.count()

        ResultStructureDAO resultStructureDAO = ResultStructureDAO.findByName("TEST")
        assertNotNull resultStructureDAO

        assertEquals 2, resultStructureDAO.structureMappings.size()

        assertEquals "1", resultStructureDAO.structureMappings.find { it.resultPath == "x"}.artificialPath
        assertEquals "2", resultStructureDAO.structureMappings.find { it.resultPath == "y"}.artificialPath
    }
}
