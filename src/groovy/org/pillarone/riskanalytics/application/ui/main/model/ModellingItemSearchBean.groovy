package org.pillarone.riskanalytics.application.ui.main.model

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.lucene.document.Document
import org.apache.lucene.queryParser.ParseException
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TopDocs
import org.apache.lucene.util.Version
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.hibernate.SQLQuery
import org.hibernate.SessionFactory

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */

class ModellingItemSearchBean {

    ModellingItemIndexer indexer = null
    private QueryParser parser = null;
    private static Log LOG = LogFactory.getLog(ModellingItemSearchBean)
    static final String SEPARATOR = "TAG_SEPARTOR"
    static final String SQL_SEPARATOR = "' " + SEPARATOR + " '"

    public List<String> performSearch(String queryString) throws IOException, ParseException {
        initIndexer()
        List<String> result = []
        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        Query q = new QueryParser(Version.LUCENE_30, ModellingItemIndexer.SEARCH_TEXT_TITLE, indexer.analyzer).parse(queryString);

        // 3. search
        int hitsPerPage = 10;
        IndexSearcher searcher = new IndexSearcher(indexer.index);
        TopDocs rs = searcher.search(q, null, 10);

        for (int i = 0; i < rs.totalHits; ++i) {
            Document hit = searcher.doc(rs.scoreDocs[i].doc);
            result << hit.get(ModellingItemIndexer.SEARCH_TEXT_TITLE)
        }
        // searcher can only be closed when there
        // is no need to access the documents any more.
        searcher.close();
        return result
    }

    private void initIndexer() throws IOException {
        if (!indexer) {
            List<String> names = getModellingItemNames()
            indexer = new ModellingItemIndexer(names)
            parser = new QueryParser(Version.LUCENE_30, ModellingItemIndexer.SEARCH_TEXT_TITLE, indexer.analyzer)
        }
    }

    private def getModellingItemNames() {
        List<String> names = []
        long currentTime = System.currentTimeMillis()
        SessionFactory sessionFactory = ApplicationHolder.getApplication().getMainContext().getBean('sessionFactory')
        StringBuilder sb = new StringBuilder("SELECT concat_ws( " + SQL_SEPARATOR)
        sb.append(" ,p.name, (select GROUP_CONCAT(t.name)  from parameterization_tag ptag, tag t")
        sb.append(" where ptag.parameterizationdao_id = p.id and t.id = ptag.tag_id) ) FROM parameterizationdao p")
        SQLQuery query = sessionFactory.currentSession.createSQLQuery(sb.toString())
        List objects = query.list()
        for (Object name in objects) {
            names << name
        }
        LOG.info "read modellingItem names tooks ${System.currentTimeMillis() - currentTime} ms"
        return names
    }
}
