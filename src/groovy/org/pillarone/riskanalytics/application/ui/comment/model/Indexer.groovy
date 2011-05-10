package org.pillarone.riskanalytics.application.ui.comment.model

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class Indexer {
    List<Comment> comments
    StandardAnalyzer analyzer
    IndexWriter indexWriter = null;
    Directory index
    final static String SEARCH_TEXT_TITLE = "title"
    def commentsMap

    /** Creates a new instance of Indexer                 */
    public Indexer(List<Comment> comments) {
        this.comments = comments
        commentsMap = [:]
        init()
    }

    public void init() throws IOException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        analyzer = new StandardAnalyzer(Version.LUCENE_30);

        index = new RAMDirectory();
        // the boolean arg in the IndexWriter ctor means to
        // create a new index, overwriting any existing index
        indexWriter = new IndexWriter(index, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
        indexComments()
    }

    public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }

    public void indexComments() throws IOException {
        for (Comment comment: comments) {
            addCommentToDoc(indexWriter, comment)
        }
        closeIndexWriter()
    }

    private void addCommentToDoc(IndexWriter w, Comment comment) throws IOException {
        String commentIndex = String.valueOf(commentsMap.size())
        Document doc = new Document();
        StringBuilder content = new StringBuilder(comment.comment)
        comment.tags.each { content.append(" " + it + " ")}
        if (comment.files) content.append(" " + comment.files.join(" "))
        doc.add(new Field(SEARCH_TEXT_TITLE, content.toString(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("commentIndex", commentIndex, Field.Store.YES, Field.Index.NO));
        w.addDocument(doc);
        commentsMap[commentIndex] = comment
    }


}
