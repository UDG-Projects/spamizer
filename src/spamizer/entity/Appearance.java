package spamizer.entity;

public class Appearance {

    private String word;
    private int times;

    public Appearance(String word, int times){
        this.word = word;
        this.times= times;
    }

    public String getWord() {
        return word;
    }

    public int getTimes() {
        return times;
    }
}
