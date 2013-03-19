package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

class PropertiesViewModel {

    private ModellingItem item

    public PropertiesViewModel(ModellingItem item) {
        this.@item = item
    }

    String getComment() {
        item.comment
    }

    void setComment(String comment) {
        item.comment = comment
        item.changed = true
    }

    public void setItem(ModellingItem item) {
        this.@item = item
    }
}