//RunDataNorm.java

/*
* Il campo scoreNorm rappresenta lo score "normalizzato" di un certo documento per un certo topic.
* Scegliamo di normalizzare gli score con il metodo standard che prevede score minimo = 0 e score massimo =1
*/
public class RunDataNorm  extends  RunData{

    private double scoreNorm;

    public RunDataNorm(){
        super();
        scoreNorm=1;
    }//costruttore di default

    public RunDataNorm(int t, String q, String d, int r, double s, String run, double n){
        super(t, q, d, r, s, run);
        scoreNorm=n;
    }//costruttore parametrico

    public RunDataNorm(RunData rd, double min, double max){
        super(rd.getTopic(),rd.getQ0(), rd.getIdDoc(), rd.getRank(), rd.getScore(), rd.getIdRun());
        scoreNorm=(rd.getScore()-min)/(max-min);
    }//costruttore parametrico

    //metodi accessori
    public double getScoreNorm() {
        return scoreNorm;
    }//getScoreNorm

    public String toString(){
        return super.toString()+" "+scoreNorm;
    }//toString
}//RunDataNorm
