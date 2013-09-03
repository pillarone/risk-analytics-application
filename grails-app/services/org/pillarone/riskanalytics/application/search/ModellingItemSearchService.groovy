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

    static transactional = false

    ModellingItemHibernateListener modellingItemListener

    private List<ModellingItem> cache = []

    private ModellingItemListener listener = new SearchModellingItemListener()

    private final Map<ULCSession, List<ModellingItemEvent>> queue = new ConcurrentHashMap<ULCSession, List<ModellingItemEvent>>()

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

    void registerSession(ULCSession session) {
        if (queue.containsKey(session)) {
            LOG.warn("Session already registered $session")
        }
        queue.put(session, new ArrayList<ModellingItemEvent>())
    }

    void unregisterSession(ULCSession session) {
        synchronized (queue) {
            List<ModellingItemEvent> eventQueue = queue.remove(session)
            if (eventQueue == null) {
                LOG.warn("Session not found $session")
            }
        }
    }

    List<ModellingItemEvent> getPendingEvents(ULCSession session) {
        synchronized (queue) {
            List<ModellingItemEvent> result = queue.get(session)
            queue.put(session, new ArrayList<ModellingItemEvent>())
            return result
        }
    }

    protected synchronized void createInitialIndex() {
        LOG.info("start creating initial index.")
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
    }

    synchronized void refresh() {
        cache.clear()
        createInitialIndex()
    }

    synchronized List<ModellingItem> search(List<ISearchFilter> filters) {
        List<ModellingItem> results = []
        List<ModellingItem> cacheCopy = new ArrayList<ModellingItem>(cache)
        for (ModellingItem item in cacheCopy) {
            boolean match = true
            for (ISearchFilter filter in filters) {
                if (!filter.accept(item)) {
                    match = false
                }
            }
            if (match) {
                results << item
            }
        }

        return results.collect { ModellingItemFactory.getItemInstance(it) }
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
                for (List<ModellingItemEventType> list in queue.values()) {
                    list << new ModellingItemEvent(item: item, eventType: ModellingItemEventType.ADDED)
                }
            }
        }

        void modellingItemDeleted(ModellingItem item) {
            removeModellingItemFromIndex(item)
            synchronized (queue) {

                for (List<ModellingItemEventType> list in queue.values()) {
                    list << new ModellingItemEvent(item: item, eventType: ModellingItemEventType.REMOVED)
                }
            }
        }

        void modellingItemChanged(ModellingItem item) {
            synchronized (queue) {
                updateModellingItemInIndex(item)
                for (List<ModellingItemEventType> list in queue.values()) {
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
