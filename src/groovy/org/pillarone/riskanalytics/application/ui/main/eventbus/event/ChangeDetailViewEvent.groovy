package org.pillarone.riskanalytics.application.ui.main.eventbus.event

import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem

class ChangeDetailViewEvent {
    ChangeDetailViewEvent(AbstractUIItem uiItem) {
        this.uiItem = uiItem
    }
    final AbstractUIItem uiItem
}
