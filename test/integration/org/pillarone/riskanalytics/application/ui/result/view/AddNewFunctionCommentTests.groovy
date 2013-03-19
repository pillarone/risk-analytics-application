package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ULCContainer
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.comment.view.NewFunctionCommentView
import com.ulcjava.testframework.operator.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class AddNewFunctionCommentTests extends AbstractSimulationCommentTests {

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

        assertEquals 3, simulation.comments.size()
    }

    ULCContainer createContent() {
        CommentAndErrorView commentAndErrorView = new CommentAndErrorView(resultViewModel)

        Map map0 = ["path": "Podra:path0", "periodIndex": 0, "function": "Mean"]
        Map map1 = ["path": "Podra:path1", "periodIndex": 0, "function": "Min"]
        Map map2 = ["path": "Podra:path2", "periodIndex": 0, "function": "Max"]

        NewFunctionCommentView newCommentView = new NewFunctionCommentView(commentAndErrorView, [map0, map1, map2])
        newCommentView.init()
        return newCommentView.content
    }
}
