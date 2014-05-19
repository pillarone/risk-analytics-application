package org.pillarone.riskanalytics.application.ui.search

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.ui.PollingSupport
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.ModellingItemEvent
import org.pillarone.riskanalytics.core.search.CacheItemEvent
import org.pillarone.riskanalytics.core.search.CacheItemSearchService
import org.pillarone.riskanalytics.core.search.ICacheItemEventListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource
import java.util.concurrent.CopyOnWriteArraySet

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class UlcCacheItemEventHandler {

    private final Set<ICacheItemEventListener> listeners = new CopyOnWriteArraySet<ICacheItemEventListener>()
    @Autowired
    CacheItemSearchService cacheItemSearchService
    @Resource(name = 'pollingSupport2000')
    PollingSupport pollingSupport

    private List<CacheItemEvent> queue = []
    private ICacheItemEventListener listener
    private final Object queueLock = new Object()
    private final IActionListener pollingListener = new MyPollingListener()

    @PostConstruct
    void init() {
        listener = new MyCacheItemEventListener()
        cacheItemSearchService.addItemEventListener(listener)
        pollingSupport.addActionListener(pollingListener)
    }

    @PreDestroy
    void cleanUp() {
        pollingSupport.removeActionListener(pollingListener)
        cacheItemSearchService.removeItemEventListener(listener)
        listener = null
        queue.clear()
    }

    void addItemEventListener(ICacheItemEventListener listener) {
        listeners.add(listener)
    }

    void removeItemEventListener(ICacheItemEventListener listener) {
        listeners.remove(listener)
    }

    private List<ModellingItemEvent> handleEvents() {
        List<CacheItemEvent> result
        synchronized (queueLock) {
            result = queue
            queue = []
        }
        result.each { fireItemEvent(it) }
    }

    private void fireItemEvent(CacheItemEvent event) {
        listeners.each { it.onEvent(event) }
    }

    private class MyCacheItemEventListener implements ICacheItemEventListener {
        @Override
        void onEvent(CacheItemEvent event) {
            synchronized (queueLock) {
                queue << event
            }
        }
    }

    private class MyPollingListener implements IActionListener {
        @Override
        void actionPerformed(ActionEvent event) {
            handleEvents()
        }
    }
}
