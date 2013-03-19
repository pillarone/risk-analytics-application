package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.DefaultComboBoxModel
import org.joda.time.DateTime

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
        switch (date.monthOfYear) {
            case 1: setSelectedItem(Q4); break;
            case 4: setSelectedItem(Q1); break;
            case 7: setSelectedItem(Q2); break;
            case 10: setSelectedItem(Q3); break;
        }
    }
}