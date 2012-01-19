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
    private DataCellElement dataCellElement

    private Map<String, ULCComponent> categoryComboBoxes = new HashMap<String, ULCComboBox>()

    private KeyfigureSelectionModel model

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

        dataCellElement = customTableModel.getDataAt (row, col)
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

                ULCComboBox categoryValueCombo = new ULCComboBox(wildCardValues.toArray())
                categoryValueCombo.setName(category)
                categoryValueCombo.setEditable(true)
                categoryValueCombo.setPreferredSize(new Dimension (200,25))
                categoryValueCombo.selectedItem = dataCellElement.categoryMap[category]
                categoryValueCombo.addActionListener(new CategoryValueComboListener())

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
        ULCLabel statisticsLabel = new ULCLabel("statistics")
        statisticsLabel.setName("statistics")
        statisticsLabel.setDragEnabled(true)

        ULCComboBox statisticsCombo = new ULCComboBox(model.getKeyfigureModel())
        statisticsCombo.setName("statistics")
        statisticsCombo.setEditable(true)
        statisticsCombo.setPreferredSize(new Dimension (200,25))
        statisticsCombo.selectedItem = dataCellElement.categoryMap[OutputElement.STATISTICS]
        statisticsCombo.addActionListener(new CategoryValueComboListener())

        ULCTextField parameterTextField = new ULCTextField()
        parameterTextField.setName("parameter")
        parameterTextField.setPreferredSize(new Dimension (100,25))
        parameterTextField.text = dataCellElement.categoryMap[OutputElement.STATISTICS_PARAMETER]
        parameterTextField.addActionListener(new CategoryValueComboListener())

        // add statistics components to pane and list
        this.add (BOX_EXPAND_TOP, statisticsLabel)
        ULCBoxPane statisticsPane = new ULCBoxPane(false)
        statisticsPane.add (BOX_EXPAND_EXPAND, statisticsCombo)
        statisticsPane.add (BOX_RIGHT_EXPAND, parameterTextField)
        this.add (BOX_EXPAND_TOP, statisticsPane)
        categoryComboBoxes.put ("statistics", statisticsCombo)
        categoryComboBoxes.put ("parameter", parameterTextField)

        updateParameterVisibility()
    }

    /**
     * Initialize the period components
     */
    private void initPeriodComponents () {
        // init Period components
        ULCLabel periodLabel = new ULCLabel("period")
        periodLabel.setName("period")
        periodLabel.setDragEnabled(true)

        ULCComboBox periodCombo = new ULCComboBox(model.getPeriodSelectionModel())
        periodCombo.setName("period")
        periodCombo.setEditable(true)
        periodCombo.selectedItem = dataCellElement.categoryMap[OutputElement.PERIOD]
        periodCombo.addActionListener(new CategoryValueComboListener())

        // add period components to pane and list
        this.add (BOX_EXPAND_TOP, periodLabel)
        this.add (BOX_EXPAND_EXPAND, periodCombo)
        categoryComboBoxes.put ("period", periodCombo)
    }

    /**
     * checks if the selected statistics needs parameters, and hides/shows the parameter textField
     */
    private void updateParameterVisibility () {
        ULCComboBox statisticsCombo = categoryComboBoxes["statistics"]
        ULCTextField parameterTextField = categoryComboBoxes["parameter"]

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
    private class CategoryValueComboListener implements IActionListener {
        void actionPerformed(ActionEvent actionEvent) {

            String category, selectedValue

            if (actionEvent.source instanceof ULCTextField) {
                ULCTextField textField = actionEvent.source
                category = textField.getName()
                selectedValue = textField.text
            }

            if (actionEvent.source instanceof ULCComboBox) {
                ULCComboBox combo = actionEvent.source
                category = combo.getName()
                selectedValue = combo.selectedItem
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
            if (category == "statistics")
                DataCellEditPane.this.updateParameterVisibility()
        }
    }
}
