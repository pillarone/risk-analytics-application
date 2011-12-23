package org.pillarone.riskanalytics.application.ui.customtable.view

import com.ulcjava.base.application.ULCBoxPane
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableHelper

/**
 *
 *
 * @author ivo.nussbaumer
 */
public class DataCellEditPane extends ULCBoxPane {
    private final String cellReferenceString = "Cell-Reference"

    private CustomTable customTable
    private CustomTableModel customTableModel
    private int row = 0
    private int col = 0
    private OutputElement outputElement

    /**
     * Constructor
     * @param customTable the CustomTable
     */
    public DataCellEditPane (CustomTable customTable) {
        super (2, 0)
        this.customTable = customTable
        this.customTableModel = customTable.getModel()
    }

    /**
     * set the Data, which the pane should display
     *
     * @param row the row of the data to display
     * @param col the col of the data to display
     */
    public void setData (int row, int col) {
        this.row = row
        this.col = col

        outputElement = customTableModel.getDataAt (row, col)

        this.removeAll()

        this.setRows (outputElement.getCategoryMap().size())
        int insertRow = 0
        for (String category : outputElement.getCategoryMap().keySet()) {

            List<String> wildCardValues = outputElement.getWildCardPath().getWildCardValues(category)
            if (wildCardValues != null) {
                ULCLabel categoryLabel = new ULCLabel(category)


                ULCTextField cellReferenceTextField = new ULCTextField()
                cellReferenceTextField.setName(category)
                cellReferenceTextField.setVisible(false)
                cellReferenceTextField.addActionListener(new IActionListener() {
                    void actionPerformed(ActionEvent textFieldActionEvent) {
                        ULCTextField textField = textFieldActionEvent.source
                        DataCellEditPane.this.outputElement.categoryMap[textField.getName()] = textField.getText()
                    }
                })

                ULCComboBox categoryValueCombo = new ULCComboBox(wildCardValues.toArray())
                categoryValueCombo.addItem(cellReferenceString)
                categoryValueCombo.setName(category)
                categoryValueCombo.addActionListener(new CategoyValueComboListener())

                String itemToSelect = outputElement.categoryMap[category]

                if (itemToSelect ==~ CustomTableHelper.variable_pattern) {
                    itemToSelect = cellReferenceString
                    cellReferenceTextField.setVisible(true)
                }

                this.set (0, insertRow, BOX_LEFT_EXPAND, categoryLabel)
                this.set (1, insertRow, BOX_EXPAND_EXPAND, categoryValueCombo)
                this.set (2, insertRow, BOX_RIGHT_EXPAND, cellReferenceTextField)
                insertRow++

                categoryValueCombo.selectedItem = itemToSelect
            }
        }

    }

    /**
     * Listener for the Category-ComboBoxes
     */
    private class CategoyValueComboListener implements IActionListener {
        void actionPerformed(ActionEvent comboActionEvent) {
            if (comboActionEvent.source instanceof ULCComboBox) {
                ULCComboBox combo = comboActionEvent.source
                int rowOfCombo = DataCellEditPane.this.getRowOf (combo)

                // When the selectedItem is the CellReference
                // add a TextField to the row
                if (combo.selectedItem == cellReferenceString) {
                    DataCellEditPane.this.getComponent(rowOfCombo*3 + 2).setVisible(true)
                } else {
                    DataCellEditPane.this.getComponent(rowOfCombo*3 + 2).setVisible(false)

                    // save the selected item to the outputElement
                    DataCellEditPane.this.outputElement.categoryMap[combo.getName()] = combo.selectedItem
                }
            }
        }
    }
}
