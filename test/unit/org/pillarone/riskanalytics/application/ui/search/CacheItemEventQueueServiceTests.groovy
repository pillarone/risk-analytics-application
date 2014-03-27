package org.pillarone.riskanalytics.application.ui.search

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.core.modellingitem.CacheItem
import org.pillarone.riskanalytics.core.modellingitem.CacheItemHibernateListener
import org.pillarone.riskanalytics.core.modellingitem.ResourceCacheItem
import org.pillarone.riskanalytics.core.search.CacheItemEvent

import static org.pillarone.riskanalytics.core.search.CacheItemEvent.EventType.*

@TestMixin(GrailsUnitTestMixin)
class CacheItemEventQueueServiceTests {

    TestHibernateListener hibernateListenerForTest
    CacheItemEventQueue service

    @Before
    void setUp() {
        service = new CacheItemEventQueue()
        hibernateListenerForTest = new TestHibernateListener()
        service.cacheItemListener = hibernateListenerForTest
        service.init()
        assert hibernateListenerForTest.listeners.size() == 1

    }

    @Test
    void testAddEvent() {
        CacheItem item = createCacheItem()
        hibernateListenerForTest.itemAdded(item)
        List<CacheItemEvent> events = service.pollCacheItemEvents()
        assert events.size() == 1
        CacheItemEvent first = events.first()
        assert first.eventType == ADDED
        assert first.item == item
    }

    @Test
    void testRemoveEvent() {
        CacheItem item = createCacheItem()
        hibernateListenerForTest.itemDeleted(item)
        List<CacheItemEvent> events = service.pollCacheItemEvents()
        assert events.size() == 1
        CacheItemEvent first = events.first()
        assert first.eventType == REMOVED
        assert first.item == item
    }

    @Test
    void testUpdateEvent() {
        CacheItem item = createCacheItem()
        hibernateListenerForTest.itemChanged(item)
        List<CacheItemEvent> events = service.pollCacheItemEvents()
        assert events.size() == 1
        CacheItemEvent first = events.first()
        assert first.eventType == UPDATED
        assert first.item == item
    }

    @Test(expected = NullPointerException)
    void testCleanup() {
        service.cleanUp()
        assert hibernateListenerForTest.listeners.size() == 0
        service.pollCacheItemEvents()
    }

    private static ResourceCacheItem createCacheItem() {
        new ResourceCacheItem(1l, 'resource', null, null, null, null, null, null, null, false, null)
    }

    private static class TestHibernateListener extends CacheItemHibernateListener {
        void itemAdded(CacheItem item) {
            listeners.each { it.itemAdded(item) }
        }

        void itemChanged(CacheItem item) {
            listeners.each { it.itemChanged(item) }
        }

        void itemDeleted(CacheItem item) {
            listeners.each { it.itemDeleted(item) }
        }
    }
}
