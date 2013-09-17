package org.pillarone.riskanalytics.application.ui.comment

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.ULCFrame
import models.core.CoreModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
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
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DeleteCommentTests extends AbstractSimpleFunctionalTest {

    Parameterization parameterization

    protected void doStart() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['Core'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Core'])

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
        Comment comment = new Comment("Core:exampleInputOutputComponent", 0)
        comment.text = "test"
        parameterization.addComment(comment)

        ParameterViewModel parameterViewModel = new ParameterViewModel(model, parameterization, structure)
        ParameterView parameterView = new ParameterView(parameterViewModel, new RiskAnalyticsMainModel())
        frame.setContentPane(parameterView.content)
        ULCClipboard.install()
        UIUtils.setRootPane(frame)
        frame.visible = true
    }

    void testDeleteComment() {
        assertEquals 1, parameterization.comments.size()
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        ULCTableTreeOperator componentTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeRowHeader"))


        ULCTabbedPaneOperator tabbedPaneOperator = new ULCTabbedPaneOperator(frameOperator, new ComponentByNameChooser('commentAndErrorPane'))
        assertNotNull tabbedPaneOperator
        assertEquals 1, tabbedPaneOperator.getComponentCount()


        ULCButtonOperator buttonOperator = new ULCButtonOperator(tabbedPaneOperator, new ComponentByNameChooser('deleteComment'))
        assertNotNull buttonOperator
        buttonOperator.getFocus()
        buttonOperator.clickMouse()

        assertEquals parameterization.comments.size(), 0
    }
}
