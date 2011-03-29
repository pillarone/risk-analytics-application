package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCSpinnerOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NewBatchViewTests extends AbstractP1RATTestCase {

    String batchName = "test"
    NewBatchView batchView

    @Override
    ULCComponent createContentPane() {
        batchView = new NewBatchView(null, null)
        def actionsToRemove = []
        batchView.addButton.getActionListeners().each {def action ->
            actionsToRemove << action
        }
        actionsToRemove.each {def action ->
            batchView.addButton.removeActionListener action
        }
        batchView.addButton.addActionListener([actionPerformed: {ActionEvent evt ->
            assertEquals batchName, batchView.batchNameTextField.getText()
        }] as IActionListener)

        return batchView.content
    }

    public void testView() {
        ULCTextFieldOperator textFieldOperator = getTextFieldOperator("batchNameTextField")
        textFieldOperator.clearText()
        textFieldOperator.typeText("test")
        assertNotNull textFieldOperator
        ULCSpinnerOperator spinnerOperator = getSpinnerOperator("executionTimeSpinner")
        assertNotNull spinnerOperator
        ULCButtonOperator addButton = getButtonOperator("addButton")
        assertNotNull addButton

        addButton.getFocus()
        addButton.clickMouse()

    }


}
