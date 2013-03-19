package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.application.search.ModellingItemSearchService
import com.ulcjava.base.application.ULCPollingTimer
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.search.ModellingItemSearchService.ModellingItemEvent
import com.ulcjava.base.server.ULCSession


class NavigationTreeFactory {

    ModellingItemSearchService searchService

    private ULCPollingTimer pollingTimer

    NavigationTreeFactory() {
        searchService = ModellingItemSearchService.getInstance()
        pollingTimer = new ULCPollingTimer(5000, new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                for(ModellingItemEvent event in searchService.getPendingEvents(ULCSession.currentSession())) {
                    println(event.toString())
                }
            }

        })
//        pollingTimer.start()
    }
}

