package spamizer.entity;

public class Result {

    private int spamNumber, hamNumber, validateNumber;
    private double phi, k;
    private int tp, fp, tn, fn;
    private long totalMillis;

    public Result(){

    }

    @Override
    public String toString() {
        return "---------------------------------------------------------" + "\n" +
        "---------------------------------------------------------" + "\n" +
        " Spam number       : " + spamNumber + "\n" +
        " Ham number        : " + spamNumber + "\n" +
        " Unknwon number    : " + spamNumber + "\n" +
        " PHI               : " + phi + "\n" +
        " K                 : " + k + "\n" +
        "---------------------------------------------------------" + "\n" +
        " Missatge HAM classificat correctament com a HAM : " + tp + "\n" +
        " Missatge HAM classificat com a SPAM : " + fp + "\n" +
        " Missatge SPAM classificat correctament com spam : " + tn + "\n" +
        " Missatge SPAM classificat com a HAM : " + fn + "\n" +
        "---------------------------------------------------------" + "\n" +
        "---------------------------------------------------------" + "\n";
    }

    public int getSpamNumber() {
        return spamNumber;
    }

    public void setSpamNumber(int spamNumber) {
        this.spamNumber = spamNumber;
    }

    public int getHamNumber() {
        return hamNumber;
    }

    public void setHamNumber(int hamNumber) {
        this.hamNumber = hamNumber;
    }

    public int getValidateNumber() {
        return validateNumber;
    }

    public void setValidateNumber(int validateNumber) {
        this.validateNumber = validateNumber;
    }

    public double getPhi() {
        return phi;
    }

    public void setPhi(double phi) {
        this.phi = phi;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public int getTp() {
        return tp;
    }

    public void setTp(int tp) {
        this.tp = tp;
    }

    public int getFp() {
        return fp;
    }

    public void setFp(int fp) {
        this.fp = fp;
    }

    public int getTn() {
        return tn;
    }

    public void setTn(int tn) {
        this.tn = tn;
    }

    public int getFn() {
        return fn;
    }

    public void setFn(int fn) {
        this.fn = fn;
    }

    public long getTotalMillis() {
        return totalMillis;
    }

    public void setTotalMillis(long totalMillis) {
        this.totalMillis = totalMillis;
    }
}
