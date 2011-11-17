package org.pillarone.riskanalytics.application.search

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
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

class ModellingItemSearchService {

    static transactional = false

    ModellingItemHibernateListener modellingItemListener

    protected IndexSearcher indexSearcher
    protected Directory directory

    protected Analyzer analyzer
    private ModellingItemListener listener = new SearchModellingItemListener()

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
            for (SimulationRun dao in SimulationRun.list()) {
                indexWriter.addDocument(org.pillarone.riskanalytics.application.search.DocumentFactory.createDocument(toSimulation(dao)))
            }
        } finally {
            indexWriter.close()
        }

        indexSearcher = new IndexSearcher(directory, true)
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

    private class SearchModellingItemListener implements ModellingItemListener {

        void modellingItemAdded(ModellingItem item) {
            addModellingItemToIndex(item)
        }

        void modellingItemDeleted(ModellingItem item) {
            removeModellingItemFromIndex(item)
        }

        void modellingItemChanged(ModellingItem item) {
            //To change body of implemented methods use File | Settings | File Templates.
        }


    }
}
