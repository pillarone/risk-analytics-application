package org.pillarone.riskanalytics.application.ui.comment

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.ULCFrame
import java.awt.event.InputEvent
import models.core.CoreModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.ParameterView
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.output.DBCleanUpService
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import com.ulcjava.testframework.operator.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class EditCommentTests extends AbstractSimpleFunctionalTest {

    Parameterization parameterization

    protected void doStart() {
        new DBCleanUpService().cleanUp()
        new ParameterizationImportService().compareFilesAndWriteToDB(['CoreParameters'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['CoreStructure'])

        ModellingItemFactory.clear()

        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"
        CoreModel model = new CoreModel()
        model.initComponents()

        def dao = ParameterizationDAO.findByName('CoreParameters')
        parameterization = ModellingItemFactory.getParameterization(dao)
        parameterization.load()
        dao = ModelStructureDAO.findByModelClassName(model.class.name)
        ModelStructure structure = ModellingItemFactory.getModelStructure(dao)
        structure.load()
        ParameterViewModel parameterViewModel = new ParameterViewModel(model, parameterization, structure)
        ParameterView parameterView = new ParameterView(parameterViewModel)
        frame.setContentPane(parameterView.content)
        ULCClipboard.install()
        ExceptionSafe.rootPane = frame
        frame.visible = true
    }

    void testEditComment() {
        parameterization.addComment(new Comment("Core:exampleInputOutputComponent", 0))
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        ULCTableTreeOperator componentTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeRowHeader"))

        componentTree.doCollapseRow(1)
        componentTree.clickOnCell(1, 0, 1, InputEvent.BUTTON1_MASK)
        componentTree.clickOnCell(1, 0, 1, InputEvent.BUTTON3_MASK)

        ULCTabbedPaneOperator tabbedPaneOperator = new ULCTabbedPaneOperator(frameOperator, new ComponentByNameChooser('commentAndErrorPane'))
        assertNotNull tabbedPaneOperator
        assertEquals 1, tabbedPaneOperator.getComponentCount()

        ULCPopupMenuOperator popupMenuOperator = new ULCPopupMenuOperator(frameOperator, new ComponentByNameChooser("popup.expand"))
        ULCMenuItemOperator expandItem = new ULCMenuItemOperator(popupMenuOperator, "show comments")
        assertNotNull expandItem
        expandItem.clickMouse()

        assertEquals 2, tabbedPaneOperator.getComponentCount()
        assertEquals 1, tabbedPaneOperator.getSelectedIndex()

        ULCComponentOperator tabbedPaneComments = new ULCComponentOperator(frameOperator, new ComponentByNameChooser('Comments'))
        assertNotNull tabbedPaneComments

        ULCButtonOperator buttonOperator = new ULCButtonOperator(tabbedPaneOperator, new ComponentByNameChooser('editComment'))
        assertNotNull buttonOperator
        buttonOperator.getFocus()
        buttonOperator.clickMouse()

        assertEquals 3, tabbedPaneOperator.getComponentCount()
        assertEquals 2, tabbedPaneOperator.getSelectedIndex()

        ULCTextAreaOperator textAreaOperator = new ULCTextAreaOperator(frameOperator, new ComponentByNameChooser('newCommentText'))
        assertNotNull textAreaOperator
        textAreaOperator.typeText('newComment')

        ULCButtonOperator updateOperator = new ULCButtonOperator(tabbedPaneOperator, new ComponentByNameChooser('updateComment'))
        assertNotNull updateOperator
        updateOperator.getFocus()
        updateOperator.clickMouse()

        assertEquals 1, parameterization.comments.size()
        assertEquals 'newComment', parameterization.comments.get(0).text
        assertEquals 2, tabbedPaneOperator.getComponentCount()

    }
}
