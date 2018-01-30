//Chiavi.java

public class Chiavi {

    private String idDoc;
    private int topic;
    private String modello;

    public Chiavi(){
        idDoc="doc0";
        topic=0;
        modello="modello0";
    }//costruttore di default

    public Chiavi(String d, int t, String m){
        idDoc=d;
        topic=t;
        modello=m;
    }//costruttore parametrico

    //metodi accessori

    public String getIdDoc() {
        return idDoc;
    }//getIdDoc

    public int getTopic() {
        return topic;
    }//getTopic

    public String getModello() {
        return modello;
    }//getModello

    @Override
    public String toString() {
        return ""+idDoc+" "+topic+" "+modello;
    }//toString
}//Chiavi
