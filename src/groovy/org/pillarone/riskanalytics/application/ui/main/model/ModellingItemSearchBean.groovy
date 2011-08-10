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
import org.pillarone.riskanalytics.core.ParameterizationDAO

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */

class ModellingItemSearchBean implements ChangeIndexerListener {

    ModellingItemIndexer indexer = null
    private QueryParser parser = null;
    private static Log LOG = LogFactory.getLog(ModellingItemSearchBean)
    static final String SEPARATOR = " +++ "
    static final String SQL_SEPARATOR = "' " + SEPARATOR + " '"
    boolean reInitIndexer = false

    public List<String> performSearch(String queryString) throws IOException, ParseException {
        initIndexer()
        List<String> result = []
        String escapedQuery = escapeQuery(queryString)
        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        QueryParser parser = new QueryParser(Version.LUCENE_30, ModellingItemIndexer.SEARCH_TEXT_TITLE, indexer.analyzer)
        parser.setAllowLeadingWildcard(true);
        Query q = parser.parse(escapedQuery);

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
        if (!indexer || reInitIndexer) {
            List<String> names = getModellingItemNames()
            indexer = new ModellingItemIndexer(names)
            parser = new QueryParser(Version.LUCENE_30, ModellingItemIndexer.SEARCH_TEXT_TITLE, indexer.analyzer)
            reInitIndexer = false
        }
    }

    private def getModellingItemNames() {
        List<String> names = []
        long currentTime = System.currentTimeMillis()
        ParameterizationDAO.withTransaction {status ->
            SessionFactory sessionFactory = ApplicationHolder.getApplication().getMainContext().getBean('sessionFactory')
            names.addAll(getParameterizationNames(sessionFactory))
            names.addAll(getResultConfigurationNames(sessionFactory, "model_class_name"))
            names.addAll(getRunNames(sessionFactory, "model"))
        }

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
        //, (select GROUP_CONCAT(t.name)  from parameterization_tag ptag, tag t
        StringBuilder sb = new StringBuilder("SELECT concat_ws(" + SQL_SEPARATOR)
        sb.append(",dao.name, (select GROUP_CONCAT(t.name)  from simulation_tag stag, tag t")
        sb.append(" where stag.simulation_run_id = dao.id and t.id = stag.tag_id) ")
        sb.append(" ,p.name, r.name ) FROM simulation_run as dao, parameterizationdao as p, result_configurationdao r ")
        sb.append(" where dao.parameterization_id = p.id and dao.result_configuration_id = r.id ")
        println " query : $sb"
//        StringBuilder sb = new StringBuilder("SELECT concat_ws(" + SQL_SEPARATOR)
        //        sb.append(",dao.name, p.name, r.name ) FROM simulation_run as dao, parameterizationdao as p, result_configurationdao r ")
        //        sb.append(" where dao.parameterization_id = p.id and dao.result_configuration_id = r.id ")
        String whereClause = clauseByModel(modelClass)
        if (whereClause) {
            sb.append(" and (" + whereClause + ")")
        }
        List<String> names = getNames(sessionFactory, sb)
        println "names : $names"
        return names
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
            if (name.indexOf(",") != -1)
                name = name.replaceAll(",", SEPARATOR)
            names << name
        }
        return names
    }

    void indexChanged() {
        reInitIndexer = true
    }

    public static String escapeQuery(String query) {
        try {
            String escapeChars = "[\\\\+\\-\\!\\(\\)\\:\\^\\]\\{\\}\\~\\\"\\'\\#]";
            query = query.replaceAll(escapeChars, " ")
            List queries = query.split()
            String escapedQuery = ""
            for (String st: queries) {
                escapedQuery += "*" + st + "* "
            }
            return escapedQuery
        } catch (Exception ex) {
            return QueryParser.escape(query)
        }

    }


}

