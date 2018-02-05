//RunData.java

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class RunData {

    private int topic;
    private String q0;
    private String idDoc;
    private int rank;
    private double score;
    private String idRun;

    public RunData(){
        topic=0;
        q0="q0";
        idDoc="doc0";
        rank=0;
        score=1.0;
        idRun="run1";
    }//costruttore di default

    public RunData(int t, String q, String d, int r, double s, String run){
        topic=t;
        q0=q;
        idDoc=d;
        rank=r;
        score=s;
        idRun=run;
    }//costruttore parametrico

    //Costruttore data la stringa corrispondente ad una linea della run (linea del file ***.res)
    public RunData(String s){
        int i=0;
        String temp=RunData.leggiParola(s,i);
        i+=temp.length()+1;
        topic= parseInt(temp);
        temp=RunData.leggiParola(s,i);
        i+=temp.length()+1;
        q0=temp;
        temp=RunData.leggiParola(s,i);
        i+=temp.length()+1;
        idDoc=temp;
        temp=RunData.leggiParola(s,i);
        i+=temp.length()+1;
        rank= parseInt(temp);
        temp=RunData.leggiParola(s,i);
        i+=temp.length()+1;
        score= parseDouble(temp);
        temp=RunData.leggiParola(s,i);
        i+=temp.length()+1;
        idRun=temp;
    }//costruttore da Stringa

    public static String leggiParola(String s,int i){
        char[]sc=s.toCharArray();
        String r="";
        while(sc[i]!=' '){
            r+=sc[i];
            i++;
            if(i==s.length()){
                break;
            }//if
        }//while
        return r;
    }//leggiParola

    //metodi accessori
    public int getTopic(){
        return topic;
    }//getTopic

    public String getQ0() {
        return q0;
    }//getQ0

    public String getIdDoc() {
        return idDoc;
    }//getIdDoc

    public int getRank() {
        return rank;
    }//getRank

    public double getScore() {
        return score;
    }//getScore

    public String getIdRun() {
        return idRun;
    }//getIdRun

    @Override
    public String toString() {
        return ""+topic+" "+q0+" "+idDoc+" "+rank+" "+score+" "+idRun;
    }//toString
}//RunData
