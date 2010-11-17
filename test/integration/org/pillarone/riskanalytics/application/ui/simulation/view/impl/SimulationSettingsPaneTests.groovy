package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ULCFrameOperator
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel
import models.core.CoreModel
import com.ulcjava.testframework.operator.ULCCheckBoxOperator
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import com.ulcjava.testframework.operator.ULCComboBoxOperator
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCFileChooserOperator
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.testframework.operator.ULCTextAreaOperator


class SimulationSettingsPaneTests extends AbstractSimpleFunctionalTest {

    SimulationSettingsPane pane
    Parameterization extraParameterization

    protected void doStart() {
        LocaleResources.setTestMode()
        ModellingItemFactory.clear()

        FileImportService.importModelsIfNeeded(["Core"])
        Parameterization parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('CoreParameters'))
        parameterization.load()
        extraParameterization = ModellingItemFactory.incrementVersion(parameterization)
        extraParameterization.save()


        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"

        pane = new SimulationSettingsPane(new SimulationSettingsPaneModel(CoreModel))
        frame.setContentPane(pane.content)
        frame.visible = true
    }


    public void stop() {
        LocaleResources.clearTestMode()
        extraParameterization.delete()
    }

    public void testRandomSeed() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        assertNotNull frame

        ULCCheckBoxOperator userDefinedSeed = new ULCCheckBoxOperator(frame, new ComponentByNameChooser("userDefinedRandomSeed"))
        assertNotNull userDefinedSeed

        ULCTextFieldOperator randomSeed = new ULCTextFieldOperator(frame, new ComponentByNameChooser("randomSeed"))
        assertNotNull randomSeed
        assertFalse randomSeed.isEnabled()

        userDefinedSeed.clickMouse()
        assertTrue randomSeed.isEnabled()

        randomSeed.enterText("1234")
        assertEquals 1234, pane.model.randomSeed

        userDefinedSeed.clickMouse()
        assertFalse randomSeed.isEnabled()
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
        assertEquals 1, versions.getItemCount()

        names.selectItem("CoreParameters")
        assertEquals 2, versions.getItemCount()
    }

    void testOutputStrategy() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        assertNotNull frame

        ULCComboBoxOperator output = new ULCComboBoxOperator(frame, new ComponentByNameChooser("outputStrategy"))
        ULCTextFieldOperator location = new ULCTextFieldOperator(frame, new ComponentByNameChooser("resultLocation"))
        assertNotNull output
        assertNotNull location

        output.selectItem("No output")
        assertFalse location.isEnabled()

        output.selectItem("File")
        assertTrue location.isEnabled()
    }

    // todo: selection of result location is currently disabled on kti branch
//    void testResultLocation() {
//        ULCFrameOperator frame = new ULCFrameOperator("test")
//        assertNotNull frame
//
//        ULCComboBoxOperator output = new ULCComboBoxOperator(frame, new ComponentByNameChooser("outputStrategy"))
//        ULCTextFieldOperator location = new ULCTextFieldOperator(frame, new ComponentByNameChooser("resultLocation"))
//        ULCButtonOperator button = new ULCButtonOperator(frame, new ComponentByNameChooser("changeLocation"))
//        assertNotNull output
//        assertNotNull location
//        assertNotNull button
//
//        output.selectItem("File")
//
//        button.clickMouse()
//
//        ULCFileChooserOperator fileChooser = new ULCFileChooserOperator()
//        fileChooser.pathField.enterText("result")
//
//        assertTrue location.text.endsWith("result")
//
//    }

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
        ULCTextFieldOperator randomSeed = new ULCTextFieldOperator(frame, new ComponentByNameChooser("randomSeed"))
        userDefinedSeed.clickMouse()
        randomSeed.enterText("1234")

        Simulation simulation = pane.model.getSimulation()
        assertEquals "Simulation", simulation.name
        assertEquals "comment", simulation.comment
        assertEquals 10, simulation.numberOfIterations
        assertEquals "CoreMultiPeriodParameters", simulation.parameterization.name
        assertEquals 2, simulation.periodCount
        assertEquals 1234, simulation.randomSeed

    }
}
