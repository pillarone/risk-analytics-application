package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import org.pillarone.riskanalytics.application.ui.base.model.ModellingItemNodeFilter
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CheckBoxDialog extends SelectionTreeHeaderDialog {

    private List<ULCCheckBox> filterCheckBoxes
    boolean allSelected = false

    public CheckBoxDialog(ULCTableTree tree, int columnIndex) {
        super(tree, columnIndex)
        filter = model.getFilter(columnIndex)
    }

    @Override
    ULCBoxPane addChoiceButton() {
        ULCBoxPane filterPane = new ULCBoxPane(2, 0)
        filterPane.setBorder BorderFactory.createTitledBorder(UIUtils.getText(SelectionTreeHeaderDialog.class, "filteredby") + ": " + getColumnName(columnIndex));
        filterCheckBoxes.each {ULCCheckBox checkBox ->
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
            if (!filter) {
                filter = new ModellingItemNodeFilter(filterValues, columnIndex, allSelected)
                model.addFilter(filter)
            } else {
                filter.allSelected = allSelected
                filter.values = filterValues
            }
            model.applyFilter()
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
            filterCheckBoxes.each {ULCCheckBox checkBox ->
                checkBox.setSelected(allCheckBox.selected)
            }

        }] as IValueChangedListener)

        filterCheckBoxes << allCheckBox
        List values = tableTree.model.getValues(columnIndex)
        values.each {
            ULCCheckBox box = new ULCCheckBox(String.valueOf(it))
            box.addValueChangedListener([valueChanged: { ValueChangedEvent event ->
                if (box.selected)
                    filterValues << box.getText()
                else
                    filterValues.remove(box.getText())
            }] as IValueChangedListener)
            filterCheckBoxes << box
        }
        selectValues()
    }

    protected void selectValues() {
        if (filter) {
            filterCheckBoxes.each {ULCCheckBox checkBox ->
                def value = filter.values.find { it == checkBox.getText() }
                checkBox.setSelected(filter.allSelected || value != null)
            }
            this.@allSelected = filter.allSelected
            filterValues = filter.values
        }
    }


}
