//IndexingExample.java

//import java.io.File;
//import java.io.FileReader;
//import java.util.Arrays;
import org.terrier.indexing.Collection;
import org.terrier.indexing.SimpleFileCollection;
import org.terrier.structures.indexing.Indexer;
import org.terrier.structures.indexing.classical.BasicIndexer;
import org.terrier.utility.ApplicationSetup;

import java.util.Collections;

//import java.util.HashMap;

public class IndexingExample {

    public static void main(String[] args) throws Exception {
        ApplicationSetup.TERRIER_HOME="D:\\Uni\\IR\\IRprogVario\\terrier-core-4.1";
        ApplicationSetup.TERRIER_ETC="D:\\Uni\\IR\\IRprogVario\\terrier-core-4.1\\etc";
        System.out.println("\nTE:"+ApplicationSetup.TERRIER_ETC);
        System.out.println("\nTH:"+ApplicationSetup.TERRIER_HOME);

        // Directory containing files to index
        String aDirectoryToIndex = "D:\\Uni\\IR\\IRprogVario\\source";

        // Configure Terrier
        ApplicationSetup.setProperty("indexer.meta.forward.keys", "filename");
        ApplicationSetup.setProperty("indexer.meta.forward.keylens", "200");

        Indexer indexer = new BasicIndexer("D:\\Uni\\IR\\IRprogVario\\index", "data");
        Collection coll = new SimpleFileCollection(Collections.singletonList(aDirectoryToIndex), true);
        indexer.index(new Collection[]{coll});
        //indexer.close();
    }
}//IndexingExample