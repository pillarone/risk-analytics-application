package org.pillarone.riskanalytics.application.ui.comment

import java.awt.event.InputEvent
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import com.ulcjava.testframework.operator.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class AddPostLockingCommentTests extends AddNewComentTests {


    void testAddPostLockComment() {
        assertEquals 0, parameterization.comments.size()
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        ULCTableTreeOperator componentTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeRowHeader"))

        componentTree.doCollapseRow(1)
        componentTree.clickOnCell(1, 0, 1, InputEvent.BUTTON1_MASK)
        componentTree.clickOnCell(1, 0, 1, InputEvent.BUTTON3_MASK)

        ULCTabbedPaneOperator tabbedPaneOperator = new ULCTabbedPaneOperator(frameOperator, new ComponentByNameChooser('commentAndErrorPane'))
        assertNotNull tabbedPaneOperator
        assertEquals 1, tabbedPaneOperator.getComponentCount()

        ULCPopupMenuOperator popupMenuOperator = new ULCPopupMenuOperator(frameOperator, new ComponentByNameChooser("popup.expand"))
        ULCMenuItemOperator expandItem = new ULCMenuItemOperator(popupMenuOperator, "Add comment")
        assertNotNull expandItem
        expandItem.clickMouse()

        assertEquals 2, tabbedPaneOperator.getComponentCount()
        assertEquals 1, tabbedPaneOperator.getSelectedIndex()

        ULCTextAreaOperator textAreaOperator = new ULCTextAreaOperator(frameOperator, new ComponentByNameChooser('newCommentText'))
        assertNotNull textAreaOperator
        textAreaOperator.typeText('Comment')

        ULCButtonOperator buttonOperator = new ULCButtonOperator(frameOperator, new ComponentByNameChooser('saveNewComment'))
        assertNotNull buttonOperator
        buttonOperator.getFocus()
        buttonOperator.clickMouse()

        assertEquals 1, parameterization.comments.size()
        assertEquals 'Comment', parameterization.comments.get(0).text
        assertTrue parameterization.comments.get(0).tags.any { Tag tag -> tag.name == NewCommentView.POST_LOCKING}

    }

    protected boolean isReadOnly() {
        return true
    }


}
