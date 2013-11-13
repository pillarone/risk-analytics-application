package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.testframework.operator.*

import java.awt.event.InputEvent

class AddRemoveDynamicComponentTests extends AbstractParameterFunctionalTest {

    void testAddRemove() {

        def dynamicNode = tree.getChild(tree.getRoot(), 3)
        assertEquals(1, tree.getChildCount(dynamicNode))
        assertEquals(1, model.dynamicComponent.componentList.size())

        tree.clickOnCell(4, 0, 1, InputEvent.BUTTON1_MASK)
        tree.clickOnCell(4, 0, 1, InputEvent.BUTTON3_MASK)

        ULCPopupMenuOperator popupMenu = new ULCPopupMenuOperator(frame, new ComponentByNameChooser("popup.expand"))
        ULCMenuItemOperator menuItem = new ULCMenuItemOperator(popupMenu, "Add")
        menuItem.clickMouse()

        ULCDialogOperator dialog = new ULCDialogOperator(frame)
        final ULCTextFieldOperator name = new ULCTextFieldOperator(dialog)
        name.enterText("new component")

        assertEquals(2, tree.getChildCount(dynamicNode))
        assertEquals(2, model.dynamicComponent.componentList.size())
        assertEquals("New Component", tree.getValueAt(6, 0))

        tree.clickOnCell(6, 0, 1, InputEvent.BUTTON1_MASK)
        tree.clickOnCell(6, 0, 1, InputEvent.BUTTON3_MASK)

        popupMenu = new ULCPopupMenuOperator(frame, new ComponentByNameChooser("popup.remove"))
        menuItem = new ULCMenuItemOperator(popupMenu, "Remove")
        menuItem.clickMouse()

        assertEquals(1, tree.getChildCount(dynamicNode))
        assertEquals(1, model.dynamicComponent.componentList.size())
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp() //TODO: IntegrationTestMixin does not work when this method does not exist
    }

    @Override
    protected void tearDown() {
        super.tearDown() //TODO: IntegrationTestMixin does not work when this method does not exist
    }
}
