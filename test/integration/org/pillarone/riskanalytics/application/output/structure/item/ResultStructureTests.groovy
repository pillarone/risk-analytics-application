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

        ResultNode root = new ResultNode("root", null)
        ResultNode level1 = new ResultNode("a", "path:a")
        root.addChild(level1)
        level1.addChild(new ResultNode("x", "path:x"))
        level1.addChild(new ResultNode("y", "path:y"))
        resultStructure.rootNode = root

        resultStructure.save()

        assertEquals structureCount + 1, ResultStructureDAO.count()
        assertEquals mappingCount + 4, StructureMapping.count()

        ResultStructureDAO resultStructureDAO = ResultStructureDAO.findByName("TEST")
        assertNotNull resultStructureDAO

        assertEquals ApplicationModel.name, resultStructureDAO.modelClassName
        assertEquals 4, resultStructureDAO.structureMappings.size()

        StructureMapping rootMapping = resultStructureDAO.structureMappings.find { it.name == "root"}
        assertNull rootMapping.resultPath
        assertNull rootMapping.parent

        StructureMapping a = resultStructureDAO.structureMappings.find { it.name == "a"}
        assertEquals "path:a", a.resultPath
        assertSame rootMapping, a.parent

        StructureMapping x = resultStructureDAO.structureMappings.find { it.name == "x"}
        assertEquals "path:x", x.resultPath
        assertEquals 0, x.orderWithinLevel
        assertSame a, x.parent

        StructureMapping y = resultStructureDAO.structureMappings.find { it.name == "y"}
        assertEquals "path:y", y.resultPath
        assertEquals 1, y.orderWithinLevel
        assertSame a, y.parent
    }

    void testLoad() {
        ResultStructure resultStructure = new ResultStructure("TEST")
        resultStructure.modelClass = ApplicationModel

        ResultNode root = new ResultNode("root", null)
        ResultNode level1 = new ResultNode("a", "path:a")
        root.addChild(level1)
        level1.addChild(new ResultNode("x", "path:x"))
        level1.addChild(new ResultNode("y", "path:y"))
        resultStructure.rootNode = root
        resultStructure.save()

        ResultStructure resultStructure2 = new ResultStructure("TEST")
        resultStructure2.load()

        assertEquals ApplicationModel, resultStructure2.modelClass

        ResultNode rootNode = resultStructure2.rootNode
        assertEquals "root", rootNode.name
        assertEquals 1, rootNode.childNodes.size()

        ResultNode aNode = rootNode.childNodes[0]
        assertEquals "a", aNode.name
        assertEquals 2, aNode.childNodes.size()

        ResultNode xNode = aNode.childNodes[0]
        assertEquals "x", xNode.name
        assertEquals 0, xNode.childNodes.size()

        ResultNode yNode = aNode.childNodes[1]
        assertEquals "y", yNode.name
        assertEquals 0, yNode.childNodes.size()
    }

    //TODO: not necessary at the moment
    /*void testUpdate() {


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
    }*/
}
