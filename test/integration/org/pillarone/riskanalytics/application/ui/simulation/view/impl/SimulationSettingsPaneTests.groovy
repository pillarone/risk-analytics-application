package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.*
import models.core.CoreModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ModelDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class SimulationSettingsPaneTests extends AbstractSimpleFunctionalTest {

    SimulationSettingsPane pane

    protected void doStart() {
        LocaleResources.testMode = true
        ModellingItemFactory.clear()

        FileImportService.importModelsIfNeeded(["Core"])
        Parameterization parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('CoreParameters'))
        parameterization.load()
        Parameterization extraParameterization = ModellingItemFactory.incrementVersion(parameterization)
        extraParameterization.save()


        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"

        pane = new SimulationSettingsPane(new SimulationSettingsPaneModel(CoreModel))
        frame.contentPane = pane.content
        frame.visible = true

        //ART-588
        ModelDAO modelDAO = new ModelDAO(name: CoreModel.simpleName, modelClassName: CoreModel.name, itemVersion: "0.5")
        modelDAO.srcCode = ""
        modelDAO.save(flush: true)
        assertNotNull(modelDAO.id)
    }


    public void stop() {
        LocaleResources.testMode = false
    }

    public void testRandomSeed() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        assertNotNull frame

        ULCCheckBoxOperator userDefinedSeed = new ULCCheckBoxOperator(frame, new ComponentByNameChooser("userDefinedRandomSeed"))
        assertNotNull userDefinedSeed

        ULCTextFieldOperator randomSeed = new ULCTextFieldOperator(frame, new ComponentByNameChooser("randomSeed"))
        assertNotNull randomSeed
        assertFalse randomSeed.enabled

        userDefinedSeed.clickMouse()
        assertTrue randomSeed.enabled

        randomSeed.enterText("1234")
        assertEquals 1234, pane.model.randomSeed

        userDefinedSeed.clickMouse()
        assertFalse randomSeed.enabled
        assertNull pane.model.randomSeed

        userDefinedSeed.clickMouse()
        assertEquals 1234, pane.model.randomSeed

    }

    void testVersions() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        assertNotNull frame

        ULCComboBoxOperator names = new ULCComboBoxOperator(frame, new ComponentByNameChooser("parameterizationNames"))
        ULCComboBoxOperator versions = new ULCComboBoxOperator(frame, new ComponentByNameChooser("parameterizationVersions"))
        assertNotNull names
        assertNotNull versions

        names.selectItem("CoreAlternativeParameters")
        assertEquals 1, versions.itemCount

        names.selectItem("CoreParameters")
        //todo it doesn't work on the cruise
        //        assertEquals 2, versions.getItemCount()
    }

    void testOutputStrategy() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        assertNotNull frame

        ULCComboBoxOperator output = new ULCComboBoxOperator(frame, new ComponentByNameChooser("outputStrategy"))
        ULCTextFieldOperator location = new ULCTextFieldOperator(frame, new ComponentByNameChooser("resultLocation"))
        assertNotNull output
        assertNotNull location

        output.selectItem("No output")
        assertFalse location.enabled

        output.selectItem("File")
        assertTrue location.enabled
    }

    void testGetSimulation() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        assertNotNull frame

        ULCTextFieldOperator name = new ULCTextFieldOperator(frame, new ComponentByNameChooser("simulationName"))
        name.enterText("Simulation")

        ULCTextAreaOperator comment = new ULCTextAreaOperator(frame, new ComponentByNameChooser("comment"))
        comment.typeText("comment")

        ULCTextFieldOperator iterations = new ULCTextFieldOperator(frame, new ComponentByNameChooser("iterations"))
        iterations.enterText("10")

        ULCComboBoxOperator param = new ULCComboBoxOperator(frame, new ComponentByNameChooser("parameterizationNames"))
        param.selectItem "CoreMultiPeriodParameters"

        ULCCheckBoxOperator userDefinedSeed = new ULCCheckBoxOperator(frame, new ComponentByNameChooser("userDefinedRandomSeed"))
        userDefinedSeed.clickMouse()
        ULCTextFieldOperator randomSeed = new ULCTextFieldOperator(frame, new ComponentByNameChooser("randomSeed"))
        randomSeed.enterText("1234")
        Simulation simulation = pane.model.simulation
        assertEquals "Simulation", simulation.name
        assertEquals("1", simulation.modelVersionNumber.toString())
        //ART-588, make sure that the model version from Model.getModelVersion() is used, even when a 'higher' one exists ("1")
        assertEquals "comment", simulation.comment
        assertEquals 10, simulation.numberOfIterations
        assertEquals "CoreMultiPeriodParameters", simulation.parameterization.name
        assertEquals 2, simulation.periodCount
        assertEquals 1234, simulation.randomSeed
    }
}
