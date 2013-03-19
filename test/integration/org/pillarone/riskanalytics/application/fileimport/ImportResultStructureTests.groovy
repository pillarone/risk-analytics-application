package org.pillarone.riskanalytics.application.fileimport

import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.application.output.structure.item.ResultNode
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.core.output.DBCleanUpService

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ImportResultStructureTests extends GroovyTestCase {

    ResultStructureImportService service = new ResultStructureImportService()

    void testImport() {

        File paramFile = new File(getModelFolder(), "application/ApplicationDefaultResultTree.groovy")

        def count = ResultStructureDAO.count()

//        assertTrue "import not successful", service.importFile(paramFile.toURI().toURL())
        service.importFile(paramFile.toURI().toURL())
//        assertEquals count + 1, ResultStructureDAO.count()

        ResultStructure resultStructure = new ResultStructure(ResultStructureImportService.DEFAULT_NAME, ApplicationModel)
        resultStructure.load()

        ResultNode node = resultStructure.rootNode
        assertEquals 2, node.childCount

        ResultNode dC = node.getChildAt(0)
        assertEquals "dynamicComponent", dC.name
        assertEquals 2, dC.childCount

        ResultNode subComponents = dC.getChildAt(0)
        assertEquals "[%subcomponents%]", subComponents.name
        assertEquals 2, subComponents.childCount

        ResultNode outSecondValue = subComponents.getChildAt(0)
        assertEquals "outSecondValue", outSecondValue.name
        assertEquals 1, outSecondValue.childCount

        ResultNode xValue = outSecondValue.getChildAt(0)
        assertEquals "value", xValue.name
        assertEquals "Application:dynamicComponent:[%subcomponents%]:outSecondValue:value", xValue.resultPath
        assertEquals 0, xValue.childCount

        ResultNode outValue1 = dC.getChildAt(1)
        assertEquals "outValue1", outValue1.name
        assertEquals 1, outValue1.childCount

        ResultNode value = outValue1.getChildAt(0)
        assertEquals "value", value.name
        assertEquals "Application:dynamicComponent:outValue1:value", value.resultPath
        assertEquals 0, value.childCount


    }

    private File getModelFolder() {
        return new File(getClass().getResource("/models").toURI())
    }


}
