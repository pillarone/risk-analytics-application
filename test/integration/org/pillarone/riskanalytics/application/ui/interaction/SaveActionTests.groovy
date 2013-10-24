package org.pillarone.riskanalytics.application.ui.interaction

import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.testframework.operator.*
import models.core.CoreModel
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.EnumTagType
import org.pillarone.riskanalytics.core.workflow.AuditLog
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase

import javax.swing.tree.TreePath

class SaveActionTests extends AbstractFunctionalTestCase {

    protected void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(["Core"])
        super.setUp()
        new Tag(name: NewCommentView.VERSION_COMMENT, tagType: EnumTagType.COMMENT).save()
        new Tag(name: NewCommentView.REPORT, tagType: EnumTagType.COMMENT).save()
    }

    @Override
    protected void tearDown() {
        AuditLog.list().each {
            it.delete(flush: true)
        }
        super.tearDown()
    }

    void testSaveParameterization_noWorkflow() {
        TreePath pathForRename = selectionTableTreeRowHeader.findPath(["Core", "Parameterization", "CoreParameters"] as String[])
        assertNotNull "path not found", pathForRename

        selectionTableTreeRowHeader.doExpandPath(pathForRename.parentPath)
        int row = selectionTableTreeRowHeader.getRowForPath(pathForRename)


        runSimulation(selectionTableTreeRowHeader, row)
        addCommentAndSave(mainFrameOperator)
        handleNewVersionDialog()
        assertStatus('CoreParameters', '2', Status.NONE)
    }

    void testSaveParameterization_withWorkflow() {
        ParameterizationDAO.withNewSession {
            ParameterizationDAO dao = ParameterizationDAO.find('CoreParameters', CoreModel.class.name, '1')
            dao.status = Status.DATA_ENTRY
            dao.save(flush: true)
        }
        TreePath pathForRename = selectionTableTreeRowHeader.findPath(["Core", "Parameterization", "CoreParameters"] as String[])
        assertNotNull "path not found", pathForRename

        selectionTableTreeRowHeader.doExpandPath(pathForRename.parentPath)
        int row = selectionTableTreeRowHeader.getRowForPath(pathForRename)

        runSimulation(selectionTableTreeRowHeader, row)
        addCommentAndSave(mainFrameOperator)
        handleNewVersionDialog()
        assertStatus('CoreParameters', '2', Status.DATA_ENTRY)
    }

    private void handleNewVersionDialog() {
        ULCDialogOperator dialog = ULCDialogOperator.findULCDialog('Item already used', true, true)
        ULCButtonOperator okButton = new ULCButtonOperator(dialog, 'Save a new Version');
        okButton.clickMouse()
        dialog = ULCDialogOperator.findULCDialog('Add version comment', true, true)
        ULCTextAreaOperator newVersionComment = new ULCTextAreaOperator(dialog, new ComponentByNameChooser('commentTextArea'))
        newVersionComment.enterText('new Version')
        ULCButtonOperator createNewVersionButton = new ULCButtonOperator(dialog, 'Create new version');
        createNewVersionButton.clickMouse()
    }

    private void addCommentAndSave(ULCFrameOperator frame) {
        ULCTabbedPaneOperator tabs = new ULCTabbedPaneOperator(frame, new ComponentByNameChooser('DetachableTabbedPane'))
        tabs.selectPage(0)
        ULCTableTreeOperator parameterTree = getTableTreeOperatorByName('parameterTreeRowHeader')
        assert parameterTree
        parameterTree.selectCell(0, 0)
        ULCPopupMenuOperator popUpMenu = parameterTree.callPopupOnCell(0, 0)
        popUpMenu.pushMenu('Add comment')
        ULCTextAreaOperator commentContent = new ULCTextAreaOperator(frame, new ComponentByNameChooser('newCommentText'))
        commentContent.enterText('new Comment')
        ULCButtonOperator applyComment = getButtonOperator('saveNewComment')
        applyComment.clickMouse()
        parameterTree.pushKey(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)
    }

    private void runSimulation(ULCTableTreeOperator tree, int row) {
        tree.selectCell(row, 0)

        ULCPopupMenuOperator popUpMenu = tree.callPopupOnCell(row, 0)
        assertNotNull popUpMenu
        popUpMenu.pushMenu('Open')
        popUpMenu = tree.callPopupOnCell(row, 0)
        popUpMenu.pushMenu('Run simulation ...')
        ULCTextFieldOperator numberOfIterationsTextField = getTextFieldOperator('iterations')
        numberOfIterationsTextField.enterText('2')
        ULCButtonOperator runButton = getButtonOperator('SimulationActionsPane.run')
        assert runButton.enabled
        runButton.clickMouse()
    }

    private assertStatus(String parameterizationName, String version, Status expectedStatus) {
        ParameterizationDAO.withNewSession {
            ParameterizationDAO dao = ParameterizationDAO.find(parameterizationName, CoreModel.class.name, version)
            assert expectedStatus == dao.status
        }


    }
}