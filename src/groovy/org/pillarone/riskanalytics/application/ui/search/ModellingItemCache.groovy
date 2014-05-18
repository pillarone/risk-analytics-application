package org.pillarone.riskanalytics.application.ui.search

import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.search.CacheItemEvent
import org.pillarone.riskanalytics.core.search.ICacheItemEventListener
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class ModellingItemCache {

    private ICacheItemEventListener listener

    @Resource
    UlcCacheItemEventHandler ulcCacheItemEventHandler

    @Resource
    RiskAnalyticsMainModel riskAnalyticsMainModel

    @PostConstruct
    void initialize() {
        listener = new MyCacheItemEventListener()
        ulcCacheItemEventHandler.addItemEventListener(listener)
    }

    @PreDestroy
    void unregister() {
        ulcCacheItemEventHandler.removeItemEventListener(listener)
        listener = null
    }


    private ModellingItemEvent convert(CacheItemEvent event) {
        ModellingItem modellingItem = null
        switch (event.eventType) {
            case CacheItemEvent.EventType.REMOVED:
                modellingItem = remove(event)
                break
            case CacheItemEvent.EventType.ADDED:
            case CacheItemEvent.EventType.UPDATED:
                modellingItem = addOrUpdate(event)
                break
        }

        new ModellingItemEvent(
                cacheItem: event.item,
                modellingItem: modellingItem,
                eventType: event.eventType
        )
    }

    private ModellingItem addOrUpdate(CacheItemEvent event) {
        ModellingItemFactory.updateOrCreateModellingItem(event.item)
    }

    private ModellingItem remove(CacheItemEvent event) {
        ModellingItem modellingItem = ModellingItemFactory.getOrCreateModellingItem(event.item)
        ModellingItemFactory.remove(modellingItem)
        modellingItem
    }

    private class MyCacheItemEventListener implements ICacheItemEventListener {
        @Override
        void onEvent(CacheItemEvent event) {
            riskAnalyticsMainModel.post(convert(event))
        }
    }
}
