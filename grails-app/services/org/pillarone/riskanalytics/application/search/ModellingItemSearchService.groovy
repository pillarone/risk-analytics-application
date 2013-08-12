package org.pillarone.riskanalytics.application.search

import com.ulcjava.base.server.ULCSession
import grails.util.Holders
import org.apache.lucene.index.Term
import org.pillarone.riskanalytics.core.ResourceDAO

import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriter.MaxFieldLength
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.queryParser.QueryParser.Operator
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.listener.ModellingItemHibernateListener
import org.pillarone.riskanalytics.core.listener.ModellingItemListener
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.*
import org.codehaus.groovy.grails.commons.ApplicationHolder

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
        ParameterizationDAO.withTransaction {
            for (ParameterizationDAO dao in ParameterizationDAO.list()) {
                cache.add(toParameterization(dao))
            }
            for (ResultConfigurationDAO dao in ResultConfigurationDAO.list()) {
                cache.add(toResultConfiguration(dao))
            }

            for (SimulationRun dao in SimulationRun.list().findAll { !it.toBeDeleted }) {
                cache.add(toSimulation(dao))
            }
            for (ResourceDAO dao in ResourceDAO.list()) {
                cache.add(toResource(dao))
            }
        }
    }

    public synchronized void refresh() {
        cache.clear()

        createInitialIndex()
    }

    private Parameterization toParameterization(ParameterizationDAO dao) {
        Parameterization parameterization = new Parameterization(dao.name, getClass().getClassLoader().loadClass(dao.modelClassName))
        parameterization.versionNumber = new VersionNumber(dao.itemVersion)
        parameterization.load(false)
        if (dao.tags*.tag) {
            parameterization.tags = dao.tags*.tag
        }
        return parameterization
    }

    private Resource toResource(ResourceDAO dao) {
        Resource resource = new Resource(dao.name, getClass().getClassLoader().loadClass(dao.resourceClassName))
        resource.versionNumber = new VersionNumber(dao.itemVersion)
        resource.load(false)
        if (dao.tags*.tag) {
            resource.tags = dao.tags*.tag
        }

        return resource
    }

    private ResultConfiguration toResultConfiguration(ResultConfigurationDAO dao) {
        ResultConfiguration resultConfiguration = new ResultConfiguration(dao.name)
        resultConfiguration.modelClass = getClass().getClassLoader().loadClass(dao.modelClassName)
        resultConfiguration.versionNumber = new VersionNumber(dao.itemVersion)
        resultConfiguration.load(false)

        return resultConfiguration
    }

    private Simulation toSimulation(SimulationRun dao) {
        Simulation simulation = new Simulation(dao.name)
        simulation.modelClass = getClass().getClassLoader().loadClass(dao.model)
        simulation.load(false)
        if (dao.tags*.tag) {
            simulation.tags = dao.tags*.tag
        }

        return simulation
    }

    synchronized List<ModellingItem> search(List<ISearchFilter> filters) {
        List<ModellingItem> results = []

        for(ModellingItem item in cache) {
            boolean match = true
            for(ISearchFilter filter in filters) {
                if(!filter.accept(item)) {
                    match = false
                }
            }
            if(match) {
                results << item
            }
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
