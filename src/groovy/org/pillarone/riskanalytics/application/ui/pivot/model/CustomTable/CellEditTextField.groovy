package org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable

import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.*
import java.util.regex.Pattern
import org.pillarone.riskanalytics.application.ui.pivot.view.CustomTable

class CellEditTextField extends ULCTextField {
    boolean          selectDataMode = false
    int              insertDataPos = 0
    CustomTableModel customTableModel
    CustomTable      customTable
    int row = 0
    int col = 0


    public CellEditTextField(CustomTableModel customTableModel, CustomTable customTable) {
        this.customTableModel = customTableModel
        this.customTable = customTable

        this.addKeyListener(new IKeyListener() {
            void keyTyped(KeyEvent keyEvent) {
                if (keyEvent.keyChar == "=" || CellEditTextField.this.text == "=") {
                    selectDataMode = true;
                }
            }
        })

        this.addFocusListener(new IFocusListener() {
            void focusGained(FocusEvent focusEvent) {
            }
            void focusLost(FocusEvent focusEvent) {
                Pattern variable_pattern = ~/[A-Z]+[0-9]+/
                Pattern variables_pattern = ~/[A-Z]+[0-9]+[A-Z0-9;]+[A-Z]+[0-9]+/
                Pattern range_pattern = ~/[A-Z]*[0-9]*:[A-Z]*[0-9]*/
                String selectedText = CellEditTextField.this.getSelectedText()

                if (selectedText ==~ variable_pattern || selectedText ==~ range_pattern || selectedText ==~ variables_pattern) {
                    selectDataMode = true;
                }
            }
        })

        this.addActionListener(new IActionListener(){
            void actionPerformed(ActionEvent actionEvent) {
                selectDataMode = false;
                customTableModel.setValueAt (CellEditTextField.this.text, CellEditTextField.this.row, CellEditTextField.this.col)

                int selectRow = CellEditTextField.this.row+1
                int selectCol = CellEditTextField.this.col

                if (selectRow >= customTable.rowCount) {
                    selectRow = 0
                    selectCol++

                    if (selectCol >= customTable.columnCount) {
                        selectCol = 0
                    }

                    customTable.getSelectionModel().setSelectionInterval(selectRow, selectRow)
                    customTable.getColumnModel().getSelectionModel().setSelectionInterval(selectCol, selectCol)
                } else {
                    customTable.getSelectionModel().setSelectionInterval(selectRow, selectRow)
                }

            }
        })
    }

    public void setText (int row, int col) {
        this.text = customTableModel.getDataAt (row, col)
        this.row = row
        this.col = col
    }

    public void insertData (String data) {
        StringBuilder sb = new StringBuilder (this.text)
        sb.delete (this.getSelectionStart(), this.getSelectionEnd())
        sb.insert (this.getSelectionStart(), data)
        this.text = sb.toString()
        this.setSelectionEnd(this.getSelectionStart() + data.size())
        this.requestFocus()
        selectDataMode = false
    }
}
