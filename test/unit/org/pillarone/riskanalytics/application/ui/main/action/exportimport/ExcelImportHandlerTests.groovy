package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.example.parameter.ExampleEnum
import org.pillarone.riskanalytics.core.example.parameter.ExampleResourceConstraints
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.ParameterizationHelper
import org.pillarone.riskanalytics.core.simulation.item.parameter.EnumParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

class ExcelImportHandlerTests extends GroovyTestCase {
    File exportFile

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        LocaleResources.setTestMode()
        ConstraintsFactory.registerConstraint(new ExampleResourceConstraints())
        exportFile = File.createTempFile('excel', '.xlsx')
        exportFile.bytes = new ExcelExportHandler(new ApplicationModel()).exportModel()
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
        LocaleResources.clearTestMode()
    }

    void testValidation() {
        ExcelImportHandler handler = new ExcelImportHandler(exportFile)
        List<ImportResult> result = handler.validate()
        assert 0 == result.size()
    }

    void testParseExcel(){
        ExcelImportHandler handler = new ExcelImportHandler(new File('/home/detlef/temp/pmo-2449/exportresult.xlsx'))
        List result = handler.process()
        Model model = handler.modelInstance
        List<ParameterHolder> parameterizations = ParameterizationHelper.extractParameterHoldersFromModel(model, 1)
        ParameterHolder enumHolder = parameterizations.find{it.path == 'parameterComponent:parmEnumParameter'}
        assert enumHolder
        assert enumHolder instanceof EnumParameterHolder
        assert ExampleEnum.SECOND_VALUE == enumHolder.businessObject
    }
}
