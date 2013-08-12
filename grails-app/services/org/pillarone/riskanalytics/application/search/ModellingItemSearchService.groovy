package org.pillarone.riskanalytics.application.search

import com.ulcjava.base.server.ULCSession
import grails.util.Holders
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

    private static Log LOG = LogFactory.getLog(ModellingItemSearchService)

    static transactional = false

    ModellingItemHibernateListener modellingItemListener

    private List<ModellingItem> cache = []

    private ModellingItemListener listener = new SearchModellingItemListener()

    private Map<ULCSession, List<ModellingItemEvent>> queue = new ConcurrentHashMap<ULCSession, List<ModellingItemEvent>>()

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
        queue.remove(session.id)
    }

    List<ModellingItemEvent> getPendingEvents(ULCSession session) {
        List<ModellingItemEvent> result = queue.get(session)
        queue.put(session, new ArrayList<ModellingItemEvent>())
        return result
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

        for (ModellingItem item in cache) {
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

        return newInstances(results)
    }

    List<ModellingItem> newInstances(List<ModellingItem> items) {
        List<ModellingItem> results = []
        for (ModellingItem item in items){
            results << ModellingItemMapper.newItemInstance(item)
        }
        return results
    }

    protected synchronized void addModellingItemToIndex(ModellingItem modellingItem) {
        cache.add(modellingItem)
    }

    protected synchronized void removeModellingItemFromIndex(ModellingItem modellingItem) {
        cache.remove(modellingItem)
    }

    private void updateModellingItemInIndex(ModellingItem item) {
        cache[cache.indexOf(item)] = item
        internalUpdateModellingItemInIndex(item)
    }

    private void internalUpdateModellingItemInIndex(ModellingItem item) {

    }

    private void internalUpdateModellingItemInIndex(Parameterization item) {
        List<ModellingItem> allSimulations = cache.findAll { it instanceof Simulation }
        for (Simulation simulation in allSimulations) {
            if (simulation.parameterization.equals(item)) {
                simulation.parameterization = item
            }
        }
    }

    private void internalUpdateModellingItemInIndex(ResultConfiguration item) {
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
            for (List<ModellingItemEventType> list in queue.values()) {
                list << new ModellingItemEvent(item: item, eventType: ModellingItemEventType.ADDED)
            }
        }

        void modellingItemDeleted(ModellingItem item) {
            removeModellingItemFromIndex(item)
            for (List<ModellingItemEventType> list in queue.values()) {
                list << new ModellingItemEvent(item: item, eventType: ModellingItemEventType.REMOVED)
            }
        }

        void modellingItemChanged(ModellingItem item) {
            updateModellingItemInIndex(item)
            for (List<ModellingItemEventType> list in queue.values()) {
                list << new ModellingItemEvent(item: item, eventType: ModellingItemEventType.UPDATED)
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
