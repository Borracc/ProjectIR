//RunDataNorm.java

/*
* I campi rappresentano lo score "normalizzato" di un certo documento per un certo topic.
* Ognuno dei 3 campi rappresenta la normalizzazione secondo un metodo presentato nel paper:
* normStandard ==> minimo=0 e massimo=1;
* normSum=0;   ==> minimo=0 e somma=1
* normZMUV=0;  ==> media=0 e varianza=1
*/
public class RunDataNorm  extends  RunData{

    private double normStandard;
    private double normSum;
    private double normZMUV;

    public RunDataNorm(){
        super();
        normStandard=0;
        normSum=0;
        normZMUV=0;
    }//costruttore di default

    public RunDataNorm(int t, String q, String d, int r, double s, String run, double nSt, double nSu, double nZm){
        super(t, q, d, r, s, run);
        normStandard=nSt;
        normSum=nSu;
        normZMUV=nZm;
    }//costruttore parametrico

    public RunDataNorm(RunData rd, double min, double max, double sumMin, double avg, double std){
        super(rd.getTopic(),rd.getQ0(), rd.getIdDoc(), rd.getRank(), rd.getScore(), rd.getIdRun());
        normStandard=(rd.getScore()-min)/(max-min);
        normSum=(rd.getScore()-min)/sumMin;
        normZMUV=(rd.getScore()-avg)/std;
    }//costruttore parametrico

    //metodi accessori
    public double getNormStandard() {
        return normStandard;
    }//getNormStandard

    public double getNormSum() {
        return normSum;
    }//getNormSum

    public double getNormZMUV() {
        return normZMUV;
    }//getNormZMUV

    public String toString(){
        return super.toString()+" "+normStandard+" "+normSum+" "+normZMUV;
    }//toString
}//RunDataNorm
