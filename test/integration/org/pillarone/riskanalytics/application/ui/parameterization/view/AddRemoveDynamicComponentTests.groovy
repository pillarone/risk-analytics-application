package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.testframework.operator.*

import java.awt.event.InputEvent

class AddRemoveDynamicComponentTests extends AbstractParameterFunctionalTest {

    void testAddRemove() {

        def dynamicNode = tree.getChild(tree.getRoot(), 2)
        assertEquals(1, tree.getChildCount(dynamicNode))
        assertEquals(1, model.dynamicComponent.componentList.size())

        tree.clickOnCell(3, 0, 1, InputEvent.BUTTON1_MASK)
        tree.clickOnCell(3, 0, 1, InputEvent.BUTTON3_MASK)

        ULCPopupMenuOperator popupMenu = new ULCPopupMenuOperator(frame, new ComponentByNameChooser("popup.expand"))
        ULCMenuItemOperator menuItem = new ULCMenuItemOperator(popupMenu, "Add")
        menuItem.clickMouse()

        ULCDialogOperator dialog = new ULCDialogOperator(frame)
        final ULCTextFieldOperator name = new ULCTextFieldOperator(dialog)
        name.enterText("new component")

        assertEquals(2, tree.getChildCount(dynamicNode))
        assertEquals(2, model.dynamicComponent.componentList.size())
        assertEquals("New Component", tree.getValueAt(5, 0))

        tree.clickOnCell(5, 0, 1, InputEvent.BUTTON1_MASK)
        tree.clickOnCell(5, 0, 1, InputEvent.BUTTON3_MASK)

        popupMenu = new ULCPopupMenuOperator(frame, new ComponentByNameChooser("popup.remove"))
        menuItem = new ULCMenuItemOperator(popupMenu, "Remove")
        menuItem.clickMouse()

        assertEquals(1, tree.getChildCount(dynamicNode))
        assertEquals(1, model.dynamicComponent.componentList.size())
    }


}
