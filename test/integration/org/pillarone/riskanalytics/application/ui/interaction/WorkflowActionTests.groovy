package org.pillarone.riskanalytics.application.ui.interaction

import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCPopupMenuOperator
import com.ulcjava.testframework.operator.ULCTreeOperator
import com.ulcjava.testframework.standalone.AbstractStandaloneTestCase
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.P1RATApplication
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.output.DBCleanUpService
import javax.swing.tree.TreePath

class WorkflowActionTests extends AbstractStandaloneTestCase {

    protected void setUp() {
        FileImportService.importModelsIfNeeded(["Application"])
        ModellingItemFactory.clear()
        LocaleResources.setTestMode()
        super.setUp();
    }

    protected void tearDown() {
        super.tearDown();
        LocaleResources.clearTestMode()
    }

    protected Class getApplicationClass() {
        return P1RATApplication
    }

    //TODO: refactor with several roles
    void testWorkflow() {
        return
        ULCFrameOperator frame = new ULCFrameOperator("Risk Analytics")
        ULCTreeOperator tree = new ULCTreeOperator(frame, new ComponentByNameChooser("selectionTree"))

        TreePath pathForRename = tree.findPath(["Application", "Parameterization", "Normal", "ApplicationParameters"] as String[])
        assertNotNull "path not found", pathForRename

        ULCPopupMenuOperator popUpMenu = tree.callPopupOnPath(pathForRename)
        assertNotNull popUpMenu
        popUpMenu.pushMenu("Start workflow")

        TreePath newPath = tree.findPath(["Application", "Parameterization", "Workflow", "ApplicationParameters vR1"] as String[])
        assertNotNull "path not found", newPath

        popUpMenu = tree.callPopupOnPath(newPath)
        assertNotNull popUpMenu
        popUpMenu.pushMenu("Send to reviewer")

        popUpMenu = tree.callPopupOnPath(newPath)
        popUpMenu.pushMenu("Reject")

        newPath = tree.findPath(["Application", "Parameterization", "Workflow", "ApplicationParameters vR2"] as String[])
        assertNotNull "path not found", newPath

        popUpMenu = tree.callPopupOnPath(newPath)
        assertNotNull popUpMenu
        popUpMenu.pushMenu("Send to reviewer")

        popUpMenu = tree.callPopupOnPath(newPath)
        popUpMenu.pushMenu("In production")

    }

}
