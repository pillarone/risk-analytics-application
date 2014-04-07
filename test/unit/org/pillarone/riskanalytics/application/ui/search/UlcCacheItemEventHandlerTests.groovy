package org.pillarone.riskanalytics.application.ui.search

import com.ulcjava.base.application.event.IActionListener
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.application.ui.PollingSupport
import org.pillarone.riskanalytics.core.modellingitem.CacheItem
import org.pillarone.riskanalytics.core.modellingitem.CacheItemHibernateListener
import org.pillarone.riskanalytics.core.modellingitem.ResourceCacheItem
import org.pillarone.riskanalytics.core.search.CacheItemEvent
import org.pillarone.riskanalytics.core.search.CacheItemSearchService
import org.pillarone.riskanalytics.core.search.ICacheItemEventListener

import static org.pillarone.riskanalytics.core.search.CacheItemEvent.EventType.*

@TestMixin(GrailsUnitTestMixin)
class UlcCacheItemEventHandlerTests {

    UlcCacheItemEventHandler service
    TestPollingSupport testSupport = new TestPollingSupport()
    CacheItemSearchService cacheItemSearchService

    @Before
    void setUp() {
        service = new UlcCacheItemEventHandler(pollingSupport: testSupport)
        cacheItemSearchService = new CacheItemSearchService(cacheItemListener: new CacheItemHibernateListener())
        cacheItemSearchService.metaClass.createInitialIndex = {}
        cacheItemSearchService.init()
        service.cacheItemSearchService = cacheItemSearchService
        service.init()
        assert cacheItemSearchService.listeners.size() == 1

    }

    @Test
    void testAddEvent() {
        CacheItem item = createCacheItem()
        cacheItemSearchService.addModellingItemToIndex(item)
        def collector = new EventCollector()
        service.addItemEventListener(collector)
        testSupport.poll()
        List<CacheItemEvent> events = collector.events
        assert events.size() == 1
        CacheItemEvent first = events.first()
        assert first.eventType == ADDED
        assert first.item == item
    }

    @Test
    void testRemoveEvent() {
        CacheItem item = createCacheItem()
        cacheItemSearchService.removeModellingItemFromIndex(item)
        def collector = new EventCollector()
        service.addItemEventListener(collector)
        testSupport.poll()
        List<CacheItemEvent> events = collector.events
        assert events.size() == 1
        CacheItemEvent first = events.first()
        assert first.eventType == REMOVED
        assert first.item == item
    }

    @Test
    void testUpdateEvent() {
        CacheItem item = createCacheItem()
        cacheItemSearchService.updateModellingItemInIndex(item)
        def collector = new EventCollector()
        service.addItemEventListener(collector)
        testSupport.poll()
        List<CacheItemEvent> events = collector.events
        assert events.size() == 2
        CacheItemEvent event = events[0]
        assert event.eventType == ADDED
        assert event.item == item

        event = events[1]
        assert event.eventType == UPDATED
        assert event.item == item

    }

    @Test
    void testCleanup() {
        service.cleanUp()
        assert testSupport.listeners.size() == 0
        assert service.cacheItemSearchService.listeners.size() == 0
    }

    private static ResourceCacheItem createCacheItem() {
        new ResourceCacheItem(1l, 'resource', null, null, null, null, null, null, null, false, null)
    }

    private static class TestPollingSupport extends PollingSupport {
        private final List<IActionListener> listeners = []

        @Override
        void addActionListener(IActionListener listener) {
            listeners.add(listener)
        }

        @Override
        void removeActionListener(IActionListener listener) {
            listeners.remove(listener)
        }

        void poll() {
            listeners.each {
                it.actionPerformed(null)
            }
        }
    }

    private static class EventCollector implements ICacheItemEventListener {
        List<CacheItemEvent> events = []

        @Override
        void onEvent(CacheItemEvent event) {
            events << event
        }
    }
}
