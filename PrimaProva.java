//PrimaProva.java

import org.terrier.indexing.Collection;
import org.terrier.indexing.SimpleFileCollection;
import org.terrier.matching.ResultSet;
import org.terrier.querying.Manager;
import org.terrier.querying.SearchRequest;
import org.terrier.structures.Index;
import org.terrier.structures.indexing.Indexer;
import org.terrier.structures.indexing.classical.BasicIndexer;
import org.terrier.utility.ApplicationSetup;

import java.util.Arrays;


public class PrimaProva {

    public static void main(String[] args) throws Exception {
        ApplicationSetup.TERRIER_HOME="D:\\Uni\\IR\\IRprogVario\\terrier-core-4.1";
        ApplicationSetup.TERRIER_ETC="D:\\Uni\\IR\\IRprogVario\\terrier-core-4.1\\etc";
        System.out.println("\nTE:"+ApplicationSetup.TERRIER_ETC);
        System.out.println("\nTH:"+ApplicationSetup.TERRIER_HOME);

        // Directory containing files to index
        String aDirectoryToIndex = "D:\\Uni\\IR\\IRprogVario\\source";

        // Configure Terrier (come in file properties)
        ApplicationSetup.setProperty("querying.postprocesses.order", "QueryExpansion");
        ApplicationSetup.setProperty("querying.postprocesses.controls", "qe:QueryExpansion");

        ApplicationSetup.setProperty("querying.postfilters.order", "SimpleDecorate,SiteFilter,Scope");
        ApplicationSetup.setProperty("querying.postfilters.controls", "decorate:SimpleDecorate,site:SiteFilter,scope:Scope");

        ApplicationSetup.setProperty("querying.default.controls","");
        ApplicationSetup.setProperty("querying.allowed.controls","scope,qe,qemodel,start,end,site,scope");

        ApplicationSetup.setProperty("tokeniser","EnglishTokeniser");
        ApplicationSetup.setProperty("trec.encoding","UTF-8");

        ApplicationSetup.setProperty("trec.collection.class","SimpleFileCollection");
        ApplicationSetup.setProperty("trec.document.class","FileDocument");

        ApplicationSetup.setProperty("indexer.meta.forward.keys", "filename");
        ApplicationSetup.setProperty("indexer.meta.forward.keylens", "512");

        ApplicationSetup.setProperty("indexing.simplefilecollection.recurse","true");

        ApplicationSetup.setProperty("stopwords.filename","stopword-list.txt");

        ApplicationSetup.setProperty("termpipelines","Stopwords,PorterStemmer");

        Indexer indexer = new BasicIndexer("D:\\Uni\\IR\\IRprogVario\\index", "data");
        Collection coll = new SimpleFileCollection(Arrays.asList(aDirectoryToIndex), true);
        indexer.index(new Collection[]{coll});
        //indexer.close();

        Index index = Index.createIndex("D:\\Uni\\IR\\IRprogVario\\index", "data");

        // Enable the decorate enhancement
        ApplicationSetup.setProperty("querying.postfilters.order", "org.terrier.querying.SimpleDecorate");
        ApplicationSetup.setProperty("querying.postfilters.controls", "decorate:org.terrier.querying.SimpleDecorate");

        //ApplicationSetup.setProperty("trec.collection.class","TRECCollection");
        //ApplicationSetup.setProperty("trec.document.class","TRECDocument");

        // Create a new manager run queries
        Manager queryingManager = new Manager(index);

        // Create a search request
        SearchRequest srq = queryingManager.newSearchRequestFromQuery("africa durban");

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

    }//main

}//PrimaProva
