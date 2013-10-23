package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import models.application.ApplicationModel
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.components.ExampleMultiMarkerConstraint
import org.pillarone.riskanalytics.core.example.parameter.ExampleResourceConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory

class ExcelExportHandlerTests extends GroovyTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        LocaleResources.setTestMode()
        ConstraintsFactory.registerConstraint(new ExampleResourceConstraints())
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
        LocaleResources.clearTestMode()
    }

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
