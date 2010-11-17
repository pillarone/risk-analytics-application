package org.pillarone.riskanalytics.application.ui.interaction

import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.output.DBCleanUpService

import com.ulcjava.testframework.standalone.AbstractStandaloneTestCase
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.P1RATApplication
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.testframework.operator.*

class RenameActionTests extends AbstractStandaloneTestCase {


    protected void setUp() {
        FileImportService.importModelsIfNeeded(["Application"])
        ModellingItemFactory.clear()
        LocaleResources.setTestMode()
        super.setUp();
    }

    protected void tearDown() {
        super.tearDown();
        new DBCleanUpService().cleanUp()
        LocaleResources.clearTestMode()
    }

    protected Class getApplicationClass() {
        return P1RATApplication
    }

    void testRenameParameter() {
        ULCFrameOperator frame = new ULCFrameOperator("Risk Analytics")

        ULCTableTreeOperator tableTree = new ULCTableTreeOperator(frame, new ComponentByNameChooser("selectionTreeRowHeader"))
        assertNotNull tableTree

        TreePath pathForRename = tableTree.findPath(["Application", "Parameterization", "ApplicationParameters"] as String[])
        assertNotNull "path not found", pathForRename
        int oldParametersCount = tableTree.getChildCount(pathForRename.lastPathComponent.parent)
        tableTree.doExpandRow 0
        tableTree.doExpandRow 1
        tableTree.doExpandRow 2

        ULCPopupMenuOperator popUpMenu = tableTree.callPopupOnCell(3, 0)
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

        TreePath newPath = tableTree.findPath(["Application", "Parameterization", "RenamedParameters v1"] as String[])
        assertNotNull "path not found", newPath
        assertEquals "element added", oldParametersCount, tableTree.getChildCount(newPath.lastPathComponent.parent)

    }

    // TODO (Apr 16, 2009, msh): Test rename of used items
}