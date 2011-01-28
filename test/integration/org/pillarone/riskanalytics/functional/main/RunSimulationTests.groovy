package org.pillarone.riskanalytics.functional.main

import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationActionsPane
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationSettingsPane
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RunSimulationTests extends AbstractFunctionalTestCase {

    protected void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['CoreParameters'])
        super.setUp();
    }

    public void testRunSimulation() {
        ULCTableTreeOperator tableTree = getSelectionTreeRowHeader()
        showPopupOnParameterizationGroupNode(tableTree, "Core", "Run simulation ...")
        ULCTextFieldOperator iterations = getTextFieldOperator("${SimulationSettingsPane.getSimpleName()}.iterations")
        iterations.typeText("10")
        getButtonOperator("${SimulationActionsPane.getSimpleName()}.run").clickMouse()
        ULCButtonOperator resultButton = getButtonOperator("${SimulationActionsPane.getSimpleName()}.openResults")
        wait({resultButton.isEnabled()}, 500, 5000)
        getButtonOperator("${SimulationActionsPane.getSimpleName()}.openResults").clickMouse()
        //TODO finish test
    }

    public wait(Closure condition, int millis, int timeoutMillis) {
        long end = System.currentTimeMillis() + timeoutMillis

        while (!condition.call()) {
            if (System.currentTimeMillis() > end) {
                return
            }
            Thread.sleep(500)
        }
    }

}
