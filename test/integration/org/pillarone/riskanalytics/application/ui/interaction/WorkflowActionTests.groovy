package org.pillarone.riskanalytics.application.ui.interaction

import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCDialogOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCMenuItemOperator
import com.ulcjava.testframework.operator.ULCPopupMenuOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import com.ulcjava.testframework.operator.ULCTextAreaOperator
import com.ulcjava.testframework.operator.ULCTreeOperator
import com.ulcjava.testframework.standalone.AbstractStandaloneTestCase
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.P1RATApplication
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry
import org.pillarone.riskanalytics.core.workflow.AuditLog

import javax.swing.tree.TreePath

class WorkflowActionTests extends AbstractStandaloneTestCase {

    protected void setUp() {
        FileImportService.importModelsIfNeeded(["Application"])
        ModellingItemFactory.clear()
        LocaleResources.setTestMode()
        super.setUp()
        UserContext.metaClass.static.hasCurrentUser = {->
            true
        }
    }

    protected void tearDown() {
        super.tearDown();
        LocaleResources.clearTestMode()
    }

    protected Class getApplicationClass() {
        return P1RATApplication
    }

    void testDeleteP14NInWorkflow() {
        ULCFrameOperator frame = new ULCFrameOperator("Risk Analytics")
        ULCTableTreeOperator itemTree = new ULCTableTreeOperator(frame, new ComponentByNameChooser("selectionTreeRowHeader"))
        TreePath applicationModelPath = itemTree.findPath('Application')
        int applicationModelRow = itemTree.getRowForPath(applicationModelPath)
        itemTree.doExpandRow(applicationModelRow)
        itemTree.doExpandRow(applicationModelRow + 1)
        itemTree.selectRow(applicationModelRow + 2)
        ULCPopupMenuOperator popupMenu = itemTree.callPopupOnCell(applicationModelRow + 2, 0)
        ULCMenuItemOperator startWorkflow = new ULCMenuItemOperator(popupMenu, "Start workflow")
        startWorkflow.clickMouse()
        ULCDialogOperator dealLink = new ULCDialogOperator(new ComponentByNameChooser('dealDialog'))
        new ULCButtonOperator(dealLink, new ComponentByNameChooser('okButton')).clickMouse()

        ULCDialogOperator newVersion = new ULCDialogOperator(new ComponentByNameChooser('renameDialog'))
        ULCTextAreaOperator commentTextArea = new ULCTextAreaOperator(newVersion, new ComponentByNameChooser('commentTextArea'))
        commentTextArea.enterText('test')
        new ULCButtonOperator(newVersion, new ComponentByNameChooser('okButton')).clickMouse()
        sleep(2000)
        itemTree.selectRow(3)
        popupMenu = itemTree.callPopupOnCell(applicationModelRow + 3, 0)
        ULCMenuItemOperator sendToReview = new ULCMenuItemOperator(popupMenu, "Send to reviewer")
        sendToReview.clickMouse()
        sleep(2000)
        popupMenu = itemTree.callPopupOnCell(applicationModelRow + 3, 0)
        ULCMenuItemOperator sendToProduction = new ULCMenuItemOperator(popupMenu, "Send to production")
        sendToProduction.clickMouse()
        sleep(2000)
        popupMenu = itemTree.callPopupOnCell(applicationModelRow + 3, 0)
        ULCMenuItemOperator createNewVersion = new ULCMenuItemOperator(popupMenu, "Create new version")
        createNewVersion.clickMouse()
        newVersion = new ULCDialogOperator(new ComponentByNameChooser('renameDialog'))
        commentTextArea = new ULCTextAreaOperator(newVersion, new ComponentByNameChooser('commentTextArea'))
        commentTextArea.enterText('test')
        new ULCButtonOperator(newVersion, new ComponentByNameChooser('okButton')).clickMouse()

        //V2 added to the tree.
        itemTree.selectRow(3)
        int row = itemTree.getRowForPath(itemTree.findPath(['Application', 'Parameterization', 'Connection failed - contact support. vR2'] as String[]))
        assert applicationModelRow + 3 == row
        popupMenu = itemTree.callPopupOnCell(applicationModelRow + 3, 0)
        ULCMenuItemOperator delete = new ULCMenuItemOperator(popupMenu, "Delete")
        delete.clickMouse()
        ULCDialogOperator deleteConfirm = new ULCDialogOperator(new ComponentByNameChooser('AlertDialog'))
        ULCButtonOperator okButton = new ULCButtonOperator(deleteConfirm, new ComponentByNameChooser('AlertDialog.ok'))
        okButton.clickMouse()
        sleep(2000)

        //V2 removed from the tree.
        row = itemTree.getRowForPath(itemTree.findPath(['Application', 'Parameterization', 'Connection failed - contact support. vR1'] as String[]))
        assert applicationModelRow + 3 == row
    }

}
