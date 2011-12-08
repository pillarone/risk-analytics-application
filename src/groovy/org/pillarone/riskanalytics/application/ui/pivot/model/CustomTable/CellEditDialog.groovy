package org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable

import com.ulcjava.base.application.ULCDialog
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.DefaultComboBoxModel
import org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable.CustomTableHelper.Functions
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.event.IFocusListener
import com.ulcjava.base.application.event.FocusEvent
import com.ulcjava.base.application.ULCCheckBox
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent

class CellEditDialog extends ULCDialog {
    ULCComboBox  operatorComboBox
    ULCComboBox  functionsComboBox
    ULCTextField dataTextField = new ULCTextField()
    ULCCheckBox  selectDataCheckBox = new ULCCheckBox("")
    ULCButton    insertButton = new ULCButton("Insert")
    ULCTextField formulaTextField = new ULCTextField()
    ULCButton    okButton = new ULCButton("OK")
    boolean      selectDataMode = false

    public CellEditDialog (ULCWindow parent, CustomTableModel tableModel, int row, int col) {
        super (parent, "Edit Cell", false)

        ULCBoxPane content = new ULCBoxPane(true)
        this.add (content)

        ULCBoxPane row1 = new ULCBoxPane (false)
        ULCBoxPane row2 = new ULCBoxPane (false)
        ULCBoxPane row3 = new ULCBoxPane (false)
        content.add (ULCBoxPane.BOX_EXPAND_TOP, row1)
        content.add (ULCBoxPane.BOX_EXPAND_TOP, row2)
        content.add (ULCBoxPane.BOX_EXPAND_TOP, row3)

        operatorComboBox = new ULCComboBox ((String[])["", "+", "-", "*", "/"])

        Collection<Functions> functionsList = Functions.values()
        String[] functions = (String[])functionsList.toArray()
        functionsComboBox = new ULCComboBox(functions)

        this.addFocusListener(new IFocusListener() {
            void focusGained(FocusEvent focusEvent) {
                System.out.println ("EditDialog: Gained Focus")
            }
            void focusLost(FocusEvent focusEvent) {
                System.out.println ("EditDialog: Lost Focus")
            }
        })

        dataTextField.setPreferredSize(new Dimension (100,30))
        dataTextField.addFocusListener(new IFocusListener(){
            void focusGained(FocusEvent focusEvent) {
                System.out.println ("Data text Field: Gained Focus")
            }
            void focusLost(FocusEvent focusEvent) {
                System.out.println ("Data text Field: Lost Focus")

            }
        })

        selectDataCheckBox.addValueChangedListener(new IValueChangedListener() {
            void valueChanged(ValueChangedEvent valueChangedEvent) {
                selectDataMode = selectDataCheckBox.isSelected()
            }
        })


        row1.add (ULCBoxPane.BOX_LEFT_EXPAND, operatorComboBox)
        row1.add (ULCBoxPane.BOX_LEFT_EXPAND, functionsComboBox)
        row1.add (ULCBoxPane.BOX_EXPAND_EXPAND, dataTextField)
        row1.add (ULCBoxPane.BOX_LEFT_EXPAND, selectDataCheckBox)
        row1.add (ULCBoxPane.BOX_LEFT_EXPAND, insertButton)
        row2.add (ULCBoxPane.BOX_EXPAND_EXPAND, formulaTextField)
        row3.add (ULCBoxPane.BOX_EXPAND_EXPAND, okButton)

        formulaTextField.setText (tableModel.getDataAt (row, col).toString())

        insertButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (formulaTextField.text.startsWith("=") == false)
                    formulaTextField.text = "="

                formulaTextField.text += operatorComboBox.selectedItem +
                                         functionsComboBox.selectedItem + "(" +
                                         dataTextField.text + ")"
            }
        })

        okButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                tableModel.setValueAt (formulaTextField.getText(), row, col)
                CellEditDialog.this.setVisible(false)
            }
        })

    }
}
