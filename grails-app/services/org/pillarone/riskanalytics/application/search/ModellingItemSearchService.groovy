package org.pillarone.riskanalytics.application.search

import com.ulcjava.base.server.ULCSession
import grails.util.Holders
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.ResourceDAO
import org.pillarone.riskanalytics.core.modellingitem.ModellingItemMapper

import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.modellingitem.ModellingItemHibernateListener
import org.pillarone.riskanalytics.core.modellingitem.ModellingItemListener
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.*

class ModellingItemSearchService {

    private final static Log LOG = LogFactory.getLog(ModellingItemSearchService)
    private final static boolean profileCacheFiltering = System.getProperty("disableProfileCacheFiltering", "false").equalsIgnoreCase("false")

    static transactional = false

    ModellingItemHibernateListener modellingItemListener

    private List<ModellingItem> cache = []

    private ModellingItemListener listener = new SearchModellingItemListener()

    private final Map<IEventConsumer, List<ModellingItemEvent>> queue = new ConcurrentHashMap<IEventConsumer, List<ModellingItemEvent>>()

    @PostConstruct
    void init() {
        createInitialIndex()
        modellingItemListener.addModellingItemListener(listener)
    }

    @PreDestroy
    void cleanUp() {
        modellingItemListener.removeModellingItemListener(listener)
        cache.clear()
        queue.clear()
    }

    void register(IEventConsumer consumer) {
        if (queue.containsKey(consumer)) {
            LOG.warn("Consumer already registered $consumer")
        }
        queue.put(consumer, new ArrayList<ModellingItemEvent>())
    }

    void unregisterAllConsumers(ULCSession session) {
        synchronized (queue) {
            List<IEventConsumer> consumers = []
            queue.keySet().each { IEventConsumer c ->
                if (c.session == session) {
                    consumers << c
                }
            }
            consumers.each { queue.remove(it) }
        }
    }

    List<ModellingItemEvent> getPendingEvents(IEventConsumer consumer) {
        synchronized (queue) {
            List<ModellingItemEvent> result = queue.get(consumer)
            queue.put(consumer, new ArrayList<ModellingItemEvent>())
            return result
        }
    }

    protected synchronized void createInitialIndex() {
        LOG.info("start creating initial index.")

        long t;
        if( profileCacheFiltering ){
            LOG.info("-DdisableProfileCacheFiltering not set, will time search service")
            t = System.currentTimeMillis()
        }else{
            LOG.info("-DdisableProfileCacheFiltering=true, will not time search service")
        }

        ParameterizationDAO.withTransaction {
            for (ParameterizationDAO dao in ParameterizationDAO.list()) {
                cache.add(ModellingItemMapper.getModellingItem(dao))
            }
            for (ResultConfigurationDAO dao in ResultConfigurationDAO.list()) {
                cache.add(ModellingItemMapper.getModellingItem(dao))
            }

            for (SimulationRun dao in SimulationRun.list().findAll { !it.toBeDeleted }) {
                cache.add(ModellingItemMapper.getModellingItem(dao))
            }
            for (ResourceDAO dao in ResourceDAO.list()) {
                cache.add(ModellingItemMapper.getModellingItem(dao))
            }
        }
        LOG.info("end creating initial index.")
        if( profileCacheFiltering ){
            t = System.currentTimeMillis() - t
            LOG.info("Timed " + t + " ms: creating initial index");
        }
    }

    synchronized void refresh() {
        cache.clear()
        createInitialIndex()
    }

    synchronized List<ModellingItem> search(List<ISearchFilter> filters) {

        long t
        long start

        if( profileCacheFiltering ){
            start = System.currentTimeMillis()
        }

        List<ModellingItem> results = []
        List<ModellingItem> cacheCopy = new ArrayList<ModellingItem>(cache)

        if( profileCacheFiltering ){
            t = System.currentTimeMillis()
            LOG.info("Timed " + (t-start) + " ms: copying cache")
        }

        cacheCopy.each { ModellingItem item ->
            if( filters.every { it.accept(item) } ){
                results << item
            }
        }
        if( profileCacheFiltering ){
            long now = System.currentTimeMillis()
            LOG.info("Timed " + (now - t) + " ms: filtered copy")
            t = now
        }

        List<ModellingItem> ret = results.collect { ModellingItemFactory.getOrCreateModellingItem(it) }
        if( profileCacheFiltering ){
            long now = System.currentTimeMillis()
            LOG.info("Timed " + (now - t) + " ms: collecting. Total: " + (now-start)/1000 + " sec.");
        }

        return ret;
    }

    private synchronized void addModellingItemToIndex(ModellingItem modellingItem) {
        cache.add(modellingItem)
    }

    private synchronized void removeModellingItemFromIndex(ModellingItem modellingItem) {
        cache.remove(modellingItem)
    }

    private synchronized void updateModellingItemInIndex(ModellingItem item) {
        cache[cache.indexOf(item)] = item
        internalUpdateModellingItemInIndex(item)
    }

    private synchronized void internalUpdateModellingItemInIndex(ModellingItem item) {

    }

    private synchronized void internalUpdateModellingItemInIndex(Parameterization item) {
        List<ModellingItem> allSimulations = cache.findAll { it instanceof Simulation }
        for (Simulation simulation in allSimulations) {
            if (simulation.parameterization.equals(item)) {
                simulation.parameterization = item
            }
        }
    }

    private synchronized void internalUpdateModellingItemInIndex(ResultConfiguration item) {
        List<ModellingItem> allSimulations = cache.findAll { it instanceof Simulation }
        for (Simulation simulation in allSimulations) {
            if (simulation.template.equals(item)) {
                simulation.template = item
            }
        }
    }

    public static ModellingItemSearchService getInstance() {
        return Holders.grailsApplication.mainContext.getBean(ModellingItemSearchService)
    }

    private class SearchModellingItemListener implements ModellingItemListener {

        void modellingItemAdded(ModellingItem item) {
            addModellingItemToIndex(item)
            synchronized (queue) {
                for (List<ModellingItemEvent> list in queue.values()) {
                    list << new ModellingItemEvent(item: item, eventType: ModellingItemEventType.ADDED)
                }
            }
        }

        void modellingItemDeleted(ModellingItem item) {
            removeModellingItemFromIndex(item)
            synchronized (queue) {
                for (List<ModellingItemEvent> list in queue.values()) {
                    list << new ModellingItemEvent(item: item, eventType: ModellingItemEventType.REMOVED)
                }
            }
        }

        void modellingItemChanged(ModellingItem item) {
            updateModellingItemInIndex(item)
            synchronized (queue) {
                for (List<ModellingItemEvent> list in queue.values()) {
                    list << new ModellingItemEvent(item: item, eventType: ModellingItemEventType.UPDATED)
                }
            }
        }
    }

    public static class ModellingItemEvent {

        ModellingItem item
        ModellingItemEventType eventType

        @Override
        String toString() {
            return "$item $eventType"
        }


    }

    public static enum ModellingItemEventType {
        ADDED, REMOVED, UPDATED
    }
}
