package org.pillarone.riskanalytics.functional.main

import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import com.ulcjava.testframework.operator.ULCTreeOperator
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase
import com.ulcjava.base.application.event.KeyEvent

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RunSimulationTests extends AbstractFunctionalTestCase {

    protected void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['CoreParameters'])
        super.setUp();
    }

    public void testRunSimulation() {
        ULCTreeOperator tree = getSelectionTree()
        pushKeyOnPath(tree, tree.findPath(["Core", "Parameterization"] as String[]), KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK)
        ULCTextFieldOperator iterations = getTextFieldOperator("iterations")
        iterations.typeText("10")
        getButtonOperator("run").clickMouse()
        ULCButtonOperator resultButton = getButtonOperator("openResults")
        wait({resultButton.isEnabled()}, 500, 5000)
//        getButtonOperator("openResults").clickMouse()
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
