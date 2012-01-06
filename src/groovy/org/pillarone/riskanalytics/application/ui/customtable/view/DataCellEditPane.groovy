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

    private ULCBoxPane categoriesPane
    private ULCLabel   pathLabel

    /**
     * Constructor
     * @param customTable the CustomTable
     */
    public DataCellEditPane (CustomTable customTable) {
        super (1, 2)
        categoriesPane = new ULCBoxPane(2, 0)
        pathLabel = new ULCLabel()

        this.add (ULCBoxPane.BOX_EXPAND_EXPAND, categoriesPane)
        this.add (ULCBoxPane.BOX_EXPAND_EXPAND, pathLabel)

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

        categoriesPane.removeAll()

        categoriesPane.setRows (outputElement.getCategoryMap().size())
        int insertRow = 0
        for (String category : outputElement.getCategoryMap().keySet()) {

            List<String> wildCardValues = outputElement.getWildCardPath().getWildCardValues(category)
            if (wildCardValues != null) {
                ULCLabel categoryLabel = new ULCLabel(category)

                ULCTextField cellReferenceTextField = new ULCTextField()
                cellReferenceTextField.setName(category)
                cellReferenceTextField.setVisible(false)
                cellReferenceTextField.addActionListener(new CellReferenceChangedListener())

                ULCComboBox categoryValueCombo = new ULCComboBox(wildCardValues.toArray())
                categoryValueCombo.addItem(cellReferenceString)
                categoryValueCombo.setName(category)
                categoryValueCombo.addActionListener(new CategoyValueComboListener())

                String itemToSelect = outputElement.categoryMap[category]

                if (itemToSelect.startsWith('=')) {
                    cellReferenceTextField.text = itemToSelect.substring(1)
                    itemToSelect = cellReferenceString
                    cellReferenceTextField.setVisible(true)
                }

                categoriesPane.set (0, insertRow, BOX_LEFT_EXPAND, categoryLabel)
                categoriesPane.set (1, insertRow, 0.8, 1, BOX_EXPAND_EXPAND, categoryValueCombo)
                categoriesPane.set (2, insertRow, 0.2, 1, BOX_EXPAND_EXPAND, cellReferenceTextField)
                insertRow++

                categoryValueCombo.selectedItem = itemToSelect
            }
        }

        refreshPath()
    }

    private void refreshPath() {
        String path = CustomTableHelper.getSpecificPathWithVariables((OutputElement)outputElement, (CustomTableModel)customTableModel)

        outputElement.path = path
        pathLabel.text = path
    }

    /**
     * Listener for the Cell-Reference Textfield
     */
    private class CellReferenceChangedListener implements IActionListener {
        void actionPerformed(ActionEvent textFieldActionEvent) {
            ULCTextField textField = textFieldActionEvent.source


            if (("=" + textField.getText()) == DataCellEditPane.this.outputElement.categoryMap[textField.getName()])
                return

            DataCellEditPane.this.outputElement.categoryMap[textField.getName()] = "=" + textField.getText()

            DataCellEditPane.this.refreshPath()
            DataCellEditPane.this.customTableModel.fireTableCellUpdated(DataCellEditPane.this.row, DataCellEditPane.this.col)
        }
    }

    /**
     * Listener for the Category-ComboBoxes
     */
    private class CategoyValueComboListener implements IActionListener {
        void actionPerformed(ActionEvent comboActionEvent) {
            if (comboActionEvent.source instanceof ULCComboBox) {
                ULCComboBox combo = comboActionEvent.source


                int rowOfCombo = DataCellEditPane.this.categoriesPane.getRowOf (combo)

                // When the selectedItem is the CellReference
                // add a TextField to the row
                if (combo.selectedItem == cellReferenceString) {
                    DataCellEditPane.this.categoriesPane.getComponent(rowOfCombo*3 + 2).setVisible(true)

                } else {
                    DataCellEditPane.this.categoriesPane.getComponent(rowOfCombo*3 + 2).setVisible(false)

                    if (combo.selectedItem == DataCellEditPane.this.outputElement.categoryMap[combo.getName()])
                        return

                    // save the selected item to the outputElement
                    DataCellEditPane.this.outputElement.categoryMap[combo.getName()] = combo.selectedItem

                    DataCellEditPane.this.refreshPath()
                    DataCellEditPane.this.customTableModel.fireTableCellUpdated(DataCellEditPane.this.row, DataCellEditPane.this.col)
                }
            }
        }
    }
}
