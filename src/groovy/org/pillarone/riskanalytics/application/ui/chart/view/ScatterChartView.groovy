package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCCheckBox
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import org.pillarone.riskanalytics.application.ui.chart.model.ScatterChartViewModel
import org.pillarone.riskanalytics.application.ui.chart.view.ChartView

public class ScatterChartView extends ChartView {

    public ScatterChartView(ScatterChartViewModel model) {
        super(model)
    }

    protected ULCBoxPane getPeriodPane() {
        ULCBoxPane periodBox = new ULCBoxPane(model.periodCount, 1)
        model.periodCount.times {int index ->
            ULCCheckBox checkBox = new ULCCheckBox(model.getPeriodLabel(index))
            checkBox.addValueChangedListener([valueChanged: {ValueChangedEvent event -> model.setSeriesVisibility(0, index, event.source.isSelected())}] as IValueChangedListener)
            checkBox.name = "Period${index}CheckBox"
            if (index == 0) { checkBox.selected = true }
            periodBox.add(ULCBoxPane.BOX_EXPAND_EXPAND, checkBox)
        }

        periodBox.border = BorderFactory.createTitledBorder("")
        return periodBox
    }
}