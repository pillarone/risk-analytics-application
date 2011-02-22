package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.DefaultComboBoxModel
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

class QuarterComboBoxModel extends DefaultComboBoxModel {

    private static final String Q1 = "Q1"
    private static final String Q2 = "Q2"
    private static final String Q3 = "Q3"
    private static final String Q4 = "Q4"

    private Map<String, DateTime> items = [:]

    public QuarterComboBoxModel() {
        items.put(Q1, new DateTime(0, 4, 1, 0, 0, 0, 0))
        items.put(Q2, new DateTime(0, 7, 1, 0, 0, 0, 0))
        items.put(Q3, new DateTime(0, 10, 1, 0, 0, 0, 0))
        items.put(Q4, new DateTime(0, 1, 1, 0, 0, 0, 0))

        items.keySet().each { addElement(it) }
    }

    DateTime getDate(int year) {
        String selectedItem = getSelectedItem()
        DateTime template = items.get(selectedItem)
        if (selectedItem == Q4) {
            year++
        }
        return new DateTime(year, template.getMonthOfYear(), template.getDayOfMonth(), 0, 0, 0, 0)

    }

    void setDate(DateTime date) {
        switch(date.monthOfYear) {
            case 1: setSelectedItem(Q4); break;
            case 4: setSelectedItem(Q1); break;
            case 7: setSelectedItem(Q2); break;
            case 10: setSelectedItem(Q3); break;
        }
    }
}
