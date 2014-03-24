package org.pillarone.riskanalytics.application.fileimport

import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.TestFor
import models.application.ApplicationModel
import org.junit.Test
import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.application.output.structure.StructureMapping
import org.pillarone.riskanalytics.application.output.structure.item.ResultNode
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.core.user.Person

import static org.junit.Assert.assertEquals

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@TestFor(ResultStructureImportService)
class ImportResultStructureTests {

    @Test
    void testImport() {
        defineBeans {
            springSecurityService(TestSpringSecurityService)
        }
        mockDomain(StructureMapping)
        mockDomain(ResultStructureDAO)

        File paramFile = new File(modelFolder, "application/ApplicationDefaultResultTree.groovy")

        service.importFile(paramFile.toURI().toURL())

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

    static class TestSpringSecurityService extends SpringSecurityService {
        @Override
        Object getCurrentUser() {
            new Person()
        }
    }

}
