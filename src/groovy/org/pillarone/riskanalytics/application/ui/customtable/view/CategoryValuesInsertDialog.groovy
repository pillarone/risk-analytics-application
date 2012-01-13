package org.pillarone.riskanalytics.application.ui.customtable.view

import com.ulcjava.base.application.ULCDialog
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.ULCLabel
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableHelper
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.event.IFocusListener
import com.ulcjava.base.application.event.FocusEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.ULCRadioButton
import com.ulcjava.base.application.ULCButtonGroup

/**
 *
 * @author ivo.nussbaumer
 */
class CategoryValuesInsertDialog extends ULCDialog {


    private ULCTextField startCellTextField
    private ULCTextField endCellTextField
    private ULCRadioButton verticalRadioButton
    private ULCRadioButton horizontalRadioButton

    private List<String> categoryValues

    public boolean isCancel = true


    public CategoryValuesInsertDialog (ULCFrame parent, String startCell, List<String> categoryValues) {
        super (parent, "Insert category values", true)

        this.categoryValues = categoryValues
        init(startCell)
    }

    private void init(String startCell) {
        ULCBoxPane pane = new ULCBoxPane(2, 4)

        ULCButtonGroup buttonGroup = new ULCButtonGroup()
        verticalRadioButton   = new ULCRadioButton("vertical")
        verticalRadioButton.setGroup(buttonGroup)
        verticalRadioButton.setSelected(true)
        verticalRadioButton.addActionListener(new ChangeListener())

        horizontalRadioButton = new ULCRadioButton("horizontal")
        horizontalRadioButton.setGroup(buttonGroup)
        horizontalRadioButton.addActionListener(new ChangeListener())

        ULCLabel startCellLabel = new ULCLabel("First cell: ")
        ULCLabel endCellLabel = new ULCLabel("Last cell: ")

        startCellTextField = new ULCTextField(startCell)
        startCellTextField.addFocusListener(new TextFieldFocusListener())
        startCellTextField.registerKeyboardAction(new CloseActionListener(false), KeyStroke.getKeyStroke (KeyEvent.VK_ENTER, 0), ULCComponent.WHEN_FOCUSED)

        endCellTextField = new ULCTextField()
        endCellTextField.setEditable(false)
        endCellTextField.registerKeyboardAction(new CloseActionListener(false), KeyStroke.getKeyStroke (KeyEvent.VK_ENTER, 0), ULCComponent.WHEN_FOCUSED)
        updateEndCellTextField()

        ULCButton okButton = new ULCButton("OK")
        okButton.addActionListener(new CloseActionListener(false))

        ULCButton cancelButton = new ULCButton("Cancel")
        cancelButton.addActionListener(new CloseActionListener(true))

        pane.add (ULCBoxPane.BOX_LEFT_EXPAND, verticalRadioButton)
        pane.add (ULCBoxPane.BOX_EXPAND_EXPAND, horizontalRadioButton)

        pane.add (ULCBoxPane.BOX_LEFT_EXPAND, startCellLabel)
        pane.add (ULCBoxPane.BOX_EXPAND_EXPAND, startCellTextField)

        pane.add (ULCBoxPane.BOX_LEFT_EXPAND, endCellLabel)
        pane.add (ULCBoxPane.BOX_EXPAND_EXPAND, endCellTextField)

        pane.add (ULCBoxPane.BOX_RIGHT_EXPAND, okButton)
        pane.add (ULCBoxPane.BOX_LEFT_EXPAND, cancelButton)

        this.contentPane = pane
    }

    private void updateEndCellTextField () {
        int startRow = CustomTableHelper.getRow(startCellTextField.text)
        int startCol = CustomTableHelper.getCol(startCellTextField.text)

        if (verticalRadioButton.isSelected()) {
            endCellTextField.text = CustomTableHelper.getVariable(startRow + categoryValues.size(), startCol)
        } else {
            endCellTextField.text = CustomTableHelper.getVariable(startRow, startCol + categoryValues.size())
        }
    }

    public String getStartCell() {
        return startCellTextField.text
    }

    public boolean isVertical() {
        return verticalRadioButton.isSelected()
    }

    private class ChangeListener implements IActionListener {
        void actionPerformed(ActionEvent actionEvent) {
            CategoryValuesInsertDialog.this.updateEndCellTextField()
        }
    }

    private class TextFieldFocusListener implements IFocusListener {
        void focusGained(FocusEvent focusEvent) {
            ULCTextField textField = focusEvent.source

            if (textField != null) {
                textField.select(0, textField.text.size())
            }
        }
        void focusLost(FocusEvent focusEvent) {
            CategoryValuesInsertDialog.this.updateEndCellTextField()
        }
    }

    private class CloseActionListener implements IActionListener {
        private boolean isCancel
        public CloseActionListener(boolean isCancel) {
            this.isCancel = isCancel
        }

        void actionPerformed(ActionEvent actionEvent) {
            if (!isCancel)
                CategoryValuesInsertDialog.this.isCancel = false

            CategoryValuesInsertDialog.this.dispose()
            CategoryValuesInsertDialog.this.fireWindowClosing(new WindowEvent(actionEvent.source))
        }
    }
}
