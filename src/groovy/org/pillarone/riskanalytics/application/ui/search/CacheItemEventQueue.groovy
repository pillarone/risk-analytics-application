package org.pillarone.riskanalytics.application.ui.search

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.core.modellingitem.CacheItem
import org.pillarone.riskanalytics.core.modellingitem.CacheItemHibernateListener
import org.pillarone.riskanalytics.core.modellingitem.CacheItemListener
import org.pillarone.riskanalytics.core.search.CacheItemEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class CacheItemEventQueue {

    @Autowired
    CacheItemHibernateListener cacheItemListener
    private List<CacheItemEvent> queue
    private CacheItemListener listener
    private final Object queueLock = new Object()

    @PostConstruct
    private void init() {
        queue = new ArrayList<CacheItemEvent>()
        listener = new QueueCacheItemListener()
        cacheItemListener.addCacheItemListener(listener)
    }

    @PreDestroy
    private void cleanUp() {
        cacheItemListener.removeCacheItemListener(listener)
        queue = null
    }

    List<CacheItemEvent> pollCacheItemEvents() {
        synchronized (queueLock) {
            List<CacheItemEvent> result = new ArrayList<CacheItemEvent>(queue)
            queue.clear()
            return result
        }
    }

    private class QueueCacheItemListener implements CacheItemListener {
        void itemAdded(CacheItem item) {
            synchronized (queueLock) {
                queue << new CacheItemEvent(item: item, eventType: CacheItemEvent.EventType.ADDED)
            }
        }

        void itemDeleted(CacheItem item) {
            synchronized (queueLock) {
                queue << new CacheItemEvent(item: item, eventType: CacheItemEvent.EventType.REMOVED)
            }
        }

        void itemChanged(CacheItem item) {
            synchronized (queueLock) {
                queue << new CacheItemEvent(item: item, eventType: CacheItemEvent.EventType.UPDATED)
            }
        }
    }
}
