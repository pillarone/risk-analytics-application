package org.pillarone.riskanalytics.application.ui.interaction

import com.ulcjava.base.application.event.KeyEvent
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase
import com.ulcjava.testframework.operator.*
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService

class RenameActionTests extends AbstractFunctionalTestCase {

    protected void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(["Core"])
        super.setUp()
    }

    void testRenameParameter() {
        ULCFrameOperator frame = getMainFrameOperator()
        ULCTreeOperator tree = getSelectionTree()

        TreePath pathForRename = tree.findPath(["Core", "Parameterization", "CoreParameters"] as String[])
        assertNotNull "path not found", pathForRename

        int oldParametersCount = tree.getChildCount(pathForRename.lastPathComponent.parent)

        tree.doExpandRow(0)
        tree.doExpandRow(1)
        tree.clickOnPath(pathForRename)
        tree.pushKey(KeyEvent.VK_F2)

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

        TreePath newPath = tree.findPath(["Core", "Parameterization", "RenamedParameters v1"] as String[])
        assertNotNull "path not found", newPath
        assertEquals "element added", oldParametersCount, tree.getChildCount(newPath.lastPathComponent.parent)

    }

}