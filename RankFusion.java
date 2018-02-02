//RankFusion.java

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class RankFusion {

    public static PrintStream apriFileScrittura(String nome){
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

    public static InputStream apriFileLettura(String nome){
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

    public static void chiudiStreamI(InputStream str) throws IOException {
        str.close();
    }//chiudiFIleI

    public static void chiudiStreamO(PrintStream str){
        str.close();
    }//chiudiFIleO

    //lettura delle run e normalizzazione degli scores
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

    public static double[] getMinsRun(RunData[] run){
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

    public static double[] getMaxsRun(RunData[] run){
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

    public static double[] getSumMins(RunData[] run, double[] mins){
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

    public static double[] getAvgsRun(RunData[] run){
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

    public static double[] getStdsRun(RunData[] run, double[] avgs){
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

    public static RunDataNorm[] leggiRunNorm(String pathFile){
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
    public static double[] getScores(Record[] runs, int topic, String doc, int normMethod){
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

    public static double combMIN(Record[] runs, int topic, String doc, int normMethod){
        double[] res=getScores(runs,topic,doc,normMethod);
        double min=Double.POSITIVE_INFINITY;
        for(int i=0; i<res.length;i++){
            if(min>res[i]){
                min=res[i];
            }//if
        }//for
        return min;
    }//combMIN

    public static double combMED(Record[] runs, int topic, String doc, int normMethod){
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

    public static double combMAX(Record[] runs, int topic, String doc, int normMethod){
        double[] res=getScores(runs,topic,doc,normMethod);
        double max=Double.NEGATIVE_INFINITY;
        for(int i=0; i<res.length;i++){
            if(max<res[i]){
                max=res[i];
            }//if
        }//for
        return max;
    }//combMAX

    public static double combSUM(Record[] runs, int topic, String doc, int normMethod){
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

    public static double combANZ(Record[] runs, int topic, String doc, int normMethod){
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

    public static double combMNZ(Record[] runs, int topic, String doc, int normMethod){
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

        PrintStream ps1=apriFileScrittura("combMINstd.txt");
        PrintStream ps2=apriFileScrittura("combMINsum.txt");
        PrintStream ps3=apriFileScrittura("combMINzmuv.txt");

        System.out.println("*** APPLICAZIONE DELLE STRATEGIE DI RANK FUSION ...");
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
        /*
        PrintStream ps1=apriFileScrittura("combMAXstd.txt");
        PrintStream ps2=apriFileScrittura("combMAXsum.txt");
        PrintStream ps3=apriFileScrittura("combMAXzmuv.txt");

        System.out.println("*** APPLICAZIONE DELLE STRATEGIE DI RANK FUSION ...");
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
        */
        ///////////////////////////////
        /*
        PrintStream ps1=apriFileScrittura("combMEDstd.txt");
        PrintStream ps2=apriFileScrittura("combMEDsum.txt");
        PrintStream ps3=apriFileScrittura("combMEDzmuv.txt");

        System.out.println("*** APPLICAZIONE DELLE STRATEGIE DI RANK FUSION ...");
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
        */
        /*///////////////SUM
        PrintStream ps1=apriFileScrittura("combSUMstd.txt");
        PrintStream ps2=apriFileScrittura("combSUMsum.txt");
        PrintStream ps3=apriFileScrittura("combSUMzmuv.txt");

        System.out.println("*** APPLICAZIONE DELLE STRATEGIE DI RANK FUSION ...");
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
        */
        /*////////////////ANZ
        PrintStream ps1=apriFileScrittura("combANZstd.txt");
        PrintStream ps2=apriFileScrittura("combANZsum.txt");
        PrintStream ps3=apriFileScrittura("combANZzmuv.txt");

        System.out.println("*** APPLICAZIONE DELLE STRATEGIE DI RANK FUSION ...");
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
        */
        /*///////////////////MNZ
        PrintStream ps1=apriFileScrittura("combMNZstd.txt");
        PrintStream ps2=apriFileScrittura("combMNZsum.txt");
        PrintStream ps3=apriFileScrittura("combMNZzmuv.txt");

        System.out.println("*** APPLICAZIONE DELLE STRATEGIE DI RANK FUSION ...");
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
        */


    }//main
}//RankFusion
