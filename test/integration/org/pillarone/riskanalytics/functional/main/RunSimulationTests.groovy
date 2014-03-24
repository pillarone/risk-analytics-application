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

    void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['Core'])
        super.setUp();
    }

    void tearDown() {
        super.tearDown()
    }

    void testRunSimulation() {
        ULCTableTreeOperator tableTree = selectionTableTreeRowHeader
        pushKeyOnPath(tableTree, tableTree.findPath(["Core", "Parameterization"] as String[]), KeyEvent.VK_F9, 0)
        ULCTextFieldOperator iterations = getTextFieldOperator("iterations")
        iterations.typeText("10")
        getButtonOperator("${SimulationActionsPane.simpleName}.run").clickMouse()
        ULCButtonOperator resultButton = getButtonOperator("${SimulationActionsPane.simpleName}.openResults")
        wait({ resultButton.enabled }, 5000)
        //TODO finish test
    }

    private wait(Closure condition, int timeoutMillis) {
        long end = System.currentTimeMillis() + timeoutMillis

        while (!condition.call()) {
            if (System.currentTimeMillis() > end) {
                return
            }
            sleep(500)
        }
    }

}
