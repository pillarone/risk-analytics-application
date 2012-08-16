package org.pillarone.riskanalytics.application.ui.interaction

import com.ulcjava.base.application.event.KeyEvent
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase
import com.ulcjava.testframework.operator.*

class RenameActionTests extends AbstractFunctionalTestCase {

    protected void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(["Core"])
        super.setUp()
    }

    void testRenameParameter() {
        ULCFrameOperator frame = getMainFrameOperator()
        ULCTableTreeOperator tree = getSelectionTableTreeRowHeader()

        TreePath pathForRename = tree.findPath(["Core", "Parameterization", "CoreParameters"] as String[])
        TreePath parameterizationNodePath = tree.findPath(["Core", "Parameterization"] as String[])
        assertNotNull "path not found", pathForRename

        int oldParametersCount = tree.getChildCount(parameterizationNodePath.lastPathComponent)

        tree.doExpandRow(0)
        tree.doExpandRow(1)

        int row = tree.getRowForPath(pathForRename)
        tree.selectCell(row, 0)

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
        assertEquals "element added", oldParametersCount, tree.getChildCount(parameterizationNodePath.lastPathComponent)

    }

}