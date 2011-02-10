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
    static final String SEPARATOR = " +++ "
    static final String SQL_SEPARATOR = "' " + SEPARATOR + " '"

    public List<String> performSearch(String queryString) throws IOException, ParseException {
        initIndexer()
        List<String> result = []
        String escapedQuery = QueryParser.escape(queryString)
        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        Query q = new QueryParser(Version.LUCENE_30, ModellingItemIndexer.SEARCH_TEXT_TITLE, indexer.analyzer).parse(escapedQuery);

        // 3. search
        int hitsPerPage = 100;
        IndexSearcher searcher = new IndexSearcher(indexer.index);
        TopDocs rs = searcher.search(q, null, hitsPerPage);

        for (int i = 0; i < rs.totalHits && i < hitsPerPage; ++i) {
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
        names.addAll(getParameterizationNames(sessionFactory))
        names.addAll(getResultConfigurationNames(sessionFactory, "model_class_name"))
        names.addAll(getRunNames(sessionFactory, "model"))
        LOG.info "read modellingItem names tooks ${System.currentTimeMillis() - currentTime} ms"
        return names
    }

    private List getParameterizationNames(SessionFactory sessionFactory) {
        StringBuilder sb = new StringBuilder("SELECT concat_ws( " + SQL_SEPARATOR)
        sb.append(" ,dao.name, (select GROUP_CONCAT(t.name)  from parameterization_tag ptag, tag t")
        sb.append(" where ptag.parameterizationdao_id = dao.id and t.id = ptag.tag_id) ")
        sb.append(" ,(select GROUP_CONCAT(run.name) from simulation_run run where run.parameterization_id = dao.id) )")
        sb.append(" FROM parameterizationdao dao ")
        String whereClause = clauseByModel("model_class_name")
        if (whereClause) {
            sb.append(" where " + whereClause)
        }
        return getNames(sessionFactory, sb)
    }

    private List getRunNames(SessionFactory sessionFactory, String modelClass) {
        //SELECT concat_ws(' /// ',run.name, p.name, r.name ) FROM simulation_run as run, parameterizationdao as p, result_configurationdao r
        // where run.parameterization_id = p.id and run.result_configuration_id = r.id
        StringBuilder sb = new StringBuilder("SELECT concat_ws(" + SQL_SEPARATOR)
        sb.append(",dao.name, p.name, r.name ) FROM simulation_run as dao, parameterizationdao as p, result_configurationdao r ")
        sb.append(" where dao.parameterization_id = p.id and dao.result_configuration_id = r.id ")
        String whereClause = clauseByModel(modelClass)
        if (whereClause) {
            sb.append(" and (" + whereClause + ")")
        }
        return getNames(sessionFactory, sb)
    }

    private List getResultConfigurationNames(SessionFactory sessionFactory, String modelClass) {
        //SELECT concat_ws(' /// ', r.name, (select group_concat(run.name) from simulation_run run
        // where run.result_configuration_id = r.id)) FROM result_configurationdao r;
        StringBuilder sb = new StringBuilder("SELECT concat_ws(" + SQL_SEPARATOR)
        sb.append(", dao.name, (select group_concat(run.name) from simulation_run run ")
        sb.append(" where run.result_configuration_id = dao.id)) FROM result_configurationdao as dao ")
        String whereClause = clauseByModel(modelClass)
        if (whereClause) {
            sb.append(" where (" + whereClause + ")")
        }
        return getNames(sessionFactory, sb)
    }



    private String clauseByModel(String modelClass) {
        StringBuilder sb = new StringBuilder("")
        List models = ApplicationHolder.application.getConfig()?.models
        if (models && models.size() > 0) {
            models.eachWithIndex {String modelName, int index ->
                sb.append(" dao." + modelClass + " like '%." + modelName + "'")
                if (index < models.size() - 1)
                    sb.append(" or ")
            }
        }
        return sb.toString()
    }

    private List getNames(SessionFactory sessionFactory, StringBuilder sb) {
        SQLQuery query = sessionFactory.currentSession.createSQLQuery(sb.toString())
        List<String> names = []
        List objects = query.list()
        for (Object name in objects) {
            names << name
        }
        return names
    }


}
