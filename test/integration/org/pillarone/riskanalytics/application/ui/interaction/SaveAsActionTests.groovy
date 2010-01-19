package org.pillarone.riskanalytics.application.ui.interaction

import com.ulcjava.testframework.standalone.AbstractStandaloneTestCase
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.output.DBCleanUpService
import org.pillarone.riskanalytics.application.ui.P1RATApplication
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import com.ulcjava.testframework.operator.*

class SaveAsActionTests extends AbstractStandaloneTestCase {


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

    void testSaveAsParameter() {
        ULCFrameOperator frame = new ULCFrameOperator("Risk Analytics")
        ULCTreeOperator tree = new ULCTreeOperator(frame, new ComponentByNameChooser("selectionTree"))

        TreePath pathForRename = tree.findPath(["Application", "Parameterization", "ApplicationParameters"] as String[])
        assertNotNull "path not found", pathForRename

        ULCPopupMenuOperator popUpMenu = tree.callPopupOnPath(pathForRename)
        assertNotNull popUpMenu
        popUpMenu.pushMenu("Save as ...")

        ULCDialogOperator renameDialog = new ULCDialogOperator(frame, new ComponentByNameChooser('renameDialog'))
        assertNotNull renameDialog

        ULCTextFieldOperator newNameField = new ULCTextFieldOperator(renameDialog, new ComponentByNameChooser('newName'))
        assertNotNull newNameField

        newNameField.clearText()
        newNameField.typeText('SavedAsParameters')

        ULCButtonOperator okButton = new ULCButtonOperator(renameDialog, new ComponentByNameChooser('okButton'))
        assertNotNull okButton

        okButton.getFocus()
        okButton.clickMouse()

        TreePath oldPath = tree.findPath(["Application", "Parameterization", "ApplicationParameters"] as String[])
        assertNotNull "old path not found", oldPath

        TreePath newPath = tree.findPath(["Application", "Parameterization", "SavedAsParameters v1"] as String[])
        assertNotNull "new path not found", newPath
    }

    // TODO (Apr 16, 2009, msh): Test saveAs of other items
}