package org.pillarone.riskanalytics.application.ui.main.eventbus.event

import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem

class CloseDetailViewEvent {
    CloseDetailViewEvent(AbstractUIItem uiItem) {
        this.uiItem = uiItem
    }
    final AbstractUIItem uiItem
}
