package org.pillarone.riskanalytics.application.ui.search

import org.pillarone.riskanalytics.core.modellingitem.CacheItem
import org.pillarone.riskanalytics.core.search.CacheItemEvent
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

class ModellingItemEvent {
    CacheItem cacheItem
    ModellingItem modellingItem
    CacheItemEvent.EventType eventType

    @Override
    String toString() {
        return "$cacheItem $eventType"
    }
}