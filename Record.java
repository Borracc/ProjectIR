public class Record {

    private int topic;
    private String doc;
    private String model;
    private double score;

    public Record(){
        topic=0;
        doc="doc0";
        model="mod0";
        score=0.5;
    }//Costruttore di default

    public Record(int t, String d, String m, double s){
        topic=t;
        doc=d;
        model=m;
        score=s;
    }//Costruttore di default

    //metodi accessori

    public int getTopic() {
        return topic;
    }//getTopic

    public String getDoc() {
        return doc;
    }//getDoc

    public String getModel() {
        return model;
    }//getModel

    public double getScore() {
        return score;
    }//getScore

    @Override
    public String toString() {
        return ""+topic+" "+doc+" "+model+" "+score;
    }//toString
}//Record
