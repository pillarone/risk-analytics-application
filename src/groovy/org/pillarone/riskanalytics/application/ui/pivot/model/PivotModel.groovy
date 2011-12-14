package org.pillarone.riskanalytics.application.ui.pivot.model

import org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable.CustomTableModel

class PivotModel {
    CustomTableModel customTableModel

    Random rand = new Random()

    PivotModel() {
        customTableModel = new CustomTableModel (new LinkedList<List<Object>>(), [])
    }
}
