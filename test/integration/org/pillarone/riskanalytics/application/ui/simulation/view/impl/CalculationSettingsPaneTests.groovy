package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCFrame
import models.deterministicApplication.DeterministicApplicationModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.CalculationSettingsPaneModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.testframework.operator.*

class CalculationSettingsPaneTests extends AbstractSimpleFunctionalTest {

    CalculationSettingsPane pane

    protected void doStart() {
        LocaleResources.setTestMode()
        ModellingItemFactory.clear()

        FileImportService.importModelsIfNeeded(["DeterministicApplication"])
        Parameterization parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('DeterministicApplicationParameters'))
        parameterization.load()
        Parameterization extraParameterization = ModellingItemFactory.incrementVersion(parameterization)
        extraParameterization.save()


        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"

        pane = new CalculationSettingsPane(new CalculationSettingsPaneModel(DeterministicApplicationModel))
        frame.setContentPane(pane.content)
        frame.visible = true
    }


    public void stop() {
        LocaleResources.clearTestMode()
    }

    public void testPeriodCount() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        assertNotNull frame

        ULCTextFieldOperator periodCount = new ULCTextFieldOperator(frame, new ComponentByNameChooser("CalculationSettingsPane.periodCount"))
        assertNotNull periodCount

        periodCount.enterText("5")
        CalculationSettingsPaneModel model = pane.model
        assertEquals 5, model.periodCount

    }


    void testGetSimulation() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        assertNotNull frame

        ULCTextFieldOperator name = new ULCTextFieldOperator(frame, new ComponentByNameChooser("simulationName"))
        name.enterText("Simulation")

        ULCTextAreaOperator comment = new ULCTextAreaOperator(frame, new ComponentByNameChooser("comment"))
        comment.typeText("comment")

        ULCComboBoxOperator param = new ULCComboBoxOperator(frame, new ComponentByNameChooser("parameterizationNames"))
        param.selectItem "DeterministicApplicationParameters"

        ULCTextFieldOperator periodCount = new ULCTextFieldOperator(frame, new ComponentByNameChooser("CalculationSettingsPane.periodCount"))
        periodCount.enterText("5")

        Simulation simulation = pane.model.getSimulation()
        assertEquals "Simulation", simulation.name
        assertEquals "comment", simulation.comment
        assertEquals 1, simulation.numberOfIterations
        assertEquals "DeterministicApplicationParameters", simulation.parameterization.name
        assertEquals 5, simulation.periodCount

    }
}
