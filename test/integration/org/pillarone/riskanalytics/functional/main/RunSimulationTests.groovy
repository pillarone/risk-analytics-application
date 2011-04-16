package org.pillarone.riskanalytics.functional.main

import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationActionsPane
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
        ULCTableTreeOperator tableTree = getSelectionTableTreeRowHeader()
        pushKeyOnPath(tableTree, tableTree.findPath(["Core", "Parameterization"] as String[]), KeyEvent.VK_F9, 0)
        ULCTextFieldOperator iterations = getTextFieldOperator("iterations")
        iterations.typeText("10")
        getButtonOperator("${SimulationActionsPane.getSimpleName()}.run").clickMouse()
        ULCButtonOperator resultButton = getButtonOperator("${SimulationActionsPane.getSimpleName()}.openResults")
        wait({resultButton.isEnabled()}, 500, 5000)
//        getButtonOperator("${SimulationActionsPane.getSimpleName()}.openResults").clickMouse()
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
