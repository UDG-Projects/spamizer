package spamizer.MLCore;

public class ProbabylityResult {

    private double hamProbability;
    private double spamProbability;

    public ProbabylityResult(){

    };

    public double getHamProbability() {
        return hamProbability;
    }

    public void setHamProbability(double hamProbability) {
        this.hamProbability = hamProbability;
    }

    public double getSpamProbability() {
        return spamProbability;
    }

    public void setSpamProbability(double spamProbability) {
        this.spamProbability = spamProbability;
    }
}
