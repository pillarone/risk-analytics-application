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
class RadioButtonDialog extends SelectionTreeHeaderDialog {

    private List<ULCRadioButton> filterRadioButtons

    public RadioButtonDialog(ULCTableTree tree, int columnIndex) {
        super(tree, columnIndex)
        filter = model.getFilter(columnIndex)
    }

    @Override
    ULCBoxPane addChoiceButton() {
        ULCBoxPane filterPane = new ULCBoxPane(2, 0)
        filterPane.setBorder BorderFactory.createTitledBorder(UIUtils.getText(SelectionTreeHeaderDialog.class, "filteredby") + ": " + getColumnName(columnIndex));
        filterRadioButtons.each {ULCRadioButton radioButton ->
            filterPane.add(ULCBoxPane.BOX_LEFT_TOP, radioButton)
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
                filter = new ModellingItemNodeFilter(filterValues, columnIndex)
                model.addFilter(filter)
            } else {
                filter.values = filterValues
            }
            model.applyFilter()
            dialog.dispose()
        }] as IActionListener)
    }

    @Override
    void initFilter() {
        filterRadioButtons = []
        ULCButtonGroup commentButtonGroup = new ULCButtonGroup()
        ULCRadioButton all = new ULCRadioButton(UIUtils.getText(SelectionTreeHeaderDialog.class, "all"))
        all.addValueChangedListener([valueChanged: { ValueChangedEvent event ->
            if (all.selected) {
                filterValues[0] = ModellingItemNodeFilter.ALL
            }
        }] as IValueChangedListener)
        all.setGroup(commentButtonGroup)
        filterRadioButtons << all
        ULCRadioButton with = new ULCRadioButton(UIUtils.getText(SelectionTreeHeaderDialog.class, "withComments"))
        with.addValueChangedListener([valueChanged: { ValueChangedEvent event ->
            if (with.selected)
                filterValues[0] = ModellingItemNodeFilter.WITH_COMMENTS
        }] as IValueChangedListener)
        with.setGroup(commentButtonGroup)
        filterRadioButtons << with
        ULCRadioButton without = new ULCRadioButton(UIUtils.getText(SelectionTreeHeaderDialog.class, "withoutComments"))
        without.addValueChangedListener([valueChanged: { ValueChangedEvent event ->
            if (without.selected)
                filterValues[0] = ModellingItemNodeFilter.WITHOUT_COMMENTS
        }] as IValueChangedListener)
        without.setGroup(commentButtonGroup)
        filterRadioButtons << without
        selectValues()
    }

    protected void selectValues() {
        if (filter) {
            filterRadioButtons[0].setSelected(filter.values[0] == ModellingItemNodeFilter.ALL)
            filterRadioButtons[1].setSelected(filter.values[0] == ModellingItemNodeFilter.WITH_COMMENTS)
            filterRadioButtons[2].setSelected(filter.values[0] == ModellingItemNodeFilter.WITHOUT_COMMENTS)
            filterValues = filter.values
        }
    }


}