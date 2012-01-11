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

    private CustomTableView customTableView
    private CustomTableModel customTableModel
    private int row = 0
    private int col = 0
    private OutputElement outputElement

    private Map<String, ULCTextField> cellRefTextFields = new HashMap<String, ULCTextField>()

    /**
     * Constructor
     * @param customTable the CustomTable
     */
    public DataCellEditPane (CustomTableView customTableView) {
        super (true)

        this.customTableView = customTableView
        this.customTableModel = customTableView.customTable.getModel()
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
        cellRefTextFields.clear()

        for (String category : outputElement.getCategoryMap().keySet()) {

            List<String> wildCardValues = outputElement.getWildCardPath().getWildCardValues(category)
            if (wildCardValues != null) {
                ULCLabel categoryLabel = new ULCLabel(category)

                ULCBoxPane comboTextFieldPane = new ULCBoxPane(false)

                ULCTextField cellReferenceTextField = new ULCTextField()
                cellReferenceTextField.setName(category)
                cellReferenceTextField.setVisible(false)
                cellReferenceTextField.setPreferredSize(new Dimension (100,25))
                cellReferenceTextField.addActionListener(new CellReferenceChangedListener())

                ULCComboBox categoryValueCombo = new ULCComboBox(wildCardValues.toArray())
                categoryValueCombo.addItem(cellReferenceString)
                categoryValueCombo.setName(category)
                categoryValueCombo.setPreferredSize(new Dimension (200,25))
                categoryValueCombo.addActionListener(new CategoryValueComboListener())

                String itemToSelect = outputElement.categoryMap[category]

                if (itemToSelect.startsWith('=')) {
                    cellReferenceTextField.text = itemToSelect.substring(1)
                    itemToSelect = cellReferenceString
                    cellReferenceTextField.setVisible(true)
                }

                this.add (BOX_EXPAND_TOP, categoryLabel)
                comboTextFieldPane.add (BOX_EXPAND_EXPAND, categoryValueCombo)
                comboTextFieldPane.add (BOX_RIGHT_EXPAND, cellReferenceTextField)
                this.add (BOX_EXPAND_TOP, comboTextFieldPane)

                cellRefTextFields.put (category, cellReferenceTextField)

                categoryValueCombo.selectedItem = itemToSelect
            }
        }

        refreshPath()
    }

    private void refreshPath() {
        CustomTableHelper.updateSpecificPathWithVariables((OutputElement)outputElement, (CustomTableModel)customTableModel)

        customTableView.cellEditTextField.text = outputElement.path
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
            DataCellEditPane.this.outputElement.updateValue()
            DataCellEditPane.this.customTableModel.fireTableCellUpdated(DataCellEditPane.this.row, DataCellEditPane.this.col)
        }
    }

    /**
     * Listener for the Category-ComboBoxes
     */
    private class CategoryValueComboListener implements IActionListener {
        void actionPerformed(ActionEvent comboActionEvent) {
            if (comboActionEvent.source instanceof ULCComboBox) {
                ULCComboBox combo = comboActionEvent.source
                String category = combo.getName()

                // When the selectedItem is the CellReference
                // add a TextField to the row
                if (combo.selectedItem == cellReferenceString) {
                    combo.setPreferredSize(new Dimension (100,25))
                    DataCellEditPane.this.cellRefTextFields[category].setVisible(true)

                } else {
                    combo.setPreferredSize(new Dimension (200,25))
                    DataCellEditPane.this.cellRefTextFields[category].setVisible(false)

                    if (combo.selectedItem == DataCellEditPane.this.outputElement.categoryMap[category])
                        return

                    // save the selected item to the outputElement
                    DataCellEditPane.this.outputElement.categoryMap[category] = combo.selectedItem

                    DataCellEditPane.this.refreshPath()
                    DataCellEditPane.this.outputElement.updateValue()
                    DataCellEditPane.this.customTableModel.fireTableCellUpdated(DataCellEditPane.this.row, DataCellEditPane.this.col)
                }
            }
        }
    }
}
