package spamizer.entity;

import javafx.util.Pair;
import java.sql.*;
import java.util.*;

public class MemDB {

    private HashMap<String, Integer> wordsHam;
    private HashMap<String, Integer> wordsSpam;
    private Set<String> vocabulary;
    private int spamM;
    private int hamM;

    public enum Column {
        SPAM("SPAM"),
        HAM("HAM"),
        ;


        private final String column;

        /**
         * @param table
         */
        Column(final String table) {
            this.column = table;
        }

        @Override
        public String toString() {
            return column;
        }
    }
    private static MemDB memDB;

    private MemDB(){}

    public static MemDB getInstance() throws SQLException, ClassNotFoundException {
        if(memDB == null) {
            memDB = new MemDB();
            memDB.wordsHam = new HashMap<>();
            memDB.wordsSpam = new HashMap<>();
            memDB.vocabulary = new HashSet<>();
            memDB.spamM = 0;
            memDB.hamM = 0;
        }
        return memDB;
    }

    public void clearDB(){
        this.memDB=null;
    }

    public void insertOrUpdate(Database.Table table, HashMap<String, Integer> appearances) {
        vocabulary.addAll(appearances.keySet());
        if(table.equals(Database.Table.HAM)){
            appearances.forEach(
                    (key, value) -> wordsHam.merge( key, value, (v1, v2) -> v1+v2));
        }
        else{
            appearances.forEach(
                    (key, value) -> wordsSpam.merge( key, value, (v1, v2) -> v1+v2));
        }
    }

    public void updateCounters(int ham, int spam) {
        hamM += ham;
        spamM += spam;
    }


    public String selectCounters() {
        return hamM + " " + spamM;
    }

    public double getMessageProbabylity(Column column, double k) {
        if(column.equals(Column.HAM)){
            return Math.log((hamM+k) / ((hamM + spamM) + k * 2));
        }
        else{
            return Math.log((spamM+k) / ((hamM + spamM) + k * 2));
        }

    }

    public int getCountAlphabet(Database.Table table) {
        int count = vocabulary.size();
        if(table.equals(Database.Table.HAM)){
            for(Map.Entry<String, Integer> word : wordsHam.entrySet()){
                count += word.getValue();
            }
        }
        else{
            for(Map.Entry<String, Integer> word : wordsSpam.entrySet()){
                count += word.getValue();
            }
        }
        return count;
    }


    public double calculateProbability(List<String> words, Database.Table table, double k, int totalWords) throws SQLException {
        double counter = 0;
        for(String s : words){
            int times = 0;
            if(table.equals(Database.Table.HAM)){
                if(wordsHam.containsKey(s)){
                    times = wordsHam.get(s);
                }
            }
            else {
                if (wordsSpam.containsKey(s)) {
                    times = wordsSpam.get(s);
                }
            }
            counter += Math.log((times +  k)/(totalWords+k*2));
        }
        return counter;
    }

    public HashMap<String, Integer> select(Database.Table table) throws SQLException {
        if(Database.Table.SPAM.equals(table))
            return wordsSpam;
        else
            return wordsHam;
    }

    public Pair<Integer, Integer> selectMessages() {
        return new Pair<>(hamM, spamM);
    }

    public void insertCounters(int ham, int spam){
        hamM = ham;
        spamM = spam;
    }

    public int getSpamMessageNumber() {
        return spamM;
    }

    public int getHamMessageNumber() {
        return hamM;
    }
}

