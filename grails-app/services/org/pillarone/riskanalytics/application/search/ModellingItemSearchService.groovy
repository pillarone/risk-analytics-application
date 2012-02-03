package org.pillarone.riskanalytics.application.search

import com.ulcjava.base.server.ULCSession
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

    protected IndexSearcher indexSearcher
    protected Directory directory

    protected Analyzer analyzer
    private ModellingItemListener listener = new SearchModellingItemListener()

    private Map<Integer, List<ModellingItemEvent>> queue = new ConcurrentHashMap<Integer, List<ModellingItemEvent>>()

    @PostConstruct
    void init() {
        createInitialIndex()
        modellingItemListener.addModellingItemListener(listener)
    }

    @PreDestroy
    void cleanUp() {
        modellingItemListener.removeModellingItemListener(listener)
        indexSearcher.close()
        directory.close()
        analyzer.close()
        queue.clear()
    }

    void registerSession(ULCSession session) {
        final int sessionId = session.id
        if (queue.containsKey(sessionId)) {
            LOG.warn("Session already registered $sessionId")
        }
        queue.put(sessionId, new ArrayList<ModellingItemEvent>())
    }

    void unregisterSession(ULCSession session) {
        queue.remove(session.id)
    }

    List<ModellingItemEvent> getPendingEvents(ULCSession session) {
        List<ModellingItemEvent> result = queue.get(session.id)
        queue.put(session.id, new ArrayList<ModellingItemEvent>())
        return result
    }

    protected synchronized void createInitialIndex() {
        analyzer = new StandardAnalyzer(Version.LUCENE_30)
        directory = new RAMDirectory()
        IndexWriter indexWriter = createIndexWriter(true)

        try {
            for (ParameterizationDAO dao in ParameterizationDAO.list()) {
                indexWriter.addDocument(org.pillarone.riskanalytics.application.search.DocumentFactory.createDocument(toParameterization(dao)))
            }
            for (ResultConfigurationDAO dao in ResultConfigurationDAO.list()) {
                indexWriter.addDocument(org.pillarone.riskanalytics.application.search.DocumentFactory.createDocument(toResultConfiguration(dao)))
            }
            for (SimulationRun dao in SimulationRun.list().findAll { ! it.toBeDeleted }) {
                indexWriter.addDocument(org.pillarone.riskanalytics.application.search.DocumentFactory.createDocument(toSimulation(dao)))
            }
        } finally {
            indexWriter.close()
        }

        indexSearcher = new IndexSearcher(directory, true)
    }

    public synchronized void refresh() {
        indexSearcher.close()
        IndexWriter indexWriter = createIndexWriter(false)

        try {
            indexWriter.deleteAll()
        } finally {
            indexWriter.close()
        }

        indexSearcher = new IndexSearcher(directory, true)
        createInitialIndex()
    }

    private Parameterization toParameterization(ParameterizationDAO dao) {
        Parameterization parameterization = new Parameterization(dao.name, getClass().getClassLoader().loadClass(dao.modelClassName))
        parameterization.versionNumber = new VersionNumber(dao.itemVersion)
        parameterization.load(false)

        return parameterization
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

        return simulation
    }

    protected IndexWriter createIndexWriter(boolean create) {
        return new IndexWriter(directory, analyzer, create, MaxFieldLength.UNLIMITED)
    }

    synchronized List<ModellingItem> search(String queryString) {
        QueryParser queryParser = createQueryParser()

        final TopDocs searchResult = indexSearcher.search(queryParser.parse(queryString), Integer.MAX_VALUE)

        List<ModellingItem> results = []

        for (ScoreDoc result in searchResult.scoreDocs) {
            results << DocumentFactory.toModellingItem(indexSearcher.doc(result.doc))
        }
        return results
    }

    protected QueryParser createQueryParser() {
        QueryParser queryParser = new QueryParser(Version.LUCENE_30, DocumentFactory.NAME_FIELD, analyzer)
        queryParser.allowLeadingWildcard = true
        queryParser.defaultOperator = Operator.AND
        return queryParser
    }

    protected synchronized void addModellingItemToIndex(ModellingItem modellingItem) {
        indexSearcher.close()
        IndexWriter indexWriter = createIndexWriter(false)

        try {
            indexWriter.addDocument(org.pillarone.riskanalytics.application.search.DocumentFactory.createDocument(modellingItem))
        } finally {
            indexWriter.close()
        }

        indexSearcher = new IndexSearcher(directory, true)
    }

    protected synchronized void removeModellingItemFromIndex(ModellingItem modellingItem) {
        indexSearcher.close()
        IndexWriter indexWriter = createIndexWriter(false)
        try {
            indexWriter.deleteDocuments(createQueryParser().parse(org.pillarone.riskanalytics.application.search.DocumentFactory.createDeleteQueryString(modellingItem)))
        } finally {
            indexWriter.close()
        }

        indexSearcher = new IndexSearcher(directory, true)
    }

    public static ModellingItemSearchService getInstance() {
        return ApplicationHolder.application.mainContext.getBean(ModellingItemSearchService)
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
