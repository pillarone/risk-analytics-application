package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCSpinnerNumberModel
import org.joda.time.DateTime

class ValuationDatePaneModel {

    ULCSpinnerNumberModel yearSpinnerModel
    QuarterComboBoxModel quarterComboBoxModel

    public ValuationDatePaneModel() {
        yearSpinnerModel = new ULCSpinnerNumberModel(new DateTime().getYear(), 2000, 3000, 1)
        quarterComboBoxModel = new QuarterComboBoxModel()
    }

    DateTime getValuationDate() {
        return quarterComboBoxModel.getDate(yearSpinnerModel.value)
    }

    void setDate(DateTime date) {
        if (!(date.dayOfMonth == 1) && (date.monthOfYear == 1 || date.monthOfYear == 4 || date.monthOfYear == 7 || date.monthOfYear == 10)) {
            throw new IllegalStateException("Date $date is not a valid valuation date.")
        }

        yearSpinnerModel.value = date.monthOfYear == 1 ? date.year - 1 : date.year
        quarterComboBoxModel.setDate(date)
    }
}


