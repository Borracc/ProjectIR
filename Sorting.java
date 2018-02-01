//Sorting.java

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Sorting {

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

        RunData[] run1=new RunData[71195];
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

   public static void main(String[] args) throws IOException {

        String nomeFile="combMAXstd.txt";

        System.out.println("*** LETTURA RUN:");
        RunData[] run = leggiRun(nomeFile);

        System.out.println("*** ORDINAMENTO RUN ...");
        int k=0;
        for (int i = 0; i < run.length; i++) {
            if(run[i].getTopic()!=k){
                k++;
            }//if
            double max = run[i].getScore();
            int index = 0;
            for (int j = i + 1; j < run.length && run[j].getTopic()==k; j++) {
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
        PrintStream ps=apriFileScrittura("R"+nomeFile);
        for(int i=0; i<run.length; i++){
            ps.println(new RunData(run[i].getTopic(),"q0",run[i].getIdDoc(),i,run[i].getScore(),"nomeFile").toString());
        }//for
       chiudiStreamO(ps);

    }//main

}//Sorting
