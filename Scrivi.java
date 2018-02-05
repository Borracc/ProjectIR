//Scrivi.java

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/*
* Le operazioni sono state scomposte:
* questo codice legge le run risultanti dall'indicizzazione e il reperimento di Terrier,
* salva in un file 'docs.txt' gli id dei documenti che compaiono nelle run
* e meorizza i valori normalizzati secondo i diversi metodi
* */

public class Scrivi {

    //metodi per lettura/scrittura
    private static PrintStream apriFileScrittura(String nome){
        try{
            FileOutputStream file = new FileOutputStream(nome);
            PrintStream scrivi = new PrintStream(file);
            return scrivi;
        }catch (IOException e){
            System.out.println("Errore: " + e);
            System.exit(1);
            return null;
        }//try-catch
    }//apriFileScrittura

    private static void chiudiStreamO(PrintStream str){
        str.close();
    }//chiudiFIleO

    //lettura delle run dal file ***.res
    private static RunData[] leggiRun(String pathFile){

        RunData[] run1=new RunData[47396];
        int index=0;

        File doc=new File(pathFile);
        URL path=null;

        try{
            path=doc.toURL();
            doc=new File(path.getFile());
            System.out.println("Nome del file " + doc);
            String s;
            InputStream is=path.openStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
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

    //Data una run restituisce il minimo score di ogni topic
    private static double[] getMinsRun(RunData[] run){
        double[] scoreMin = new double[50];
        for(int i=0; i<50; i++){
            scoreMin[i]=Double.POSITIVE_INFINITY;
        }//for
        int j=0;
        for(int i=0; i<50; i++){
            while (j<run.length) {
                if(run[j].getTopic() != i + 351){
                    break;
                }//if
                if (scoreMin[i] > run[j].getScore()) {
                    scoreMin[i] = run[j].getScore();
                }//if
                j++;
            }//while
        }//for
        return scoreMin;
    }//getMinRun

    //Data una run restituisce il massimo score di ogni topic
    private static double[] getMaxsRun(RunData[] run){
        double[] scoreMax = new double[50];
        for(int i=0; i<50; i++){
            scoreMax[i]=Double.NEGATIVE_INFINITY;
        }//for
        int j=0;
        for(int i=0; i<50; i++){
            while (j<run.length) {
                if(run[j].getTopic() != i + 351){
                    break;
                }//if
                if (scoreMax[i] < run[j].getScore()) {
                    scoreMax[i] = run[j].getScore();
                }//if
                j++;
            }//while
        }//for
        return scoreMax;
    }//getMaxsRun

    //Data una run restituisce la somma score di ogni topic scalati secondo il relativo minimo
    private static double[] getSumMins(RunData[] run, double[] mins){
        double[] scoreSumMin = new double[50];
        for(int i=0; i<50; i++){
            scoreSumMin[i]=0.0;
        }//for
        int j=0;
        for(int i=0; i<50; i++){
            while (j<run.length) {
                if(run[j].getTopic() != i + 351){
                    break;
                }//if
                scoreSumMin[i]+=run[j].getScore()-mins[i];
                j++;
            }//while
        }//for
        return scoreSumMin;
    }//getSumMins

    //Data una run restituisce la media degli scores di ogni topic
    private static double[] getAvgsRun(RunData[] run){
        double[] scoreAvg = new double[50];
        for(int i=0; i<50; i++){
            scoreAvg[i]=0.0;
        }//for
        int j=0;
        for(int i=0; i<50; i++){
            int k=0;
            while (j<run.length) {
                if(run[j].getTopic() != i + 351){
                    scoreAvg[i]=scoreAvg[i]/k;
                    break;
                }//if
                scoreAvg[i]+=run[j].getScore();
                k++;
                j++;
            }//while
        }//for
        return scoreAvg;
    }//getAvgsRun

    //Data una run restituisce lo standard error degli scores di ogni topic
    private static double[] getStdsRun(RunData[] run, double[] avgs){
        double[] scoreStds = new double[50];
        for(int i=0; i<50; i++){
            scoreStds[i]=0.0;
        }//for
        int j=0;
        for(int i=0; i<50; i++){
            int k=0;
            while (j<run.length) {
                if(run[j].getTopic() != i + 351){
                    scoreStds[i]=Math.sqrt(scoreStds[i]/k);
                    break;
                }//if

                double scarto=run[j].getScore()-avgs[i];
                scoreStds[i]+=Math.pow(scarto,2);
                k++;
                j++;
            }//while
        }//for
        return scoreStds;
    }//getStdsRun

    //lettura delle run e normalizzazione degli scores secondo i 3 metodi: stamdard, sum e ZMUV
    private static RunDataNorm[] leggiRunNorm(String pathFile){
        RunData[] run= leggiRun(pathFile);

        double[] scoreMin = getMinsRun(run);
        double[] scoreMax = getMaxsRun(run);
        double[] scoreSumMin = getSumMins(run,scoreMin);
        double[] scoreAvg= getAvgsRun(run);
        double[] scoreStds= getStdsRun(run, scoreAvg);

        RunDataNorm[] runN=new RunDataNorm[run.length];
        for(int i=0; i<runN.length; i++){
            int topicIdx=run[i].getTopic()-351;
            runN[i] = new RunDataNorm(run[i], scoreMin[topicIdx], scoreMax[topicIdx], scoreSumMin[topicIdx], scoreAvg[topicIdx], scoreStds[topicIdx]);
        }//for
        return runN;
    }//leggiRunNorm


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
                runs[j*47396+i] = new Record(runN[j][i].getTopic(),runN[j][i].getIdDoc(),runN[j][i].getIdRun(),runN[j][i].getNormStandard(),runN[j][i].getNormSum(),runN[j][i].getNormZMUV());
            }//for
        }//for
        for(int j=5; j<10;j++) {
            for (int i = 0; i < runN[j].length; i++) {
                runs[236980+(j-5)*45179+i] = new Record(runN[j][i].getTopic(),runN[j][i].getIdDoc(),runN[j][i].getIdRun(),runN[j][i].getNormStandard(),runN[j][i].getNormSum(),runN[j][i].getNormZMUV());
            }//for
        }//for

        PrintStream scrivi=apriFileScrittura("docs.txt");

        System.out.println("*** CREAZIONE LISTA DOCUMENT_ID ...");
        String[] docs = new String[70369];   //Lista dei Doc_ID
        int max = 0;    //max numero di doc_ID
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
                scrivi.println(docs[max]);
                max++;
            }//if
            found = false;
        }//for

        chiudiStreamO(scrivi);

    }//main

}//Scrivi
