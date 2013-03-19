package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ULCContainer
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import com.ulcjava.testframework.operator.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class AddNewSimulationCommentTests extends AbstractSimulationCommentTests {

    void testAddNewComment() {
        assertEquals 0, simulation.comments.size()
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))

        ULCListOperator tags = new ULCListOperator(frameOperator, new ComponentByNameChooser('tagsList'))
        assertNotNull tags

        ULCTextAreaOperator textAreaOperator = new ULCTextAreaOperator(frameOperator, new ComponentByNameChooser('newCommentText'))
        assertNotNull textAreaOperator
        textAreaOperator.typeText('Comment')

        ULCButtonOperator buttonOperator = new ULCButtonOperator(frameOperator, new ComponentByNameChooser('saveNewComment'))
        assertNotNull buttonOperator

        buttonOperator.getFocus()
        buttonOperator.clickMouse()

        assertEquals 1, simulation.comments.size()
    }

    @Override
    ULCContainer createContent() {
        CommentAndErrorView commentAndErrorView = new CommentAndErrorView(resultViewModel)

        NewCommentView newCommentView = new NewCommentView(commentAndErrorView)
        newCommentView.init()
        return newCommentView.content
    }


}
