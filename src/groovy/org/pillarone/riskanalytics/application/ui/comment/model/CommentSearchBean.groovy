package org.pillarone.riskanalytics.application.ui.comment.model

import org.apache.lucene.document.Document
import org.apache.lucene.queryParser.ParseException
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TopDocs
import org.apache.lucene.util.Version
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class CommentSearchBean {
    private static final String ESCAPE_CHARS = "[\\\\!\\(\\)\\:\\]\\{\\}\\'\\#]"
    private static final String QUOTE = '"'
    private static final List LUCENE_OPERATOR = ['AND', 'OR', '||', '~', QUOTE, '+', '-']
    private Indexer indexer
    private QueryParser parser = null;
    int commentSize = 0


    public List<Comment> performSearch(String queryString) throws IOException, ParseException {
        initIndexer()
        List<Comment> result = []
        queryString = escapeQuery(queryString)
        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        QueryParser parser = new QueryParser(Version.LUCENE_30, Indexer.SEARCH_TEXT_TITLE, indexer.analyzer)
        parser.setAllowLeadingWildcard(true);
        Query q = parser.parse(queryString);

        // 3. search
        int hitsPerPage = 10;
        IndexSearcher searcher = new IndexSearcher(indexer.index);
        TopDocs rs = searcher.search(q, null, 10);

        for (int i = 0; i < rs.totalHits; ++i) {
            Document hit = searcher.doc(rs.scoreDocs[i].doc);
            result << indexer.commentsMap[hit.get("commentIndex")]
        }

        // searcher can only be closed when there
        // is no need to access the documents any more.
        searcher.close();
        return result
    }

    private void initIndexer() throws IOException {
        if (!indexer || commentSize != getComments().size()) {
            indexer = new Indexer(getComments())
            parser = new QueryParser(Version.LUCENE_30, Indexer.SEARCH_TEXT_TITLE, indexer.analyzer)
        }
    }

    abstract List<Comment> getComments()

    static CommentSearchBean getInstance(Object item) {
        switch (item.class) {
            case Parameterization: return new ParameterizationCommentSearchBean(item)
            case Simulation: return new SimulationCommentSearchBean(item)
        }
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
