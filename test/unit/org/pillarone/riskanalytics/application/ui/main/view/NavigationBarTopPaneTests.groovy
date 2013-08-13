package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCToolBar
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import com.ulcjava.testframework.operator.ULCToggleButtonOperator
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NavigationBarTopPaneTests extends AbstractP1RATTestCase {

    public void testView() {
        ULCTextFieldOperator textField = getTextFieldOperator("searchText")
        assertNotNull textField
        textField.clearText()
        textField.typeText("test")
        assertTrue textField.getText() == "test"

        // bzetterstrom: There is no user when running in the test mode thus no MyStuff button either
//        ULCToggleButtonOperator myStuffButton = getToggleButtonOperator("myStuffButton")
//        assertNotNull myStuffButton
//        myStuffButton.getFocus()
//        myStuffButton.clickMouse()
//        assertTrue myStuffButton.isSelected()

        ULCButtonOperator clearButton = getButtonOperator("clearButton")
        assertNotNull clearButton
        clearButton.getFocus()
        clearButton.clickMouse()
        // bzetterstrom: There is no user when running in the test mode thus no MyStuff button either
       // assertFalse myStuffButton.isSelected()
        assertTrue textField.getText() != "test"
    }

    @Override
    ULCComponent createContentPane() {
        NavigationBarTopPane topPane = new NavigationBarTopPane(new ULCToolBar(), getMockTreeModel(getMockRiskAnalyticsMainModel()))
        topPane.metaClass.isStandAlone = {-> false}
        topPane.metaClass.getLoggedUser = {-> null}
        topPane.init()
        return topPane.toolBar
    }


}
