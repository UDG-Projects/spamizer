package main.java.spamizer.entity;

import java.util.ArrayList;
import java.util.List;

public class Result {

    private double spamNumber, hamNumber, validateNumber;
    private double phi, k;
    private double tp, fp, tn, fn;
    private long totalMillis;


    private List<Exception> exceptions;

    public Result(){
        exceptions = new ArrayList<>();
    }

    @Override
    public String toString() {
        return mountString(-1);
    }

    public String mountString(int count){
        String exceptionsResult = "";
        for(Exception e: exceptions){
            exceptionsResult+= "EXCEPCIO ================================================\n";
            exceptionsResult+=e.getStackTrace();
            exceptionsResult+="\n\n";
        }

        //double base = (spamNumber / (50 * hamNumber + spamNumber));
        //double error = ((50 * fp + fn) / (50 * hamNumber + spamNumber) + 0.000001);
        return "---------------------------------------------------------" + "\n" +
                "---------------------------------------------------------" + "\n" +
                " Execution number  : " + count + "\n" +
                " Spam number       : " + spamNumber + "\n" +
                " Ham number        : " + hamNumber + "\n" +
                " Unknwon number    : " + validateNumber + "\n" +
                " PHI               : " + phi + "\n" +
                " K                 : " + k + "\n" +
                " Accuracy          : " + ((validateNumber - fp - fn) / validateNumber) * 100 + " %\n" +
                " Total Cost Ratio  : " + (tn + fn) / ((50 * fp) + fn + 0.000001) + "\n" +
                "---------------------------------------------------------" + "\n" +
                " Missatge HAM classificat correctament com a HAM : " + tp + "\n" +
                " Missatge HAM classificat com a SPAM : " + fp + "\n" +
                " Missatge SPAM classificat correctament com spam : " + tn + "\n" +
                " Missatge SPAM classificat com a HAM : " + fn + "\n" +
                "---------------------------------------------------------" + "\n" +
                "---------------------------------------------------------" + "\n" +
                exceptionsResult.toString();
    }


    public void setSpamNumber(int spamNumber) {
        this.spamNumber = spamNumber;
    }

    public void setHamNumber(int hamNumber) {
        this.hamNumber = hamNumber;
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

    public void setTp(int tp) {
        this.tp = tp;
    }

    public void setSpamNumber(double spamNumber) {
        this.spamNumber = spamNumber;
    }

    public void setHamNumber(double hamNumber) {
        this.hamNumber = hamNumber;
    }

    public void setFp(int fp) {
        this.fp = fp;
    }

    public void setTn(int tn) {
        this.tn = tn;
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

    public double getTp() {
        return tp;
    }

    public double getFp() {
        return fp;
    }

    public double getTn() {
        return tn;
    }

    public double getFn() {
        return fn;
    }

    public void addException(Exception e){
        exceptions.add(e);
    }

    public double getHamNumber() {
        return hamNumber;
    }

    public double getSpamNumber() {
        return spamNumber;
    }
}
