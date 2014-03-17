package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import com.ulcjava.base.application.ULCFrame
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.CalculationConfigurationModel
import models.deterministicApplication.DeterministicApplicationModel
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCTextFieldOperator


class CalculationConfigurationViewTests extends AbstractSimpleFunctionalTest {

    protected void doStart() {
        LocaleResources.testMode = true

        FileImportService.importModelsIfNeeded(["DeterministicApplication"])

        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"

        frame.contentPane = new CalculationConfigurationView(new CalculationConfigurationModel(DeterministicApplicationModel, null)).content
        frame.visible = true
    }

    void testEnableRun() {
        ULCFrameOperator frameOperator = new ULCFrameOperator("test")
        ULCButtonOperator run = new ULCButtonOperator(frameOperator, new ComponentByNameChooser("${SimulationActionsPane.simpleName}.run"))
        ULCTextFieldOperator iterations = new ULCTextFieldOperator(frameOperator, new ComponentByNameChooser("${CalculationSettingsPane.simpleName}.periodCount"))

        assertFalse run.enabled
        iterations.typeText("123")
        assertTrue run.enabled

        iterations.clearText()
        assertFalse run.enabled
    }

    void stop() {
        LocaleResources.testMode = false
    }
}
