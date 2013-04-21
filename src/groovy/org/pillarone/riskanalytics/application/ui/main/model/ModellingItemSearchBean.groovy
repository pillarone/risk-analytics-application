package org.pillarone.riskanalytics.application.ui.main.model

import groovy.transform.CompileStatic
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
import org.pillarone.riskanalytics.core.util.DatabaseUtils
import org.hibernate.Hibernate

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */

@CompileStatic
class ModellingItemSearchBean implements ChangeIndexerListener {

    ModellingItemIndexer indexer = null
    boolean reInitIndexer = false

    private static Log LOG = LogFactory.getLog(ModellingItemSearchBean)

    static final String SEPARATOR = " +++ "
    static final String SQL_SEPARATOR = "' " + SEPARATOR + " '"
    static final String QUOTE = '"'
    static final List LUCENE_OPERATOR = ['AND', 'OR', '||', '~', QUOTE, '+', '-']
    static final String ESCAPE_CHARS = "[\\\\!\\(\\)\\:\\]\\{\\}\\'\\#]"


    public List<String> performSearch(String queryString) throws IOException, ParseException {
        initIndexer()
        List<String> result = []
        String escapedQuery = escapeQuery(queryString)
        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        QueryParser parser = new QueryParser(Version.LUCENE_30, ModellingItemIndexer.SEARCH_TEXT_TITLE, indexer.analyzer)
        parser.setAllowLeadingWildcard(true);
        Query q
        try {
            q = parser.parse(escapedQuery);
        }
        catch (ParseException e) {
            // remove everything except letters, numbers, spaces and underscores
            q = parser.parse(queryString.replaceAll("[^\\w\\s]",""))
        }

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
            reInitIndexer = false
        }
    }

    // todo(sku): think, how to make this more efficient in server mode? Loading all items per sessions becomes time consuming with large numbers of p14n ...
    private def getModellingItemNames() {
        List<String> names = []
        long currentTime = System.currentTimeMillis()
        ParameterizationDAO.withTransaction {status ->
            SessionFactory sessionFactory = ApplicationHolder.getApplication().getMainContext().getBean('sessionFactory') as SessionFactory
            names.addAll(getParameterizationNames(sessionFactory))
            names.addAll(getResultConfigurationNames(sessionFactory, "model_class_name"))
            names.addAll(getRunNames(sessionFactory, "model"))
        }

        LOG.info "read modellingItem names tooks ${System.currentTimeMillis() - currentTime} ms"
        return names
    }

    private List getParameterizationNames(SessionFactory sessionFactory) {
        if (DatabaseUtils.isMsSqlDatabase()) {
            String whereClause = clauseByModel("model_class_name")
            return getNames(sessionFactory,
                    "SELECT names FROM v_ModellingItemSearchBean_getParameterizationNames dao"
                            + (whereClause ? " where (${whereClause})" : ""))
        } else /* Assume MySQL database */ {
            StringBuilder sb = new StringBuilder("SELECT concat_ws( " + SQL_SEPARATOR)
            sb.append(" ,CONCAT(dao.name, ' v', dao.item_version), (select GROUP_CONCAT(t.name SEPARATOR " + SQL_SEPARATOR + ")  from parameterization_tag ptag JOIN tag t ON t.id = ptag.tag_id")
            sb.append(" where ptag.parameterizationdao_id = dao.id) ")
            sb.append(" ,(select GROUP_CONCAT(run.name SEPARATOR " + SQL_SEPARATOR + ") from simulation_run run where run.parameterization_id = dao.id)")
            sb.append(" ,(SELECT GROUP_CONCAT(DISTINCT(CONCAT(rc.name, ' v', rc.item_version)) SEPARATOR " + SQL_SEPARATOR + ") FROM result_configurationdao rc JOIN simulation_run r2 ON r2.result_configuration_id = rc.id WHERE r2.parameterization_id = dao.id)")
            sb.append(" ) FROM parameterizationdao dao ")
            String whereClause = clauseByModel("model_class_name")
            if (whereClause) {
                sb.append(" where " + whereClause)
            }
            return getNames(sessionFactory, sb.toString())
        }
    }

    private List getRunNames(SessionFactory sessionFactory, String modelClass) {
        if (DatabaseUtils.isMsSqlDatabase()) {
            String whereClause = clauseByModel(modelClass)
        return getNames(sessionFactory,
                "SELECT names FROM v_ModellingItemSearchBean_getRunNames dao"
                        + (whereClause ? " where (${whereClause})": ""))
        } else /* Assume MySQL database */ {
            StringBuilder sb = new StringBuilder("SELECT concat_ws(" + SQL_SEPARATOR)
            sb.append(", dao.name, (select GROUP_CONCAT(t.name SEPARATOR " + SQL_SEPARATOR + ")  from simulation_tag stag JOIN tag t ON t.id = stag.tag_id")
            sb.append(" where stag.simulation_run_id = dao.id) ")
            sb.append(" , CONCAT(p.name, ' v', p.item_version), r.name ) FROM simulation_run as dao, parameterizationdao as p, result_configurationdao r ")
            sb.append(" where dao.parameterization_id = p.id and dao.result_configuration_id = r.id ")
//        StringBuilder sb = new StringBuilder("SELECT concat_ws(" + SQL_SEPARATOR)
            //        sb.append(",dao.name, p.name, r.name ) FROM simulation_run as dao, parameterizationdao as p, result_configurationdao r ")
            //        sb.append(" where dao.parameterization_id = p.id and dao.result_configuration_id = r.id ")
            String whereClause = clauseByModel(modelClass)
            if (whereClause) {
                sb.append(" and (" + whereClause + ")")
            }
            List<String> names = getNames(sessionFactory, sb.toString())
            return names
        }
    }

    private List getResultConfigurationNames(SessionFactory sessionFactory, String modelClass) {
        if (DatabaseUtils.isMsSqlDatabase()) {
            String whereClause = clauseByModel(modelClass)
        return getNames(sessionFactory,
                "SELECT names FROM v_ModellingItemSearchBean_getResultConfigurationNames dao"
                        + (whereClause ? " where (${whereClause})": ""))
        } else /* Assume MySQL database */ {
            StringBuilder sb = new StringBuilder("SELECT concat_ws(" + SQL_SEPARATOR + ", CONCAT(dao.name, ' v', dao.item_version)")
            sb.append(" ,(SELECT GROUP_CONCAT(run.name SEPARATOR " + SQL_SEPARATOR + ") from simulation_run run where run.result_configuration_id = dao.id)")
            sb.append(" ,(SELECT GROUP_CONCAT(CONCAT(p.name, ' v', p.item_version) SEPARATOR " + SQL_SEPARATOR + ") FROM parameterizationdao p JOIN simulation_run run ON run.parameterization_id = p.id WHERE run.result_configuration_id = dao.id) ")
            sb.append(" ) FROM result_configurationdao as dao ")
            String whereClause = clauseByModel(modelClass)
            if (whereClause) {
                sb.append(" where (" + whereClause + ")")
            }
            return getNames(sessionFactory, sb.toString())
        }
    }


    private String clauseByModel(String modelClass) {
        StringBuilder sb = new StringBuilder("")
        List models = ApplicationHolder.application.getConfig()?.models as List
        if (models && models.size() > 0) {
            models.eachWithIndex {String modelName, int index ->
                sb.append(" dao." + modelClass + " like '%." + modelName + "'")
                if (index < models.size() - 1)
                    sb.append(" or ")
            }
        }
        return sb.toString()
    }

    private List getNames(SessionFactory sessionFactory, String sql) {
        SQLQuery query = sessionFactory.currentSession.createSQLQuery(sql)
        if (DatabaseUtils.isMsSqlDatabase()) {
            query.addScalar("names", Hibernate.STRING)
        }
        List<String> names = query.list()
        return names
    }

    void indexChanged() {
        reInitIndexer = true
    }

    /**
     * Remove ESCAPE_CHARS from query, extend tokens with leading and trailing *, treat quoted parts as single token
     * @param query
     * @return
     */
    public static String escapeQuery(String query) {
        query = query.replaceAll(ESCAPE_CHARS, " ")
        List queries = query.split() as List
        String escapedQuery = ""

        boolean openingQuotes = false
        for (String st: queries) {
            openingQuotes = openingQuotes || st[0] == QUOTE
            if (LUCENE_OPERATOR.contains(st) || openingQuotes) {
                // don't add an asterix if the token contains a lucene operator or is placed within quotes
                escapedQuery += st + ' '
                openingQuotes = !(st[-1] == QUOTE)
            }
            else {
                escapedQuery += "*" + st + "* "
            }
        }
        return escapedQuery
    }
}

