package spamizer.entity;

import javafx.util.Pair;
import org.hsqldb.Table;
import spamizer.exceptions.NoMessageNumberException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalDB { //extends Database {

    public static String HAM_TABLE_NAME = "ham.csv";
    public static String SPAM_TABLE_NAME = "spam.csv";
    public static String RESULTS = "results.csv";
    public static String MESSAGE = "message.csv";

    private static boolean setUpDone;

    private static File ham_table;
    private static File spam_table;
    private static File results;
    private static File message;

    private static LocalDB database;

    private LocalDB(){}

    private static void init() throws IOException {

        database = new LocalDB();

        if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
            database.ham_table = new File("db\\" + HAM_TABLE_NAME);
            database.spam_table = new File("db\\" + SPAM_TABLE_NAME);
            database.message = new File("db\\" + MESSAGE);
            database.results = new File("db\\" + RESULTS);
        }
        else{
            database.ham_table = new File("db/" + HAM_TABLE_NAME);
            database.spam_table = new File("db/" + SPAM_TABLE_NAME);
            database.message = new File("db/" + MESSAGE);
            database.results = new File("db/" + RESULTS);
        }

        createIfNotExists(ham_table);
        createIfNotExists(spam_table);
        createIfNotExists(message);
        createIfNotExists(results);

    }

    private static void createIfNotExists(File file) throws IOException {
        if(!file.exists()){
            file.createNewFile();
        }
    }

    public static LocalDB getInstance() throws IOException {
        if(database == null) {
            database.init();
        }
        return database;
    }

    private void append(String toWrite, File file) throws IOException {
        FileWriter writer = new FileWriter(file, true);
        writer.append("\n" + toWrite);
        writer.close();
    }

    public void insertResult(Result result) throws IOException {
        if(!results.exists()){
            results.createNewFile();
        }

        String toWrite = result.getPhi()+ "," + result.getK() + "," + result.getTp() + "," + result.getTn() + "," + result.getFp()+ "," +result.getFn()+ "," +
                result.getHamNumber()+ "," +result.getSpamNumber();

        append(toWrite, results);
    }


    public void delete(TableEnumeration.Table table) {
        if(table.equals(TableEnumeration.Table.HAM)){
            ham_table.delete();
        }
        else if(table.equals(TableEnumeration.Table.SPAM)) {
            spam_table.delete();
        }
        else if(table.equals(TableEnumeration.Table.MESSAGE)){
            message.delete();
        }
        else{
            results.delete();
        }
    }


    public HashMap<String, Integer> select(TableEnumeration.Table table) throws IOException {
        List<String> lines  = null;
        if(table.equals(TableEnumeration.Table.HAM)){
            if(ham_table.exists())
                lines = Files.readAllLines(Paths.get(ham_table.getPath()));
            else
                throw new FileNotFoundException(ham_table.getPath());
        }
        else{
            if(spam_table.exists())
                lines = Files.readAllLines(Paths.get(spam_table.getPath()));
            else
                throw new FileNotFoundException(ham_table.getPath());
        }
        return mountHashMap(lines);
    }

    private HashMap<String, Integer> mountHashMap(List<String> lines){
        HashMap<String, Integer> times = new HashMap<>();
        for(String line : lines){
            String[] parts = line.split(",");
            if(parts.length == 2) {
                String first = parts[0];
                int second = Integer.valueOf(parts[1]);
                times.put(first, second);
            }
        }
        return times;
    }

    public Pair<Integer, Integer> selectMessages() throws IOException, NoMessageNumberException {
        if(message.exists()) {
            List<String> lines = Files.readAllLines(Paths.get(message.getPath()));
            if (lines.size() > 1 || lines.size() <= 0) {
                throw new FileNotFoundException("Messages File must contains only one line.");
            }
            String[] parts = lines.get(0).split(",");
            return new Pair<Integer, Integer>(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]));
        }
        else{
            throw new NoMessageNumberException("There is no file " + message.getPath() + " to read");
        }
    }



    public void insertCounters(int ham, int spam) throws IOException {

        if(!message.exists())
            message.createNewFile();

        message.delete();
        String insert = ham + "," + spam;
        append(insert, message);
    }

    public void insertOrUpdate(TableEnumeration.Table table, HashMap<String, Integer> appearances) throws IOException {

        if(table.equals(TableEnumeration.Table.HAM)){
            createIfNotExists(ham_table);
        }
        else {
            createIfNotExists(spam_table);
        }

        HashMap<String, Integer> resultats = select(table);
        for (Map.Entry<String, Integer> element : appearances.entrySet()) {
            if(resultats.containsKey(element.getKey())){
                resultats.put(element.getKey(), resultats.get(element.getKey()) + element.getValue());
            }
            else{
                resultats.put(element.getKey(), element.getValue());
            }
        }
        delete(table);
        String insert="";
        for(Map.Entry<String, Integer> element : resultats.entrySet()){
            insert += element.getKey() + "," + element.getValue() + "\n";
        }

        if(table.equals(TableEnumeration.Table.HAM)){
            append(insert, ham_table);
        }
        else{
            append(insert, spam_table);
        }
    }
}
