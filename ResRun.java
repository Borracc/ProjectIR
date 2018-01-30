//ResRun.java

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class ResRun{

    public static RunData[] leggiRun(String pathFile){

        RunData[] run1=new RunData[47396];
        int index=0;

        // definiamo il percorso al file da leggere
        File doc=new File(pathFile);
        URL path=null;

        // creaiamo un blocco try-catch per intercettare le eccezioni
        try{
            // mostriamo il percorso al file
            path=doc.toURL();
            //System.out.println("Il doc si trova nel percorso" + path);

            //mostriamo il nome del file
            doc=new File(path.getFile());
            System.out.println("Nome del file " + doc);
            String s;

            // apriamo lo stream di input...
            InputStream is=path.openStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(is));

            // ...e avviamo la lettura del file
            do{
                s=br.readLine();
                if(s!=null){
                    run1[index]=new RunData(s);
                    index++;
                }//if
            }while (s!=null);
            is.close();
        }catch (MalformedURLException e){
            System.out.println("Attenzione:" + e);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }//try-catch

        RunData[] run2=new RunData[index];
        for(int i=0;i<index;i++){
            run2[i]=run1[i];
        }//for
        return run2;
    }//leggiRun

    public static RunDataNorm[] leggiRunNorm(String pathFile){

        RunData[] run= leggiRun(pathFile);

        double[] scoreMin = new double[50];
        double[] scoreMax = new double[50];
        for(int i=0; i<50; i++){
            scoreMin[i]=Double.POSITIVE_INFINITY;
            scoreMax[i]=Double.NEGATIVE_INFINITY;
        }//for

        int j=0;
        for(int i=0; i<50; i++){
            while (j<run.length) {
                //System.out.println("--- j: "+j+" run L: "+run.length);
                if(run[j].getTopic() != i + 351){
                    break;
                }//if
                if (scoreMin[i] > run[j].getScore()) {
                    scoreMin[i] = run[j].getScore();
                }//if
                if (scoreMax[i] < run[j].getScore()) {
                    scoreMax[i] = run[j].getScore();
                }//if
                j++;
            }//while
        }//for

        RunDataNorm[] runN=new RunDataNorm[run.length];
        for(int i=0; i<runN.length; i++){
            int topicIdx=run[i].getTopic()-351;
            runN[i] = new RunDataNorm(run[i],scoreMin[topicIdx],scoreMax[topicIdx]);
        }//for

        return runN;

    }//leggiRunNorm

    public static double[] getScores(Record[] runs, int topic, String doc){
        double[] result= new double[10];
        int index=0;
        for(int k=0; k<runs.length; k++){
            if(runs[k].getTopic()==topic && runs[k].getDoc().equals(doc)) {
                result[index]=runs[k].getScore();
                index++;
            }//if
        }//for
        double[] result2= new double[index];
        for(int i=0; i<result2.length; i++){
              result2[i]=result[i];
        }//for
        return result2;
    }//getScores

    public static double combMIN(Record[] runs, int topic, String doc){
        double[] res=getScores(runs,topic,doc);
        double min=res[0];
        for(int i=1; i<res.length;i++){
            if(min>res[i]){
                min=res[i];
            }//if
        }//for
        return min;
    }//combMIN

    public static double combMAX(Record[] runs, int topic, String doc){
        double[] res=getScores(runs,topic,doc);
        double max=res[0];
        for(int i=1; i<res.length;i++){
            if(max<res[i]){
                max=res[i];
            }//if
        }//for
        return max;
    }//combMAX

    public static double combSUM(Record[] runs, int topic, String doc){
        double[] res=getScores(runs,topic,doc);
        double sum=res[0];
        for(int i=1; i<res.length;i++){
            sum+=res[i];
        }//for
        return sum;
    }//combSUM

    public static void main(String[] args){

        RunDataNorm[][] runN= new RunDataNorm[10][];

        System.out.println("*** LETTURA DELLE RUN:");
        //Con Stemmer
        runN[0] = leggiRunNorm("D:/Uni/IR/IRprogVario/terrier-core-4.1/var/results/BB2c1.0_0.res");
        runN[1] = leggiRunNorm("D:/Uni/IR/IRprogVario/terrier-core-4.1/var/results/BM25b0.75_1.res");
        runN[2] = leggiRunNorm("D:/Uni/IR/IRprogVario/terrier-core-4.1/var/results/PL2c1.0_2.res");
        runN[3] = leggiRunNorm("D:/Uni/IR/IRprogVario/terrier-core-4.1/var/results/TF_IDF_3.res");
        runN[4] = leggiRunNorm("D:/Uni/IR/IRprogVario/terrier-core-4.1/var/results/Hiemstra_LM0.15_4.res");
        //Senza Stemmer
        runN[5] = leggiRunNorm("D:/Uni/IR/IRprogVario/terrier-core-4.1/var/results/BB2c1.0_5.res");
        runN[6] = leggiRunNorm("D:/Uni/IR/IRprogVario/terrier-core-4.1/var/results/BM25b0.75_6.res");
        runN[7] = leggiRunNorm("D:/Uni/IR/IRprogVario/terrier-core-4.1/var/results/PL2c1.0_7.res");
        runN[8] = leggiRunNorm("D:/Uni/IR/IRprogVario/terrier-core-4.1/var/results/TF_IDF_8.res");
        runN[9] = leggiRunNorm("D:/Uni/IR/IRprogVario/terrier-core-4.1/var/results/Hiemstra_LM0.15_9.res");

        System.out.println("*** CREAZIONE ARRAY TOTALE runs ...");
        Record[] runs=new Record[462875];
        for(int j=0; j<5;j++) {
            for (int i = 0; i < runN[j].length; i++) {
                runs[j*47396+i] = new Record(runN[j][i].getTopic(),runN[j][i].getIdDoc(),runN[j][i].getIdRun(),runN[j][i].getScoreNorm());
            }//for
        }//for
        for(int j=5; j<10;j++) {
            for (int i = 0; i < runN[j].length; i++) {
                runs[236980+(j-5)*45179+i] = new Record(runN[j][i].getTopic(),runN[j][i].getIdDoc(),runN[j][i].getIdRun(),runN[j][i].getScoreNorm());
            }//for
        }//for

        System.out.println("*** CREAZIONE LISTA DOCUMENT_ID ...");
        String[] docs = new String[70369];   //Lista dei Doc_ID
        int max = 0;    //max numero di doc_ID = 70369
        boolean found = false;
        for(int k=0; k<runs.length; k++){
            for (int m=0; m<max; m++) {
                if (runs[k].getDoc().compareTo(docs[m])==0) {
                    found = true;
                    break;
                }//if
            }//for
            if (!found) {
                docs[max]=runs[k].getDoc();
                max++;
            }//if
            found = false;
        }//for

        System.out.println("*** TEST combMIN ...");
        System.out.println("combMIN di topic=400 doc='FR940202-1-00020': "+combMIN(runs,400,"FR940202-1-00020"));
        System.out.println("combMIN di topic=400 doc='FR940202-1-00020': "+combMIN(runs,400,"LA032590-0089"));

        System.out.println("*** TEST combMAX ...");
        System.out.println("combMAX di topic=400 doc='FR940202-1-00020': "+combMAX(runs,400,"FR940202-1-00020"));
        System.out.println("combMAX di topic=400 doc='FR940202-1-00020': "+combMAX(runs,400,"LA032590-0089"));

        System.out.println("*** TEST combSUM ...");
        System.out.println("combSUM di topic=400 doc='FR940202-1-00020': "+combSUM(runs,400,"FR940202-1-00020"));
        System.out.println("combSUM di topic=400 doc='FR940202-1-00020': "+combSUM(runs,400,"LA032590-0089"));

    }//main
}//ResRun
