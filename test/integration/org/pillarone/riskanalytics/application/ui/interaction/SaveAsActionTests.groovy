package org.pillarone.riskanalytics.application.ui.interaction

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.ModellingInformationTableTreeModel

import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase
import com.ulcjava.testframework.operator.*

class SaveAsActionTests extends AbstractFunctionalTestCase {


    protected void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(["Core"])
        super.setUp()
    }

    void testSaveAsParameter() {
        ULCFrameOperator frame = getMainFrameOperator()
        ULCTableTreeOperator tree = getSelectionTableTreeRowHeader()
        printTree(tree.ULCTableTree)

        TreePath pathForRename = tree.findPath(["Core", "Parameterization", "CoreParameters"] as String[])
        assertNotNull "path not found", pathForRename

        tree.doExpandPath(pathForRename.parentPath)
        int row = tree.getRowForPath(pathForRename)


        tree.selectCell(row, 0)

        tree.pushKey(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK)

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

        TreePath oldPath = tree.findPath(["Core", "Parameterization", "CoreParameters"] as String[])
        assertNotNull "old path not found", oldPath

        TreePath newPath = tree.findPath(["Core", "Parameterization", "SavedAsParameters v1"] as String[])
        assertNotNull "new path not found", newPath
    }
}