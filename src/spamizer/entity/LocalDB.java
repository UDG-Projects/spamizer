package spamizer.entity;

import javafx.util.Pair;
import org.hsqldb.Table;

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
    //protected static Connection connection;

    private LocalDB(){}

    private static void init() throws ClassNotFoundException, SQLException, IOException {
       /* database = new LocalDB();
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        database.connection = DriverManager.getConnection("jdbc:hsqldb:file:db/result_db;shutdown=true;hsqldb.write_delay=false;","spamizer", "spamizer");

        /**
         * Configurem la base de dades per que s'utilitzi la sintaxis mysql.
         *
        Statement statement = connection.createStatement();
        statement.execute("SET DATABASE SQL SYNTAX MYS TRUE");
        statement.close();*/

        database = new LocalDB();

        if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
            ham_table = new File("db\\" + HAM_TABLE_NAME);
            spam_table = new File("db\\" + SPAM_TABLE_NAME);
            message = new File("db\\" + MESSAGE);
            results = new File("db\\" + RESULTS);
        }
        else{
            ham_table = new File("db/" + HAM_TABLE_NAME);
            spam_table = new File("db/" + SPAM_TABLE_NAME);
            message = new File("db/" + MESSAGE);
            results = new File("db/" + RESULTS);
        }
    }

    public static LocalDB getInstance() throws SQLException, ClassNotFoundException, IOException {
        if(database == null) {
            database.init();
        }
        return database;
    }

    private void append(String toWrite, File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.append(toWrite);
        writer.close();
    }

    public void insertResult(double phi, double k, int tp, int tn, int fp, int fn,int totalHam, int totalSpam) throws SQLException, IOException {
        String toWrite = phi + "," + k + "," + tp + "," + tn + "," + fp+ "," +fn+ "," +totalHam+ "," +totalSpam;
        append(toWrite, results);


        /*Statement statement = connection.createStatement();
        String insert = "INSERT INTO "+ Table.RESULTS + "(phi,k,true_positive,true_negative,false_positive,false_negative, nham, nspam) " +
                "VALUES (" + phi + "," + k + "," + tp + "," + tn + "," + fp + "," + fn + ","+totalHam+ ","+totalSpam+")";

        statement.executeUpdate(insert);
        statement.closeOnCompletion();*/


    }


    public void delete(Database.Table table) throws SQLException {
        if(table.equals(Database.Table.HAM)){
            ham_table.delete();
        }
        else if(table.equals(Database.Table.SPAM)) {
            spam_table.delete();
        }
        else if(table.equals(Database.Table.MESSAGE)){
            message.delete();
        }
        else{
            results.delete();
        }
    }


    public HashMap<String, Integer> select(Database.Table table) throws SQLException, IOException {
        List<String> lines  = null;
        if(table.equals(Database.Table.HAM)){
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
            String first = parts[0];
            int second = Integer.valueOf(parts[1]);
            times.put(first, second);
        }
        return times;
    }

    public Pair<Integer, Integer> selectMessages() throws SQLException, IOException {
        List<String> lines = Files.readAllLines(Paths.get(message.getPath()));
        if(lines.size() > 1 || lines.size() <= 0){
            throw new FileNotFoundException("El fitxer messages només pot tenir una línia.");
        }
        String[] parts = lines.get(0).split(",");
        return new Pair<Integer, Integer>(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]));
    }



    public void insertCounters(int ham, int spam) throws SQLException, IOException {
        results.delete();
        String insert = ham + "," + spam;
        append(insert, results);

        //insertCounters(connection, ham, spam);
    }
/*

    public void closeDB() throws SQLException {

    }

    */




    public void insertOrUpdate(Database.Table table, HashMap<String, Integer> appearances) throws SQLException, IOException {
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

        if(table.equals(Database.Table.HAM)){
            append(insert, ham_table);
        }
        else{
            append(insert, spam_table);
        }

        //insertOrUpdate(table, appearances, connection);
    }

    /*
    public void insertZeroValues(Database.Table table, HashMap<String, Integer> appearances) throws SQLException {
        insertZeroValues(table, appearances, connection);
    }*/


}
