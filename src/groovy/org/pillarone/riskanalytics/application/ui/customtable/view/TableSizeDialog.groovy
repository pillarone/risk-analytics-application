package org.pillarone.riskanalytics.application.ui.customtable.view

import com.ulcjava.base.application.ULCDialog
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.event.IFocusListener
import com.ulcjava.base.application.event.FocusEvent

/**
 *
 * @author ivo.nussbaumer
 */
class TableSizeDialog extends ULCDialog {
    private ULCTextField rowTextField
    private ULCTextField colTextField

    public TableSizeDialog (ULCFrame parent, int rows, int cols) {
        super (parent, "Set table size", true)
        init(rows, cols)
    }

    private void init(int rows, int cols) {
        ULCBoxPane pane = new ULCBoxPane(2, 3)

        ULCLabel rowLabel = new ULCLabel("Number rows: ")
        ULCLabel colLabel = new ULCLabel("Number columns: ")

        rowTextField = new ULCTextField(rows.toString())
        colTextField = new ULCTextField(cols.toString())

        rowTextField.addFocusListener(new TextFieldFocusListener())
        colTextField.addFocusListener(new TextFieldFocusListener())

        ULCButton okButton = new ULCButton("OK")
        okButton.addActionListener(new IActionListener(){
            void actionPerformed(ActionEvent actionEvent) {
                TableSizeDialog.this.dispose()
                TableSizeDialog.this.fireWindowClosing(new WindowEvent(actionEvent.source))
            }
        })

        ULCButton cancelButton = new ULCButton("Cancel")
        cancelButton.addActionListener(new IActionListener(){
            void actionPerformed(ActionEvent actionEvent) {
                TableSizeDialog.this.dispose()
                TableSizeDialog.this.fireWindowClosing(new WindowEvent(actionEvent.source))
            }
        })

        pane.add (ULCBoxPane.BOX_LEFT_EXPAND, rowLabel)
        pane.add (ULCBoxPane.BOX_EXPAND_EXPAND, rowTextField)

        pane.add (ULCBoxPane.BOX_LEFT_EXPAND, colLabel)
        pane.add (ULCBoxPane.BOX_EXPAND_EXPAND, colTextField)

        pane.add (ULCBoxPane.BOX_RIGHT_EXPAND, okButton)
        pane.add (ULCBoxPane.BOX_LEFT_EXPAND, cancelButton)

        this.contentPane = pane
    }

    public int getNumberRows() {
        int rows = Integer.parseInt (rowTextField.text)
        return rows
    }

    public int getNumberColumns() {
        int cols = Integer.parseInt (colTextField.text)
        return cols
    }

    private class TextFieldFocusListener implements IFocusListener {
        void focusGained(FocusEvent focusEvent) {
            ULCTextField textField = focusEvent.source

            if (textField != null) {
                textField.select(0, textField.text.size())
            }
        }
        void focusLost(FocusEvent focusEvent) {
        }
    }
}
