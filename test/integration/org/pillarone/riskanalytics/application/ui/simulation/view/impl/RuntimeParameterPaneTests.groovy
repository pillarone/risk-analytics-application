package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.ServerSideCommand
import models.application.ApplicationModel
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.example.parameter.ExampleEnum
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import com.ulcjava.testframework.operator.*

class RuntimeParameterPaneTests extends AbstractSimpleFunctionalTest {

    SimulationSettingsPane pane


    protected void doStart() {
        LocaleResources.setTestMode()
        ModellingItemFactory.clear()

        FileImportService.importModelsIfNeeded(["Application"])
        Parameterization parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('ApplicationParameters'))
        parameterization.load()
        Parameterization extraParameterization = ModellingItemFactory.incrementVersion(parameterization)
        extraParameterization.save()


        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"

        pane = new SimulationSettingsPane(new SimulationSettingsPaneModel(ApplicationModel))
        frame.setContentPane(pane.content)
        frame.visible = true
    }


    public void stop() {
        LocaleResources.clearTestMode()
    }

    public void testRuntimeParameters() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        assertNotNull frame

        ULCTabbedPaneOperator tabbedPaneOperator = new ULCTabbedPaneOperator(frame)
        tabbedPaneOperator.selectPage(1)

        ULCTextFieldOperator stringParameter = new ULCTextFieldOperator(tabbedPaneOperator, new ComponentByNameChooser("runtimeStringParameter"))
        assertEquals("test", stringParameter.text)

        stringParameter.enterText("new value")

        ULCTextFieldOperator doubleParameter = new ULCTextFieldOperator(tabbedPaneOperator, new ComponentByNameChooser("runtimeDoubleParameter"))
        assertEquals("1.1", doubleParameter.text)

        doubleParameter.enterText("2.2")

        ULCTextFieldOperator integerParameter = new ULCTextFieldOperator(tabbedPaneOperator, new ComponentByNameChooser("runtimeIntParameter"))
        assertEquals("5", integerParameter.text)

        integerParameter.enterText("10")

        ULCCheckBoxOperator booleanParameter = new ULCCheckBoxOperator(tabbedPaneOperator, new ComponentByNameChooser("runtimeBooleanParameter"))
        assertTrue(booleanParameter.selected)

        booleanParameter.clickMouse()

        ULCComboBoxOperator enumParameter = new ULCComboBoxOperator(tabbedPaneOperator, new ComponentByNameChooser("runtimeEnumParameter"))
        assertEquals(ExampleEnum.SECOND_VALUE.toString(), enumParameter.selectedItem)

        enumParameter.selectItem(ExampleEnum.FIRST_VALUE.toString())

        ULCSpinnerOperator dateParameter = new ULCSpinnerOperator(tabbedPaneOperator, new ComponentByNameChooser("runtimeDateParameter"))
        assertEquals(new DateTime(2011, 1, 1, 0, 0, 0, 0).millis, dateParameter.getValue().time)

        tabbedPaneOperator.selectPage(0)

        ULCTextFieldOperator iterations = new ULCTextFieldOperator(tabbedPaneOperator, new ComponentByNameChooser("iterations"))
        iterations.enterText("10")

        runVoidCommand(new ServerSideCommand() {
            @Override
            protected void proceedOnServer() {
                final Simulation simulation = pane.model.simulation
                final List<ParameterHolder> parameters = simulation.runtimeParameters
                assertEquals(7, parameters.size())

                ParameterHolder param = parameters.find { it.path == "runtimeStringParameter"}
                assertEquals("new value", param.businessObject)

                param = parameters.find { it.path == "runtimeIntParameter"}
                assertEquals(10, param.businessObject)

                param = parameters.find { it.path == "runtimeDoubleParameter"}
                assertEquals(2.2d, param.businessObject)

                param = parameters.find { it.path == "runtimeBooleanParameter"}
                assertEquals(false, param.businessObject)

                param = parameters.find { it.path == "runtimeEnumParameter"}
                assertEquals(ExampleEnum.FIRST_VALUE, param.businessObject)

                param = parameters.find { it.path == "runtimeDateParameter"}
                assertEquals(new DateTime(2011, 1, 1, 0, 0, 0, 0), param.businessObject)
            }

        })
    }
}
