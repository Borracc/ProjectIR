//RankFusion.java

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/*
 * Le operazioni sono state scomposte:
 * questo codice legge le run risultanti dall'indicizzazione e il reperimento di Terrier,
 * legge dal file 'docs.txt' gli id dei documenti che compaiono nelle run,
 * esegue le strategie di rank fusion (combMIN,combMED,combMAX,combSUM,combMNZ,combANZ) per ogni documento e per ogni topic
 * e ne memorizza i risultati in file 'comb***@@@.txt' in cui:
 * *** = strategia di rank fusion(MIN,MED,MAX,SUM,MNZ,ANZ)
 * @@@ = metodo di normalizzazione(std,sum,zmuv)
 * ATTEZIONE: L'esecuzione richiede molto tempo, si consiglia di commentare le parti di codice che non interessano nel metodo 'main'
 * */

public class RankFusion {

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

    private static InputStream apriFileLettura(String nome){
        File doc=new File(nome);
        URL path=null;
        try{
            path=doc.toURL();
            InputStream is=path.openStream();
            return is;
        }catch (MalformedURLException e){
            System.out.println("Attenzione:" + e);
            return null;
        }catch (IOException e){
            System.out.println(e.getMessage());
            return null;
        }//try-catch
    }//apriFileScrittura

    private static void chiudiStreamI(InputStream str) throws IOException {
        str.close();
    }//chiudiFIleI

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

    //Strategie di rankfusion
    //Dati i risultati delle run, il numero del topic, l'id del documento e il metodo di normalizzzazione
    //restituisce gli scores corrispondenti
    private static double[] getScores(Record[] runs, int topic, String doc, int normMethod){
        double[] result= new double[10];
        int index=0;
        for(int k=0; k<runs.length; k++){
            if(runs[k].getTopic()==topic && runs[k].getDoc().equals(doc)) {
                if(normMethod==0){
                    result[index]=runs[k].getScoreStd();
                }else if(normMethod==1){
                    result[index]=runs[k].getScoreSum();
                }else{
                    result[index]=runs[k].getScoreZMUV();
                }//if-else
                index++;
            }//if
        }//for
        double[] result2= new double[index];
        for(int i=0; i<result2.length; i++){
            result2[i]=result[i];
        }//for
        return result2;
    }//getScores

    //dati insieme delle run, topic, id del documento e metodo di normalizzazione restituisce il relativo score minimo
    private static double combMIN(Record[] runs, int topic, String doc, int normMethod){
        double[] res=getScores(runs,topic,doc,normMethod);
        double min=Double.POSITIVE_INFINITY;
        for(int i=0; i<res.length;i++){
            if(min>res[i]){
                min=res[i];
            }//if
        }//for
        return min;
    }//combMIN

    //dati insieme delle run, topic, id del documento e metodo di normalizzazione restituisce il relativo score mediano
    private static double combMED(Record[] runs, int topic, String doc, int normMethod){
        double[] res=getScores(runs,topic,doc,normMethod);
        double med=Double.POSITIVE_INFINITY;
        if(res.length>0) {
            Arrays.sort(res);
            if (res.length % 2 == 0) {
                med = (res[res.length / 2] + res[(res.length / 2) - 1]) / 2;
            } else {
                med = res[res.length / 2];
            }//if-else
        }//if
        return med;
    }//combMED

    ////dati insieme delle run, topic, id del documento e metodo di normalizzazione restituisce il relativo score massimo
    private static double combMAX(Record[] runs, int topic, String doc, int normMethod){
        double[] res=getScores(runs,topic,doc,normMethod);
        double max=Double.NEGATIVE_INFINITY;
        for(int i=0; i<res.length;i++){
            if(max<res[i]){
                max=res[i];
            }//if
        }//for
        return max;
    }//combMAX

    //dati insieme delle run, topic, id del documento e metodo di normalizzazione restituisce la somma degli scores relativi
    private static double combSUM(Record[] runs, int topic, String doc, int normMethod){
        double[] res=getScores(runs,topic,doc,normMethod);
        double sum=Double.POSITIVE_INFINITY;
        if(res.length>0) {
            sum=0;
            for (int i = 0; i < res.length; i++) {
                sum += res[i];
            }//for
        }//if
        return sum;
    }//combSUM

    //dati insieme delle run, topic, id del documento e metodo di normalizzazione restituisce la somma degli scores
    //dividendola per il numero 'presenze' del documento nelle run
    private static double combANZ(Record[] runs, int topic, String doc, int normMethod){
        double[] res=getScores(runs,topic,doc,normMethod);
        double sum=Double.POSITIVE_INFINITY;
        if(res.length>0) {
            sum=0;
            for (int i = 1; i < res.length; i++) {
                sum += res[i];
            }//for
            return sum / res.length;
        }//if
        return sum;
    }//combANZ

    //dati insieme delle run, topic, id del documento e metodo di normalizzazione restituisce la somma degli scores
    //moltiplicandole per il numero 'presenze' del documento nelle run
    private static double combMNZ(Record[] runs, int topic, String doc, int normMethod){
        double[] res=getScores(runs,topic,doc,normMethod);
        double sum=Double.POSITIVE_INFINITY;
        if(res.length>0) {
            sum=0;
            for(int i=1; i<res.length;i++){
                sum+=res[i];
            }//for
            return sum*res.length;
        }//if
        return sum;
    }//combMNZ


    public static void main(String[] args) throws IOException {

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

        InputStream is = apriFileLettura("docs.txt");
        BufferedReader br=new BufferedReader(new InputStreamReader(is));
        String[] docs = new String[70369];   //Lista dei Doc_ID
        for(int i=0; i<docs.length;i++){
            docs[i]=br.readLine();
        }//for
        chiudiStreamI(is);

        System.out.println("*** APPLICAZIONE DELLE STRATEGIE DI RANK FUSION ...");
        PrintStream ps1;
        PrintStream ps2;
        PrintStream ps3;
        ///MIN
        ps1=apriFileScrittura("combMINstd.txt");
        ps2=apriFileScrittura("combMINsum.txt");
        ps3=apriFileScrittura("combMINzmuv.txt");
        System.out.println("*** RANK FUSION ... combMIN ..");
        for(int i=0; i<50; i++){
            for(int j=0; j<docs.length; j++){
                if(combMIN(runs,351+i,docs[j],0)!=Double.POSITIVE_INFINITY){
                    ps1.println(new RunData(i+351,"q0",docs[j],0,combMIN(runs,351+i,docs[j],0),"combMINstd").toString());
                    ps2.println(new RunData(i+351,"q0",docs[j],0,combMIN(runs,351+i,docs[j],1),"combMINsum").toString());
                    ps3.println(new RunData(i+351,"q0",docs[j],0,combMIN(runs,351+i,docs[j],2),"combMINzmuv").toString());
                }//if
            }//for
            System.out.println(" .. Fatto topic: "+(i+351));
        }//for
        chiudiStreamO(ps1);
        chiudiStreamO(ps2);
        chiudiStreamO(ps3);
        ///

        ///MAX
        ps1=apriFileScrittura("combMAXstd.txt");
        ps2=apriFileScrittura("combMAXsum.txt");
        ps3=apriFileScrittura("combMAXzmuv.txt");
        System.out.println("*** RANK FUSION ... combMAX ..");
        for(int i=0; i<50; i++){
            for(int j=0; j<docs.length; j++){
                if(combMAX(runs,351+i,docs[j],0)!=Double.NEGATIVE_INFINITY){
                    ps1.println(new RunData(i+351,"q0",docs[j],0,combMAX(runs,351+i,docs[j],0),"combMAXstd").toString());
                    ps2.println(new RunData(i+351,"q0",docs[j],0,combMAX(runs,351+i,docs[j],1),"combMAXsum").toString());
                    ps3.println(new RunData(i+351,"q0",docs[j],0,combMAX(runs,351+i,docs[j],2),"combMAXzmuv").toString());
                }//if
            }//for
            System.out.println(" .. Fatto topic: "+(i+351));
        }//for
        chiudiStreamO(ps1);
        chiudiStreamO(ps2);
        chiudiStreamO(ps3);
        ///

        ///MED
        ps1=apriFileScrittura("combMEDstd.txt");
        ps2=apriFileScrittura("combMEDsum.txt");
        ps3=apriFileScrittura("combMEDzmuv.txt");
        System.out.println("*** RANK FUSION ... combMED ..");
        for(int i=0; i<50; i++){
            for(int j=0; j<docs.length; j++){
                if(combMED(runs,351+i,docs[j],0)!=Double.POSITIVE_INFINITY){
                    ps1.println(new RunData(i+351,"q0",docs[j],0,combMED(runs,351+i,docs[j],0),"combMEDstd").toString());
                    ps2.println(new RunData(i+351,"q0",docs[j],0,combMED(runs,351+i,docs[j],1),"combMEDsum").toString());
                    ps3.println(new RunData(i+351,"q0",docs[j],0,combMED(runs,351+i,docs[j],2),"combMEDzmuv").toString());
                }//if
            }//for
            System.out.println(" .. Fatto topic: "+(i+351));
        }//for
        chiudiStreamO(ps1);
        chiudiStreamO(ps2);
        chiudiStreamO(ps3);
        ///

        ///SUM
        ps1=apriFileScrittura("combSUMstd.txt");
        ps2=apriFileScrittura("combSUMsum.txt");
        ps3=apriFileScrittura("combSUMzmuv.txt");
        System.out.println("*** RANK FUSION ... combSUM ..");
        for(int i=0; i<50; i++){
            for(int j=0; j<docs.length; j++){
                if(combSUM(runs,351+i,docs[j],0)!=Double.POSITIVE_INFINITY){
                    ps1.println(new RunData(i+351,"q0",docs[j],0,combSUM(runs,351+i,docs[j],0),"combSUMstd").toString());
                    ps2.println(new RunData(i+351,"q0",docs[j],0,combSUM(runs,351+i,docs[j],1),"combSUMsum").toString());
                    ps3.println(new RunData(i+351,"q0",docs[j],0,combSUM(runs,351+i,docs[j],2),"combSUMzmuv").toString());
                }//if
            }//for
            System.out.println(" .. Fatto topic: "+(i+351));
        }//for
        chiudiStreamO(ps1);
        chiudiStreamO(ps2);
        chiudiStreamO(ps3);
        ///

        ///ANZ
        ps1=apriFileScrittura("combANZstd.txt");
        ps2=apriFileScrittura("combANZsum.txt");
        ps3=apriFileScrittura("combANZzmuv.txt");
        System.out.println("*** RANK FUSION ... combANZ ..");
        for(int i=0; i<50; i++){
            for(int j=0; j<docs.length; j++){
                if(combANZ(runs,351+i,docs[j],0)!=Double.POSITIVE_INFINITY){
                    ps1.println(new RunData(i+351,"q0",docs[j],0,combANZ(runs,351+i,docs[j],0),"combANZstd").toString());
                    ps2.println(new RunData(i+351,"q0",docs[j],0,combANZ(runs,351+i,docs[j],1),"combANZsum").toString());
                    ps3.println(new RunData(i+351,"q0",docs[j],0,combANZ(runs,351+i,docs[j],2),"combANZzmuv").toString());
                }//if
            }//for
            System.out.println(" .. Fatto topic: "+(i+351));
        }//for
        chiudiStreamO(ps1);
        chiudiStreamO(ps2);
        chiudiStreamO(ps3);
        ///

        ///MNZ
        ps1=apriFileScrittura("combMNZstd.txt");
        ps2=apriFileScrittura("combMNZsum.txt");
        ps3=apriFileScrittura("combMNZzmuv.txt");
        System.out.println("*** RANK FUSION ... combMNZ ..");
        for(int i=0; i<50; i++){
            for(int j=0; j<docs.length; j++){
                if(combMNZ(runs,351+i,docs[j],0)!=Double.POSITIVE_INFINITY){
                    ps1.println(new RunData(i+351,"q0",docs[j],0,combMNZ(runs,351+i,docs[j],0),"combMNZstd").toString());
                    ps2.println(new RunData(i+351,"q0",docs[j],0,combMNZ(runs,351+i,docs[j],1),"combMNZsum").toString());
                    ps3.println(new RunData(i+351,"q0",docs[j],0,combMNZ(runs,351+i,docs[j],2),"combMNZzmuv").toString());
                }//if
            }//for
            System.out.println(" .. Fatto topic: "+(i+351));
        }//for
        chiudiStreamO(ps1);
        chiudiStreamO(ps2);
        chiudiStreamO(ps3);
        ///

    }//main
}//RankFusion
