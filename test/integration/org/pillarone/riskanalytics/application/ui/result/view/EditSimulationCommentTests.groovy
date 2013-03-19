package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCTextAreaOperator
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.comment.view.EditCommentView
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class EditSimulationCommentTests extends AbstractSimulationCommentTests {

    void testEdit() {
        assertEquals 1, simulation.comments.size()
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        ULCTextAreaOperator textAreaOperator = new ULCTextAreaOperator(frameOperator, new ComponentByNameChooser('newCommentText'))
        assertNotNull textAreaOperator
        textAreaOperator.clearText()
        textAreaOperator.typeText('newComment')

        ULCButtonOperator updateOperator = new ULCButtonOperator(frameOperator, new ComponentByNameChooser('updateComment'))
        assertNotNull updateOperator
        updateOperator.getFocus()
        updateOperator.clickMouse()

        assertEquals 1, simulation.comments.size()
        assertEquals simulation.comments.get(0).getText(), 'newComment'
    }

    @Override
    ULCContainer createContent() {
        CommentAndErrorView commentAndErrorView = new CommentAndErrorView(resultViewModel)
        Comment comment = new Comment("Application:test", -1)
        comment.text = "test comment"
        resultViewModel.addComment(comment)
        simulation.save()
        EditCommentView editCommentView = new EditCommentView(commentAndErrorView, comment)
        return editCommentView.content
    }


}
