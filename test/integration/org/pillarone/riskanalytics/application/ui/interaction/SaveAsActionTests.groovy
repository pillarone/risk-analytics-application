package org.pillarone.riskanalytics.application.ui.interaction

import com.ulcjava.testframework.standalone.AbstractStandaloneTestCase
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.P1RATApplication
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.output.DBCleanUpService
import com.ulcjava.testframework.operator.*

class SaveAsActionTests extends AbstractStandaloneTestCase {


    protected void setUp() {
        new DBCleanUpService().cleanUp()
        FileImportService.importModelsIfNeeded(["Core"])
        ModellingItemFactory.clear()
        LocaleResources.setTestMode()
        super.setUp();
    }

    protected void tearDown() {
        super.tearDown();
        LocaleResources.clearTestMode()
        new DBCleanUpService().cleanUp()
    }

    protected Class getApplicationClass() {
        return P1RATApplication
    }

    void testSaveAsParameter() {
        ULCFrameOperator frame = new ULCFrameOperator("Risk Analytics")
        ULCTableTreeOperator tree = new ULCTableTreeOperator(frame, new ComponentByNameChooser("selectionTreeRowHeader"))
        assertNotNull tree

        TreePath pathForRename = tree.findPath(["Core", "Parameterization", "CoreAlternativeParameters"] as String[])
        assertNotNull "path not found", pathForRename

        tree.doExpandRow 0
        tree.doExpandRow 1
        tree.doExpandRow 2

        ULCPopupMenuOperator popUpMenu = tree.callPopupOnCell(3, 0)

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

        TreePath oldPath = tree.findPath(["Core", "Parameterization", "CoreAlternativeParameters"] as String[])
        assertNotNull "old path not found", oldPath

        TreePath newPath = tree.findPath(["Core", "Parameterization", "SavedAsParameters v1"] as String[])
        assertNotNull "new path not found", newPath
    }

    // TODO (Apr 16, 2009, msh): Test saveAs of other items
}