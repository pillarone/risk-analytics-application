package org.pillarone.riskanalytics.application.ui.interaction

import com.ulcjava.base.application.ULCFrame
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.environment.jnlp.P1RATFrameViewFactory
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import com.ulcjava.testframework.operator.*
import com.ulcjava.base.application.event.KeyEvent

class RefreshActionTests extends AbstractSimpleFunctionalTest {

    String newParameterizationName


    protected void doStart() {
        LocaleResources.setTestMode()
        FileImportService.importModelsIfNeeded(["Core"])
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
        ULCTableTreeOperator tree1 = new ULCTableTreeOperator(frame1, new ComponentByNameChooser("selectionTreeRowHeader"))
        tree1.doExpandRow 0
        tree1.doExpandRow 1


        TreePath pathForRename = tree1.findPath(["Core", "Parameterization", "CoreMultiPeriodParameters"] as String[])
        assertNotNull "path not found", pathForRename

        int row = tree1.getRowForPath(pathForRename)
        tree1.selectCell(row, 0)
        sleep 1000
        tree1.pushKey(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK)

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

        ULCTableTreeOperator tree2 = new ULCTableTreeOperator(frame2, new ComponentByNameChooser("selectionTreeRowHeader"))
        tree2.doExpandRow 0
        tree2.doExpandRow 1

        TreePath path = tree2.findPath(["Core", "Parameterization"] as String[])
        assertNotNull "path not found", path
        int childCountBeforeInsert = tree2.getChildCount(path)
        ULCButtonOperator refreshButton = new ULCButtonOperator(frame2, new ComponentByNameChooser("refresh"))
        refreshButton.clickMouse()

        path = tree2.findPath(["Core", "Parameterization"] as String[])
        assertEquals "childCount after refresh", childCountBeforeInsert + 1, tree2.getChildCount(path)
    }

    void testRefreshAfterDelete() {
        ULCFrameOperator frame1 = new ULCFrameOperator("first")
        ULCTableTreeOperator tree1 = new ULCTableTreeOperator(frame1, new ComponentByNameChooser("selectionTreeRowHeader"))
        tree1.doExpandRow 0
        tree1.doExpandRow 1


        TreePath pathForRename = tree1.findPath(["Core", "Parameterization", "CoreMultiPeriodParameters"] as String[])
        assertNotNull "path not found", pathForRename

        TreePath path = tree1.findPath(["Core", "Parameterization"] as String[])
        int childCountBeforeInsert = tree1.getChildCount(path)

        int row = tree1.getRowForPath(pathForRename)
        tree1.selectCell(row,0)
        sleep 1000
        tree1.pushKey(KeyEvent.VK_DELETE)

        assertNotNull "path not found", path
        ULCDialogOperator alertDialog = new ULCDialogOperator(frame1, new ComponentByNameChooser('AlertDialog'))
        assertNotNull alertDialog

        ULCButtonOperator okButton = new ULCButtonOperator(alertDialog, new ComponentByNameChooser('AlertDialog.ok'))
        assertNotNull okButton

        okButton.getFocus()
        okButton.clickMouse()


        ULCButtonOperator refreshButton = new ULCButtonOperator(frame1, new ComponentByNameChooser("refresh"))
        refreshButton.clickMouse()
        path = tree1.findPath(["Core", "Parameterization"] as String[])
        assertEquals "childCount after refresh", childCountBeforeInsert - 1, tree1.getChildCount(path)
    }

    void testRefreshAfterRename() {
        ULCFrameOperator frame1 = new ULCFrameOperator("first")
        ULCTableTreeOperator tree1 = new ULCTableTreeOperator(frame1, new ComponentByNameChooser("selectionTreeRowHeader"))
        tree1.doExpandRow 0
        tree1.doExpandRow 1


        TreePath pathForRename = tree1.findPath(["Core", "Parameterization", "CoreMultiPeriodParameters"] as String[])
        assertNotNull "path not found", pathForRename

        int row = tree1.getRowForPath(pathForRename)
        tree1.selectCell(row, 0)
        sleep 1000
        tree1.pushKey(KeyEvent.VK_F2)

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

        ULCTableTreeOperator tree2 = new ULCTableTreeOperator(frame2, new ComponentByNameChooser("selectionTreeRowHeader"))
        tree2.doExpandRow 0
        tree2.doExpandRow 1


        TreePath path = tree2.findPath(["Core", "Parameterization"] as String[])
        assertNotNull "path not found", path
        int childCountBeforeInsert = tree2.getChildCount(path)

        ULCButtonOperator refreshButton = new ULCButtonOperator(frame2, new ComponentByNameChooser("refresh"))
        refreshButton.clickMouse()

        path = tree2.findPath(["Core", "Parameterization"] as String[])
        assertEquals "childCount after refresh", childCountBeforeInsert, tree2.getChildCount(path)

    }


}