package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.comment.view.ShowCommentsView
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DeleteSimulationCommentTests extends AbstractSimulationCommentTests {

    void testDelete() {
        assertEquals 1, simulation.comments.size()
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        ULCButtonOperator buttonOperator = new ULCButtonOperator(frameOperator, new ComponentByNameChooser('deleteComment'))
        assertNotNull buttonOperator
        buttonOperator.getFocus()
        buttonOperator.clickMouse()
    }


    @Override
    ULCContainer createContent() {
        CommentAndErrorView commentAndErrorView = new CommentAndErrorView(resultViewModel)
        Comment comment = new Comment("Application:test", -1)
        comment.text = "test comment"
        resultViewModel.addComment(comment)
        simulation.save()

        ShowCommentsView showCommentsView = new ShowCommentsView(commentAndErrorView, "Application:test")
        showCommentsView.addComment(comment)
        return showCommentsView.container
    }


}
