package org.pillarone.riskanalytics.application.ui.interaction

import com.ulcjava.testframework.standalone.AbstractStandaloneTestCase
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.output.DBCleanUpService
import org.pillarone.riskanalytics.application.ui.P1RATApplication
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import com.ulcjava.testframework.operator.*

class RenameActionTests extends AbstractStandaloneTestCase {


    protected void setUp() {
        FileImportService.importModelsIfNeeded(["Application"])
        ModellingItemFactory.clear()
        super.setUp();
    }

    protected void tearDown() {
        super.tearDown();
        new DBCleanUpService().cleanUp()
    }

    protected Class getApplicationClass() {
        return P1RATApplication
    }

    void testRenameParameter() {
        ULCFrameOperator frame = new ULCFrameOperator("Risk Analytics")
        ULCTreeOperator tree = new ULCTreeOperator(frame, new ComponentByNameChooser("selectionTree"))

        TreePath pathForRename = tree.findPath(["Application", "Parameterization", "ApplicationParameters"] as String[])
        assertNotNull "path not found", pathForRename

        int oldParametersCount = tree.getChildCount(pathForRename.lastPathComponent.parent)

        ULCPopupMenuOperator popUpMenu = tree.callPopupOnPath(pathForRename)
        assertNotNull popUpMenu
        popUpMenu.pushMenu("Rename")

        ULCDialogOperator renameDialog = new ULCDialogOperator(frame, new ComponentByNameChooser('renameDialog'))
        assertNotNull renameDialog

        ULCTextFieldOperator newNameField = new ULCTextFieldOperator(renameDialog, new ComponentByNameChooser('newName'))
        assertNotNull newNameField

        newNameField.clearText()
        newNameField.typeText('RenamedParameters')

        ULCButtonOperator okButton = new ULCButtonOperator(renameDialog, new ComponentByNameChooser('okButton'))
        assertNotNull okButton

        okButton.getFocus()
        okButton.clickMouse()

        TreePath newPath = tree.findPath(["Application", "Parameterization", "RenamedParameters v1"] as String[])
        assertNotNull "path not found", newPath
        assertEquals "element added", oldParametersCount, tree.getChildCount(newPath.lastPathComponent.parent)

    }

    // TODO (Apr 16, 2009, msh): Test rename of used items
}