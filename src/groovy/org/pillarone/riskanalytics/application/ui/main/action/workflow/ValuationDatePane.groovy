package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.ULCSpinner

class ValuationDatePane {

    private ULCComboBox quarter
    private ULCSpinner year

    ValuationDatePaneModel model
    ULCBoxPane content

    public ValuationDatePane(ValuationDatePaneModel model) {
        this.model = model

        initComponents()
        layoutComponents()
    }

    private void layoutComponents() {
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, quarter)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, year)
    }

    private void initComponents() {
        quarter = new ULCComboBox(model.quarterComboBoxModel)
        quarter.name = "quarter"
        year = new ULCSpinner(model.yearSpinnerModel)
        year.setEditor(new ULCSpinner.ULCNumberEditor(year, "###0000"))
        year.name = "year"

        content = new ULCBoxPane(4, 1, 50, 50)
    }
}


