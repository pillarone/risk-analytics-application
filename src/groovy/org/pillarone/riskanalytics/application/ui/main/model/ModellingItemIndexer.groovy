package org.pillarone.riskanalytics.application.ui.main.model

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ModellingItemIndexer {
    StandardAnalyzer analyzer
    IndexWriter indexWriter = null;
    Directory index
    final static String SEARCH_TEXT_TITLE = "title"
    List<String> itemNames

    public ModellingItemIndexer(List<String> itemNames) {
        this.itemNames = itemNames
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
        indexWriter.optimize();

        indexItems()
    }

    public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }

    public void indexItems() throws IOException {
        int i = 0
        for (String itemName: itemNames) {
            addItemToDoc(itemName, i++)
        }
        closeIndexWriter()
    }

    private void addItemToDoc(String itemName, int index) throws IOException {
        Document doc = new Document();
        doc.add(new Field(SEARCH_TEXT_TITLE, itemName, Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("modellingItemIndex", String.valueOf(index), Field.Store.YES, Field.Index.NO));
        indexWriter.addDocument(doc);
    }
}
