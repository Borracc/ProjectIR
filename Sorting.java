//Sorting.java

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Le operazioni sono state scomposte:
 * questo codice legge un file 'comb**@@.txt' specificato contente i risultati dal rank fusion,
 * riordina le run in ordine decrescente di score e assegna il rank
 * scrive dei file 'Rcomb**@@.txt' con le run ordinate secondo il rank in formato TREC
 * */

public class Sorting {

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

    //lettura del file 'comb**@@.txt'
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

    public static void main(String[] args) throws IOException {

        String nomeFile="combMEDstd";//solo il nome senza estensione

        System.out.println("*** LETTURA RUN:");
        RunData[] run = leggiRun(nomeFile+".txt");

        System.out.println("*** ORDINAMENTO RUN ...");
        int correntTopic=351;
        for (int i = 0; i < run.length-1; i++) {
            if(run[i].getTopic()!=correntTopic){
                correntTopic++;
            }//if
            double max = run[i].getScore();
            int index = i;
            for (int j = i + 1; j < run.length && run[j].getTopic()==correntTopic; j++) {
                if (max < run[j].getScore()) {
                    max = run[j].getScore();
                    index = j;
                }//if
            }//for
            RunData temp = run[i];
            run[i] = run[index];
            run[index] = temp;
        }//for

        System.out.println("*** SCRITTURA RUN ...");
        PrintStream ps=apriFileScrittura("R"+nomeFile+".txt");
        int rank=0;
        for(int i=0; i<run.length; i++){
            if(rank!=0 && run[i].getTopic()!=run[i-1].getTopic()){
                rank=0;
            }//if
            ps.println(new RunData(run[i].getTopic(),"q0",run[i].getIdDoc(),rank,run[i].getScore(),nomeFile).toString());
            rank++;
        }//for
        chiudiStreamO(ps);

    }//main

}//Sorting
