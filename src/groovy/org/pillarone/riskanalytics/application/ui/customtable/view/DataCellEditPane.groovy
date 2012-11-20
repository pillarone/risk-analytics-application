package org.pillarone.riskanalytics.application.ui.customtable.view

import com.ulcjava.base.application.ULCBoxPane
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableModel
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableHelper
import org.pillarone.riskanalytics.application.ui.customtable.model.DataCellElement
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.KeyfigureSelectionModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.StatisticsKeyfigure
import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import com.ulcjava.base.application.border.ULCTitledBorder
import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCFiller

/**
 *
 *
 * @author ivo.nussbaumer
 */
public class DataCellEditPane extends ULCBoxPane {
    private CustomTableView customTableView
    private CustomTableModel customTableModel

    // The dataCellElement of the cell which is currently selected
    private DataCellElement dataCellElement

    // The row and col of the cell, which the dataCellElement belongs to
    private int row = 0
    private int col = 0

    public Map<String, ULCComboBox> categoryComboBoxes = new HashMap<String, ULCComboBox>()
    private ULCTextField parameterTextField
    private ULCLabel fieldLabel = new ULCLabel()
    private ULCLabel collectorLabel = new ULCLabel()

    private KeyfigureSelectionModel model

    /**
     * Constructor
     * @param customTable the CustomTable
     */
    public DataCellEditPane (CustomTableView customTableView) {
        super (true)
        this.customTableView = customTableView
        this.customTableModel = (CustomTableModel)customTableView.customTable.getModel()
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

        dataCellElement = (DataCellElement)customTableModel.getDataAt (row, col)
        //Put simulation run name on the border ART-992
        setBorder(BorderFactory.createTitledBorder(dataCellElement.run.name))
        fieldLabel.setText("Field :" +dataCellElement.getField())
        collectorLabel.setText("Collector : " + dataCellElement.getCollector())
        model = new KeyfigureSelectionModel(dataCellElement.run)

        // remove all elements from the pane
        this.removeAll()

        initCategoryComponents()
        initStatisticsComponents()
        initPeriodComponents()
    }

    /**
     * Initialize the categories components
     */
    private void initCategoryComponents () {
        for (String category : dataCellElement.getCategoryMap().keySet()) {

            List<String> wildCardValues = dataCellElement.getWildCardPath().getWildCardValues(category)
            if (wildCardValues != null) {
                // init components
                ULCLabel categoryLabel = new ULCLabel(category)
                categoryLabel.setName(category)
                categoryLabel.setDragEnabled(true)
                categoryLabel.setToolTipText("Drag this label into the Table for inserting the values of combobox")

                ULCComboBox categoryValueCombo = new ULCComboBox(wildCardValues.toArray())
                categoryValueCombo.setName(category)
                categoryValueCombo.setEditable(true)
                categoryValueCombo.setPreferredSize(new Dimension (200,25))
                categoryValueCombo.selectedItem = dataCellElement.categoryMap[category]
                categoryValueCombo.addActionListener(new CategoryValueChangedListener())
                categoryValueCombo.setToolTipText("Select a value, or reference to a value in a cell (e.g. =A\$1")

                // add elements on pane and list
                this.add (BOX_EXPAND_TOP, categoryLabel)
                this.add (BOX_EXPAND_EXPAND, categoryValueCombo)
                categoryComboBoxes.put (category, categoryValueCombo)
            }
        }
    }

    /**
     * Initialize the statistics components
     */
    private void initStatisticsComponents () {
        // init Statistics components
        ULCLabel statisticsLabel = new ULCLabel(OutputElement.STATISTICS)
        statisticsLabel.setName(OutputElement.STATISTICS)
        statisticsLabel.setDragEnabled(true)
        statisticsLabel.setToolTipText("Drag this label into the Table for inserting the values of combobox")

        ULCComboBox statisticsCombo = new ULCComboBox(model.getKeyfigureModel())
        statisticsCombo.setName(OutputElement.STATISTICS)
        statisticsCombo.setEditable(true)
        statisticsCombo.setPreferredSize(new Dimension (200,25))
        statisticsCombo.selectedItem = dataCellElement.categoryMap[OutputElement.STATISTICS]
        statisticsCombo.addActionListener(new CategoryValueChangedListener())
        statisticsCombo.setToolTipText("Select a value, or reference to a value in a cell (e.g. =A\$1")

        parameterTextField = new ULCTextField()
        parameterTextField.setName(OutputElement.STATISTICS_PARAMETER)
        parameterTextField.setPreferredSize(new Dimension (100,25))
        parameterTextField.text = dataCellElement.categoryMap[OutputElement.STATISTICS_PARAMETER]
        parameterTextField.addActionListener(new CategoryValueChangedListener())
        parameterTextField.setToolTipText("Enter a parameter value, or reference to a value in a cell (e.g. =A\$1")

        //Add field and collector
        this.add(BOX_EXPAND_TOP, fieldLabel)
        this.add(BOX_EXPAND_TOP, collectorLabel)
        this.add(ULCFiller.createVerticalStrut(5))
        // add statistics components to pane and list
        this.add (BOX_EXPAND_TOP, statisticsLabel)
        ULCBoxPane statisticsPane = new ULCBoxPane(false)
        statisticsPane.add (BOX_EXPAND_EXPAND, statisticsCombo)
        statisticsPane.add (BOX_RIGHT_EXPAND, parameterTextField)
        this.add (BOX_EXPAND_TOP, statisticsPane)
        categoryComboBoxes.put (OutputElement.STATISTICS, statisticsCombo)

        updateParameterVisibility()
    }

    /**
     * Initialize the period components
     */
    private void initPeriodComponents () {
        // init Period components
        ULCLabel periodLabel = new ULCLabel(OutputElement.PERIOD)
        periodLabel.setName(OutputElement.PERIOD)
        periodLabel.setDragEnabled(true)
        periodLabel.setToolTipText("Drag this label into the Table for inserting the values of combobox")

        ULCComboBox periodCombo = new ULCComboBox(model.getPeriodSelectionModel())
        periodCombo.setName(OutputElement.PERIOD)
        periodCombo.setEditable(true)
        periodCombo.selectedItem = model.getPeriodLabelForIndex(Integer.parseInt(dataCellElement.categoryMap[OutputElement.PERIOD]))
        periodCombo.addActionListener(new CategoryValueChangedListener())
        periodCombo.setToolTipText("Select a value, or reference to a value in a cell (e.g. =A\$1")

        // add period components to pane and list
        this.add (BOX_EXPAND_TOP, periodLabel)
        this.add (BOX_EXPAND_EXPAND, periodCombo)
        categoryComboBoxes.put (OutputElement.PERIOD, periodCombo)
    }

    /**
     * checks if the selected statistics needs parameters, and hides/shows the parameter textField
     */
    private void updateParameterVisibility () {
        ULCComboBox statisticsCombo = categoryComboBoxes[OutputElement.STATISTICS]

        String statisticsString = statisticsCombo.selectedItem
        if (statisticsString.startsWith("=")) {
            statisticsString = customTableModel.getValueAt(statisticsString.substring (1))
        }

        StatisticsKeyfigure statistics = StatisticsKeyfigure.getEnumValue (statisticsString)

        if (statistics != null && statistics.needsParameters()) {
            statisticsCombo.setPreferredSize(new Dimension (100,25))
            parameterTextField.setVisible(true)

        } else {
            statisticsCombo.setPreferredSize(new Dimension (200,25))
            parameterTextField.setVisible(false)
        }
    }

    /**
     * Listener for the Category-ComboBoxes
     */
    private class CategoryValueChangedListener implements IActionListener {
        void actionPerformed(ActionEvent actionEvent) {

            // get the category and the new selected value
            String category = ""
            String selectedValue = ""
            if (actionEvent.source instanceof ULCTextField) {
                ULCTextField textField = (ULCTextField)actionEvent.source
                category = textField.getName()
                selectedValue = textField.text
            }
            if (actionEvent.source instanceof ULCComboBox) {
                ULCComboBox combo = (ULCComboBox)actionEvent.source
                category = combo.getName()
                selectedValue = combo.selectedItem
                if (category == OutputElement.PERIOD) {
                    selectedValue = String.valueOf(model.getPeriodIndexForLabel(selectedValue))
                }
            }

            if (selectedValue == DataCellEditPane.this.dataCellElement.categoryMap[category])
                return

            // remove reference from old variable
            if (DataCellEditPane.this.dataCellElement.categoryMap[category].startsWith("=")) {
                DataCellEditPane.this.customTableModel.removeReference(DataCellEditPane.this.dataCellElement.categoryMap[category].substring(1).replace('$', ''),
                                                                      CustomTableHelper.getVariable(DataCellEditPane.this.row, DataCellEditPane.this.col))
            }

            // save the selected item to the dataCellElement
            DataCellEditPane.this.dataCellElement.categoryMap[category] = selectedValue

            dataCellElement.update((CustomTableModel)customTableModel)
            DataCellEditPane.this.customTableView.cellEditTextField.text = dataCellElement.path

            DataCellEditPane.this.customTableModel.fireTableCellUpdated(DataCellEditPane.this.row, DataCellEditPane.this.col)

            // Update cells which are referencing on this cell
            DataCellEditPane.this.customTableModel.updateCellReferences(DataCellEditPane.this.row, DataCellEditPane.this.col)

            // add reference from new variable
            if (selectedValue.startsWith("=")) {
                DataCellEditPane.this.customTableModel.addReference(selectedValue.substring(1).replace('$', ''),
                                                                    CustomTableHelper.getVariable(DataCellEditPane.this.row, DataCellEditPane.this.col))
            }

            // update the visibility of the parameter text field
            if (category == OutputElement.STATISTICS)
                DataCellEditPane.this.updateParameterVisibility()
        }
    }
}
