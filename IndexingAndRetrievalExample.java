//IndexingAndRetrievalExample.java

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import org.terrier.indexing.Collection;
import org.terrier.indexing.SimpleFileCollection;
import org.terrier.matching.ResultSet;
import org.terrier.querying.Manager;
import org.terrier.querying.SearchRequest;
import org.terrier.structures.Index;
import org.terrier.structures.indexing.Indexer;
import org.terrier.structures.indexing.classical.BasicIndexer;
import org.terrier.utility.ApplicationSetup;

public class IndexingAndRetrievalExample {

    public static void main(String[] args) throws Exception {

        // Directory containing files to index
        String aDirectoryToIndex = "D:\\Uni\\IR\\IRprogVario\\text";

        // Configure Terrier
        ApplicationSetup.setProperty("indexer.meta.forward.keys", "filename");
        ApplicationSetup.setProperty("indexer.meta.forward.keylens", "200");

        Indexer indexer = new BasicIndexer("D:\\Uni\\IR\\IRprogVario\\index", "data");
        Collection coll = new SimpleFileCollection(Arrays.asList(aDirectoryToIndex), true);
        indexer.index(new Collection[]{coll});
        //indexer.close();

        Index index = Index.createIndex("D:\\Uni\\IR\\IRprogVario\\index", "data");

        // Enable the decorate enhancement
        ApplicationSetup.setProperty("querying.postfilters.order", "org.terrier.querying.SimpleDecorate");
        ApplicationSetup.setProperty("querying.postfilters.controls", "decorate:org.terrier.querying.SimpleDecorate");

        // Create a new manager run queries
        Manager queryingManager = new Manager(index);

        // Create a search request
        SearchRequest srq = queryingManager.newSearchRequestFromQuery("babele biblioteca casa");

        // Specify the model to use when searching
        srq.addMatchingModel("Matching","BM25");

        // Turn on decoration for this search request
        srq.setControl("decorate", "on");

        // Run the search
        queryingManager.runSearchRequest(srq);

        // Get the result set
        ResultSet results = srq.getResultSet();

        // Print the results
        System.out.println(results.getExactResultSize()+" documents were scored");
        System.out.println("The top "+results.getResultSize()+" of those documents were returned");
        System.out.println("Document Ranking");
        for (int i =0; i< results.getResultSize(); i++) {
            int docid = results.getDocids()[i];
            double score = results.getScores()[i];
            System.out.println("   Rank "+i+": "+docid+" "+results.getMetaItem("filename", docid)+" "+score);
        }
    }
}//IndexingAndRetrievalExample