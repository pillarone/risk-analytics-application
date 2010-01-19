package org.pillarone.riskanalytics.application.ui.simulation.view

import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCComboBoxOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import models.core.CoreModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.application.ui.simulation.model.SimulationConfigurationModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class SimulationConfigurationViewTests extends AbstractSimpleFunctionalTest {


    protected void doStart() {
        LocaleResources.setTestMode()
        ModellingItemFactory.clear()
        
        new ParameterizationImportService().compareFilesAndWriteToDB(["CoreParameters"])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(["CoreResultConfiguration"])
        new ModelStructureImportService().compareFilesAndWriteToDB(["CoreStructure"])

        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"
        Model model = new CoreModel()
        model.initComponents()
        Parameterization parameterization = new Parameterization('CoreParameters')
        parameterization.valid = true
        SimulationConfigurationModel presentationModel = new SimulationConfigurationModel(null, model.class, parameterization, null)
        frame.setContentPane(new SimulationConfigurationView(presentationModel).content)
        frame.visible = true
    }

    public void stop() {
        LocaleResources.clearTestMode()
    }

    public void testContent() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        assertNotNull frame
        ULCButtonOperator runButton = new ULCButtonOperator(frame, "run")
        assertNotNull runButton
        ULCButtonOperator stopButton = new ULCButtonOperator(frame, "stop")
        assertNotNull stopButton
        ULCButtonOperator openResults = new ULCButtonOperator(frame, "open")
        assertNotNull openResults
        ULCTextFieldOperator iterationCount = new ULCTextFieldOperator(frame, new ComponentByNameChooser("iterationCount"))
        assertNotNull iterationCount

        ULCTextFieldOperator simulationName = new ULCTextFieldOperator(frame, new ComponentByNameChooser("simulationName"))
        assertNotNull simulationName

        assertFalse "run button should be disabled", runButton.enabled
        assertFalse "stop button should be disabled", stopButton.enabled
        assertFalse "open results button should be disabled", openResults.enabled

        iterationCount.enterText("0")
        assertFalse "run button should be disabled (entered 0)", runButton.enabled
        assertFalse "stop button should be disabled", stopButton.enabled

        iterationCount.enterText("50")
        assertTrue "run button should be enabled by iterationCount", runButton.enabled
        assertFalse "stop button should be disabled by iterationCount", stopButton.enabled

        assertEquals "", simulationName.getText().trim()

        ULCComboBoxOperator template = new ULCComboBoxOperator(frame, new ComponentByNameChooser("templateNamesComboBox"))
        assertNotNull template

        template.selectItem('CoreResultConfiguration')

        ULCComboBoxOperator output = new ULCComboBoxOperator(frame, new ComponentByNameChooser("outputStrategyComboBox"))
        assertNotNull output

        output.selectItem("Database: Bulk Insert")

        /* try to fix build on hudson
        runButton.getFocus() // seems that the clickMouse has no focus on the button
        runButton.clickMouse()

        stopButton.getFocus()
        stopButton.clickMouse()


        ULCDialogOperator alert = new ULCDialogOperator('Warning')
        assertNotNull "display warning when batch upload not available", alert
        alert.close()

        assertTrue "run button should be enabled when the simulation could not be started", runButton.enabled

        template.selectItem('Summary Aggregate Claims (DB)')

        runButton.getFocus() // seems that the clickMouse has no focus on the button
        runButton.clickMouse()

        assertFalse "run button should be disabled after run", runButton.enabled
        assertTrue "stop button should be enabled after run", stopButton.enabled

        assertTrue simulationName.text.size() > 0

        stopButton.getFocus()
        stopButton.clickMouse()

        ULCProgressBarOperator progressBar = new ULCProgressBarOperator(frame, new ComponentByNameChooser(('progressBar')))
        progressBar.waitValue 'stopped'
        while (!runButton.enabled) {
            Thread.sleep 100
        }
        assertTrue "run button should be enabled after stop", runButton.enabled
        assertFalse "stop button should be disabled after stop", stopButton.enabled

        assertEquals "", simulationName.getText().trim()
        */
    }


}
