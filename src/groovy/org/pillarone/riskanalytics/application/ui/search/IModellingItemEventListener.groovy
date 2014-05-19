package org.pillarone.riskanalytics.application.ui.search

import org.pillarone.riskanalytics.application.ui.main.eventbus.event.ModellingItemEvent

interface IModellingItemEventListener {
    void onEvent(ModellingItemEvent event)
}
