package org.pillarone.riskanalytics.functional.main

import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import com.ulcjava.testframework.operator.ULCTreeOperator
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.functional.P1RATTestFunctions

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RunSimulationTests extends P1RATTestFunctions {

    protected void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['CoreParameters'])
        super.setUp();
    }

    public void testRunSimulation() {
        ULCTreeOperator tableTree = getSelectionTree()
        TreePath path = tableTree.findPath(["Core", "Parameterization"] as String[])
        assertNotNull "path not found", path
        popUpContextMenu(path, "Run simulation ...", tableTree)
        ULCTextFieldOperator iterations = getTextFieldOperator("iterations")
        iterations.typeText("10")
        getButtonOperator("run").clickMouse()
        ULCButtonOperator resultButton = getButtonOperator("openResults")
        wait({resultButton.isEnabled()}, 500, 5000)
        getButtonOperator("openResults").clickMouse()
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
