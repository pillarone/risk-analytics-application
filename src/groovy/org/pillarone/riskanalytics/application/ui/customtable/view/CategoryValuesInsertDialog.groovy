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
import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCCheckBox

/**
 * Dialog for the configuration of drag/drop category values
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

    /**
     * Constructor
     *
     * @param parent         parentFrame
     * @param startCell      the cell where the data was dropped to
     * @param categoryValues the List of the category values (used for calculating the lastCell)
     * @param vertical       if default inserting is vertical
     */
    public CategoryValuesInsertDialog (ULCFrame parent, String startCell, List<String> categoryValues, boolean vertical = true) {
        super (parent, "Insert category values", true)

        this.categoryValues = categoryValues
        init(startCell, vertical)
    }

    /**
     * initialize the components
     * @param startCell      the cell where the data was dropped to
     * @param vertical       if default inserting is vertical
     */
    private void init(String startCell, boolean vertical) {
        ULCBoxPane pane = new ULCBoxPane(3, 4)

        ULCButtonGroup buttonGroup = new ULCButtonGroup()
        verticalRadioButton   = new ULCRadioButton("vertical")
        verticalRadioButton.setGroup(buttonGroup)
        verticalRadioButton.setSelected(vertical)
        verticalRadioButton.addActionListener(new ChangeListener())

        horizontalRadioButton = new ULCRadioButton("horizontal")
        horizontalRadioButton.setGroup(buttonGroup)
        horizontalRadioButton.setSelected(!vertical)
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

        ULCBoxPane valuesPane = new ULCBoxPane(true)
        valuesPane.setBorder(BorderFactory.createTitledBorder("Values"))

        for (Object value : categoryValues) {
            ULCCheckBox checkBox = new ULCCheckBox(value.toString())
            checkBox.setSelected(true)
            checkBox.setName(value.toString())
            checkBox.addActionListener(new IActionListener(){
                void actionPerformed(ActionEvent actionEvent) {
                    ULCCheckBox clickedCheckBox = (ULCCheckBox)actionEvent.source
                    if (clickedCheckBox.isSelected()) {
                        CategoryValuesInsertDialog.this.categoryValues.add(clickedCheckBox.getName())
                    } else {
                        CategoryValuesInsertDialog.this.categoryValues.remove(clickedCheckBox.getName())
                    }
                    updateEndCellTextField()
                }
            })
            valuesPane.add (ULCBoxPane.BOX_EXPAND_TOP, checkBox)
        }


        pane.add (ULCBoxPane.BOX_LEFT_TOP, verticalRadioButton)
        pane.add (ULCBoxPane.BOX_EXPAND_TOP, horizontalRadioButton)
        pane.skip(1)

        pane.add (ULCBoxPane.BOX_LEFT_TOP, startCellLabel)
        pane.add (ULCBoxPane.BOX_EXPAND_TOP, startCellTextField)
        pane.skip(1)

        pane.add (ULCBoxPane.BOX_LEFT_TOP, endCellLabel)
        pane.add (ULCBoxPane.BOX_EXPAND_TOP, endCellTextField)
        pane.skip(1)

        pane.add (ULCBoxPane.BOX_RIGHT_EXPAND, okButton)
        pane.add (ULCBoxPane.BOX_LEFT_EXPAND, cancelButton)

        pane.set (2, 0, 1, 3, ULCBoxPane.BOX_EXPAND_EXPAND, valuesPane)

        this.contentPane = pane
    }

    /**
     * updates the EndCell TextField (startCell + #values
     */
    private void updateEndCellTextField () {
        int startRow = CustomTableHelper.getRow(startCellTextField.text)
        int startCol = CustomTableHelper.getCol(startCellTextField.text)

        if (verticalRadioButton.isSelected()) {
            endCellTextField.text = CustomTableHelper.getVariable(startRow + categoryValues.size()-1, startCol)
        } else {
            endCellTextField.text = CustomTableHelper.getVariable(startRow, startCol + categoryValues.size()-1)
        }
    }

    /**
     * return the starting cell the user selected
     * @return the starting cell the user selected
     */
    public String getStartCell() {
        return startCellTextField.text
    }

    /**
     * return true if the user selected vertical
     * @return true if the user selected vertical
     */
    public boolean isVertical() {
        return verticalRadioButton.isSelected()
    }

    /**
     * return the values the user selected
     * @return the selected values
     */
    public List<String> getValues() {
        return categoryValues
    }

    /**
     * Listener for the RadioButtons -> update EndCell Text Field
     */
    private class ChangeListener implements IActionListener {
        void actionPerformed(ActionEvent actionEvent) {
            CategoryValuesInsertDialog.this.updateEndCellTextField()
        }
    }

    /**
     * Listener for the StartTextField -> update EndCell Text Field
     */
    private class TextFieldFocusListener implements IFocusListener {
        void focusGained(FocusEvent focusEvent) {
            ULCTextField textField = (ULCTextField)focusEvent.source

            if (textField != null) {
                textField.select(0, textField.text.size())
            }
        }
        void focusLost(FocusEvent focusEvent) {
            CategoryValuesInsertDialog.this.updateEndCellTextField()
        }
    }

    /**
     * Close Action Listener
     */
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
