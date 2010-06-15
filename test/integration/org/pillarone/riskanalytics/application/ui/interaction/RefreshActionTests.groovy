package org.pillarone.riskanalytics.application.ui.interaction

import com.ulcjava.base.application.ULCFrame
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.environment.jnlp.P1RATFrameViewFactory
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import com.ulcjava.testframework.operator.*

class RefreshActionTests extends AbstractSimpleFunctionalTest {

    String newParameterizationName


    protected void doStart() {
        LocaleResources.setTestMode()
        FileImportService.importModelsIfNeeded(["Application"])
        ModellingItemFactory.clear()

        ULCFrame frame1 = new P1RATFrameViewFactory().create()
        frame1.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        ULCFrame frame2 = new P1RATFrameViewFactory().create()
        frame2.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame1.title = "first"
        frame2.title = "second"
        frame1.setLocation 50, 50
        frame2.setLocation 400, 100
        frame1.visible = true
        frame2.visible = true
    }

    public void stop() {
        LocaleResources.clearTestMode()
    }

    void testRefreshAfterInsert() {
        ULCFrameOperator frame1 = new ULCFrameOperator("first")
        ULCTreeOperator tree1 = new ULCTreeOperator(frame1, new ComponentByNameChooser("selectionTree"))
        tree1.doExpandRow 0
        tree1.doExpandRow 1


        TreePath pathForRename = tree1.findPath(["Application", "Parameterization", "ApplicationParameters"] as String[])
        assertNotNull "path not found", pathForRename

        ULCPopupMenuOperator popUpMenu = tree1.callPopupOnPath(pathForRename)
        assertNotNull popUpMenu
        popUpMenu.pushMenu("Save as ...")

        ULCDialogOperator renameDialog = new ULCDialogOperator(frame1, new ComponentByNameChooser('renameDialog'))
        assertNotNull renameDialog

        ULCTextFieldOperator newNameField = new ULCTextFieldOperator(renameDialog, new ComponentByNameChooser('newName'))
        assertNotNull newNameField

        newNameField.clearText()
        newNameField.typeText('newParam')

        ULCButtonOperator okButton = new ULCButtonOperator(renameDialog, new ComponentByNameChooser('okButton'))
        assertNotNull okButton

        okButton.getFocus()
        okButton.clickMouse()
        renameDialog.close()





        ULCFrameOperator frame2 = new ULCFrameOperator("second")
        frame2.getUIFrame().getBasicComponent().toFront()

        ULCTreeOperator tree2 = new ULCTreeOperator(frame2, new ComponentByNameChooser("selectionTree"))
        tree2.doExpandRow 0
        tree2.doExpandRow 1


        TreePath path = tree2.findPath(["Application", "Parameterization"] as String[])
        assertNotNull "path not found", path
        int childCountBeforeInsert = tree2.getChildCount(path)
        ULCButtonOperator refreshButton = new ULCButtonOperator(frame2, new ComponentByNameChooser("refresh"))
        refreshButton.clickMouse()
        path = tree2.findPath(["Application", "Parameterization"] as String[])
        assertEquals "childCount after refresh", childCountBeforeInsert + 1, tree2.getChildCount(path)
    }

    void testRefreshAfterDelete() {
        ULCFrameOperator frame1 = new ULCFrameOperator("first")
        ULCTreeOperator tree1 = new ULCTreeOperator(frame1, new ComponentByNameChooser("selectionTree"))
        tree1.doExpandRow 0
        tree1.doExpandRow 1


        TreePath pathForRename = tree1.findPath(["Application", "Parameterization", "ApplicationParameters"] as String[])
        assertNotNull "path not found", pathForRename

        TreePath path = tree1.findPath(["Application", "Parameterization"] as String[])
        int childCountBeforeInsert = tree1.getChildCount(path)

        ULCPopupMenuOperator popUpMenu = tree1.callPopupOnPath(pathForRename)
        assertNotNull popUpMenu
        popUpMenu.pushMenu("Delete")

        assertNotNull "path not found", path
        ULCButtonOperator refreshButton = new ULCButtonOperator(frame1, new ComponentByNameChooser("refresh"))
        refreshButton.clickMouse()
        path = tree1.findPath(["Application", "Parameterization"] as String[])
        assertEquals "childCount after refresh", childCountBeforeInsert - 1, tree1.getChildCount(path)
    }

    void testRefreshAfterRename() {
        ULCFrameOperator frame1 = new ULCFrameOperator("first")
        ULCTreeOperator tree1 = new ULCTreeOperator(frame1, new ComponentByNameChooser("selectionTree"))
        tree1.doExpandRow 0
        tree1.doExpandRow 1


        TreePath pathForRename = tree1.findPath(["Application", "Parameterization", "ApplicationParameters"] as String[])
        assertNotNull "path not found", pathForRename

        ULCPopupMenuOperator popUpMenu = tree1.callPopupOnPath(pathForRename)
        assertNotNull popUpMenu
        popUpMenu.pushMenu("Rename")

        ULCDialogOperator renameDialog = new ULCDialogOperator(frame1, new ComponentByNameChooser('renameDialog'))
        assertNotNull renameDialog

        ULCTextFieldOperator newNameField = new ULCTextFieldOperator(renameDialog, new ComponentByNameChooser('newName'))
        assertNotNull newNameField

        newNameField.clearText()
        newNameField.typeText('Renamed')

        ULCButtonOperator okButton = new ULCButtonOperator(renameDialog, new ComponentByNameChooser('okButton'))
        assertNotNull okButton

        okButton.getFocus()
        okButton.clickMouse()




        ULCFrameOperator frame2 = new ULCFrameOperator("second")
        frame2.getUIFrame().getBasicComponent().toFront()

        ULCTreeOperator tree2 = new ULCTreeOperator(frame2, new ComponentByNameChooser("selectionTree"))
        tree2.doExpandRow 0
        tree2.doExpandRow 1


        TreePath path = tree2.findPath(["Application", "Parameterization"] as String[])
        assertNotNull "path not found", path
        int childCountBeforeInsert = tree2.getChildCount(path)

        ULCButtonOperator refreshButton = new ULCButtonOperator(frame2, new ComponentByNameChooser("refresh"))
        refreshButton.clickMouse()

        path = tree2.findPath(["Application", "Parameterization"] as String[])
        assertEquals "childCount after refresh", childCountBeforeInsert, tree2.getChildCount(path)

    }


}