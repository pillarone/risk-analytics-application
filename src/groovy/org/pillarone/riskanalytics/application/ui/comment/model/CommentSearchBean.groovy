package org.pillarone.riskanalytics.application.ui.comment.model

import org.apache.lucene.document.Document
import org.apache.lucene.queryParser.ParseException
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TopDocs
import org.apache.lucene.util.Version
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentSearchBean {
    Parameterization parameterization
    private Indexer indexer
    private QueryParser parser = null;
    int parameterizationSize = 0

    public CommentSearchBean(Parameterization parameterization) {
        this.parameterization = parameterization
        if (this.parameterization.comments)
            parameterizationSize = this.parameterization.comments.size()
    }

    public List<Comment> performSearch(String queryString) throws IOException, ParseException {
        initIndexer()
        List<Comment> result = []
        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        Query q = new QueryParser(Version.LUCENE_30, Indexer.SEARCH_TEXT_TITLE, indexer.analyzer).parse(queryString);

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
        if (!indexer || parameterizationSize != parameterization.comments.size()) {
            indexer = new Indexer(parameterization.comments)
            parser = new QueryParser(Version.LUCENE_30, Indexer.SEARCH_TEXT_TITLE, indexer.analyzer)
        }

    }


}
