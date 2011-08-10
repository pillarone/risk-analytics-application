package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import models.core.CoreModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationConfigurationModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel

class SimulationConfigurationViewTests extends AbstractSimpleFunctionalTest {

    protected void doStart() {
        LocaleResources.setTestMode()

        FileImportService.importModelsIfNeeded(["Core"])

        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"

        frame.contentPane = new SimulationConfigurationView(new SimulationConfigurationModel(CoreModel, new RiskAnalyticsMainModel())).content
        frame.visible = true
    }

    void testEnableRun() {
        ULCFrameOperator frameOperator = new ULCFrameOperator("test")
        ULCButtonOperator run = new ULCButtonOperator(frameOperator, new ComponentByNameChooser("${SimulationActionsPane.getSimpleName()}.run"))
        ULCTextFieldOperator iterations = new ULCTextFieldOperator(frameOperator, new ComponentByNameChooser("iterations"))
        ULCTextFieldOperator name = new ULCTextFieldOperator(frameOperator, new ComponentByNameChooser("simulationName"))

        assertFalse run.enabled

        iterations.typeText("123")
        assertTrue run.enabled

        iterations.clearText()
        assertFalse run.enabled
    }

    void stop() {
        LocaleResources.clearTestMode()
    }


}
