package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import org.pillarone.riskanalytics.application.search.DocumentFactory
import org.pillarone.riskanalytics.application.ui.base.model.ModellingItemNodeFilter
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.FilterDefinition
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CheckBoxDialog extends SelectionTreeHeaderDialog {

    protected List<String> filterValues

    private List<ULCCheckBox> filterCheckBoxes
    boolean allSelected = false

    public CheckBoxDialog(ULCTableTree tree, int columnIndex, IColumnDescriptor columnDescriptor) {
        super(tree, columnIndex, columnDescriptor)
        filterValues = []
    }

    @Override
    ULCBoxPane addChoiceButton() {
        ULCBoxPane filterPane = new ULCBoxPane(2, 0)
        filterPane.setBorder BorderFactory.createTitledBorder(UIUtils.getText(SelectionTreeHeaderDialog.class, "filteredby") + ": " + getColumnName(columnIndex));
        filterCheckBoxes.each { ULCCheckBox checkBox ->
            filterPane.add(ULCBoxPane.BOX_LEFT_TOP, checkBox)
            filterPane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        }
        filterPane.add(2, ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        return filterPane
    }

    @Override
    protected void attachListeners() {
        super.attachListeners()
        applyButton.addActionListener([actionPerformed: { ActionEvent ->
            FilterDefinition filter = model.currentFilter
            filter.clearField(columnDescriptor.searchPropertyName)
            for(String val in filterValues) {
                filter.putQueryPart(columnDescriptor.searchPropertyName, val)
            }
            fireFilterChanged(filter)

            dialog.dispose()
        }] as IActionListener)
    }

    @Override
    void initFilter() {
        filterCheckBoxes = []
        ULCCheckBox allCheckBox = new ULCCheckBox(UIUtils.getText(SelectionTreeHeaderDialog.class, "all"))

        allCheckBox.addValueChangedListener([valueChanged: { ValueChangedEvent event ->
            this.@allSelected = allCheckBox.selected
            if (allCheckBox.selected) filterValues.clear()
            filterCheckBoxes.each { ULCCheckBox checkBox ->
                checkBox.setSelected(allCheckBox.selected)
            }

        }] as IValueChangedListener)

        filterCheckBoxes << allCheckBox
        List<String> values = columnDescriptor.getValues()
        List<String> activeValues = model.currentFilter.getActiveValues(columnDescriptor.searchPropertyName)
        for (String value in values) {
            ULCCheckBox box = new ULCCheckBox(String.valueOf(value))
            if(activeValues.contains(value)) {
                box.selected = true
                filterValues << value
            }
            box.addValueChangedListener([valueChanged: { ValueChangedEvent event ->
                if (box.selected)
                    filterValues << box.getText()
                else
                    filterValues.remove(box.getText())
            }] as IValueChangedListener)
            filterCheckBoxes << box
        }
    }

}
