package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import models.application.ApplicationModel
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.example.parameter.ExampleResourceConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory

class ExcelExportHandlerTests {

    @Before
    void setUp() throws Exception {
        LocaleResources.setTestMode()
        ConstraintsFactory.registerConstraint(new ExampleResourceConstraints())
    }

    @After
    void tearDown() throws Exception {
        LocaleResources.clearTestMode()
    }

    @Test
    void testExportModel() {
        ExcelExportHandler handler = new ExcelExportHandler(new ApplicationModel())
        byte[] result = handler.exportModel()
        assert result != null
        XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result))
        assert 7 == workbook.numberOfSheets
        assert workbook.getSheet('globalParameterComponent')
        assert workbook.getSheet('parameterComponent')
        assert workbook.getSheet('hierarchyComponent')
        assert workbook.getSheet('dynamicComponent')
        assert workbook.getSheet('composedComponent')
        assert workbook.getSheet('MDP0-ExampleResourceConstraints')
        assert workbook.getSheet('Meta-Info')
    }
}
