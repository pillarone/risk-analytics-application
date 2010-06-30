package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import com.ulcjava.base.application.ULCFrame
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.application.util.LocaleResources
import models.core.CoreModel
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCTextFieldOperator


class SimulationConfigurationViewTests extends AbstractSimpleFunctionalTest {

    protected void doStart() {
        LocaleResources.setTestMode()

        FileImportService.importModelsIfNeeded(["Core"])

        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"

        frame.contentPane = new SimulationConfigurationView(CoreModel).content
        frame.visible = true
    }

    void testEnableRun() {
        ULCFrameOperator frameOperator = new ULCFrameOperator("test")
        ULCButtonOperator run = new ULCButtonOperator(frameOperator, new ComponentByNameChooser("run"))
        ULCTextFieldOperator iterations = new ULCTextFieldOperator(frameOperator, new ComponentByNameChooser("iterations"))

        assertFalse run.enabled

        iterations.typeText("123")

        assertTrue run.enabled

        iterations.clearText()

        assertFalse run.enabled
    }

    protected void stop() {
        LocaleResources.clearTestMode()
    }


}
