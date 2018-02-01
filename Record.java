public class Record {

    private int topic;
    private String doc;
    private String model;
    private double scoreStd;
    private double scoreSum;
    private double scoreZMUV;

    public Record(){
        topic=0;
        doc="doc0";
        model="mod0";
        scoreStd=0.5;
        scoreSum=0.5;
        scoreZMUV=0.5;
    }//Costruttore di default

    public Record(int t, String d, String m, double std, double sum, double zmuv){
        topic=t;
        doc=d;
        model=m;
        scoreStd=std;
        scoreSum=sum;
        scoreZMUV=zmuv;
    }//Costruttore parametrico

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

    public double getScoreStd() {
        return scoreStd;
    }//getScoreStd

    public double getScoreSum() {
        return scoreSum;
    }//getScoreSum

    public double getScoreZMUV() {
        return scoreZMUV;
    }//getScoreZMUV

    @Override
    public String toString() {
        return ""+topic+" "+doc+" "+model+" "+scoreStd+" "+scoreSum+" "+scoreZMUV;
    }//toString
}//Record
