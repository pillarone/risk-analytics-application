package org.pillarone.riskanalytics.application.fileimport

import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.application.output.structure.StructureMapping
import org.pillarone.riskanalytics.application.output.structure.item.ResultNode
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure

class ResultStructureImportServiceTests extends GroovyTestCase {

    ResultStructureImportService service = new ResultStructureImportService()

    void testImport() {
        int initialMappings = StructureMapping.count()

        ResultStructureImportService.importDefaults()

        List allDaos = ResultStructureDAO.list()
        assertEquals allDaos.collect { it.modelClassName + " " + it.name}.toString(), 6, allDaos.size()
        assertTrue StructureMapping.count() > initialMappings

        ResultStructure resultStructure = new ResultStructure(ResultStructureImportService.DEFAULT_NAME, ApplicationModel)
        resultStructure.load()

        ResultNode node = resultStructure.rootNode
        assertEquals 2, node.childCount

        ResultNode dC = node.getChildByName("dynamicComponent")
        assertEquals 2, dC.childCount

        ResultNode subComponents = dC.getChildByName("[%subcomponents%]")
        assertNotNull subComponents
        assertEquals 2, subComponents.childCount

        ResultNode outSecondValue = subComponents.getChildByName("outSecondValue")
        assertNotNull outSecondValue
        assertEquals 1, outSecondValue.childCount

        ResultNode xValue = outSecondValue.getChildByName("value")
        assertNotNull xValue
        assertEquals "Application:dynamicComponent:[%subcomponents%]:outSecondValue:value", xValue.resultPath
        assertEquals 0, xValue.childCount

        ResultNode outValue1 = dC.getChildByName("outValue1")
        assertNotNull outValue1
        assertEquals 1, outValue1.childCount

        ResultNode value = outValue1.getChildByName("value")
        assertNotNull value
        assertEquals "Application:dynamicComponent:outValue1:value", value.resultPath
        assertEquals 0, value.childCount
    }



    private File getModelFolder() {
        return new File(getClass().getResource("/models").toURI())
    }
}
