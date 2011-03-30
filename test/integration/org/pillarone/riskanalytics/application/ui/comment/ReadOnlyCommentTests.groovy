package org.pillarone.riskanalytics.application.ui.comment

import java.awt.event.InputEvent
import com.ulcjava.testframework.operator.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ReadOnlyCommentTests extends CommentViewTests {


    void testShowComment() {
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

        ULCButtonOperator editButtonOperator = new ULCButtonOperator(frameOperator, new ComponentByNameChooser('editComment'))
        assertNotNull editButtonOperator
        assertFalse editButtonOperator.isEnabled()

        ULCButtonOperator deleteButtonOperator = new ULCButtonOperator(frameOperator, new ComponentByNameChooser('deleteComment'))
        assertNotNull deleteButtonOperator
        assertFalse deleteButtonOperator.isEnabled()

    }

    protected boolean isReadOnly() {
        return true
    }
}
