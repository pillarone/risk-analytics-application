package org.pillarone.riskanalytics.application.ui.main.view.item

import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.fileimport.ResultStructureImportService
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.DBCleanUpService
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.testframework.operator.ULCToggleButtonOperator
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCCheckBoxOperator
import com.ulcjava.testframework.operator.ULCComboBoxOperator
import org.pillarone.riskanalytics.application.ui.result.view.CompareSimulationsCriteriaView

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractCompareSimulationUIItemTests extends AbstractSimulationUIItemTest {

    @Override
    AbstractUIItem createUIItem() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['ApplicationParameters'])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['ApplicationResultConfiguration'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['ApplicationStructure'])
        new ResultStructureImportService().compareFilesAndWriteToDB(['ApplicationDefaultResultTree'])

        ModellingItemFactory.clear()

        init()

        SimulationRun run1 = createResults("run1", 1)
        SimulationRun run2 = createResults("run2", 2)

        Simulation simulation1 = new Simulation("run1")
        simulation1.load()

        Simulation simulation2 = new Simulation("run2")
        simulation2.load()
        Model model = new ApplicationModel()

        RiskAnalyticsMainModel mainModel = new RiskAnalyticsMainModel()
        CompareSimulationUIItem uiItem = new CompareSimulationUIItem(mainModel, model, [simulation1, simulation2])
        return uiItem
    }

    protected void addKeyFigureFunction(ULCFrameOperator frameOperator, String checkBoxName) {
        ULCCheckBoxOperator checkBoxOperator = new ULCCheckBoxOperator(frameOperator, new ComponentByNameChooser(checkBoxName))
        assertNotNull checkBoxOperator

        checkBoxOperator.getFocus()

        checkBoxOperator.clickMouse()

        Thread.sleep(1000)
    }

    public void changeSelection(ULCFrameOperator frameOperator, int index) {
        String name = "${CompareSimulationsCriteriaView.class.getSimpleName()}.simulationComboBox"
        ULCComboBoxOperator comboBoxOperator = new ULCComboBoxOperator(frameOperator, new ComponentByNameChooser(name))

        assertNotNull comboBoxOperator

        comboBoxOperator.getFocus()

        comboBoxOperator.selectItem(index)

        Thread.sleep(1000)
    }

}
