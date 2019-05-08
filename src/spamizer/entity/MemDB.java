package spamizer.entity;

import javafx.util.Pair;
import java.sql.*;
import java.util.*;

public class MemDB { // extends Database {

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

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return column;
        }
    }



    private static MemDB memDB;
    // private static Connection connection;


    private MemDB(){}

    /*private static void init() throws ClassNotFoundException, SQLException {
        memDB = new MemDB();
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        memDB.connection = DriverManager.getConnection("jdbc:hsqldb:mem:spamizer_db","spamizer", ""); //"SA", "");

        **
         * Configurem la base de dades per que s'utilitzi la sintaxis mysql.
         *
        Statement statement = connection.createStatement();
        statement.execute("SET DATABASE SQL SYNTAX MYS TRUE");
        statement.close();

        /** Es creen dues taules degut a que sinó es repeteix la clau primària
         *  Si s'utilitza le booleà queda tot bifurcat, el codi quedarà més senzill si passem el nom de la taula per paràmetre.
         *
        memDB.createTable(Table.HAM);
        memDB.createTable(Table.SPAM);
        memDB.createTable(Table.MESSAGE,"(id INTEGER, ham integer, spam integer)");
        //inserir els primers contadors
        memDB.insertCounters(0,0);

    }*/

    public static MemDB getInstance() throws SQLException, ClassNotFoundException {
        if(memDB == null) {
            memDB = new MemDB();
            memDB.wordsHam = new HashMap<>();
            memDB.wordsSpam = new HashMap<>();
            memDB.vocabulary = new HashSet<>();
            memDB.spamM = 0;
            memDB.hamM = 0;
            // memDB.init();
        }
        return memDB;
    }

  /*  private void createTable(Database.Table tableName) throws SQLException {
       createTable(tableName, "(word VARCHAR(255) not NULL, times INTEGER, PRIMARY KEY (word))");
    }*/

    public void insertOrUpdate(Database.Table table, HashMap<String, Integer> appearances) throws SQLException {
        vocabulary.addAll(appearances.keySet());
        if(table.equals(Database.Table.HAM)){
            appearances.forEach(
                    (key, value) -> wordsHam.merge( key, value, (v1, v2) -> v1+v2));
        }
        else{
            appearances.forEach(
                    (key, value) -> wordsSpam.merge( key, value, (v1, v2) -> v1+v2));
        }

       /* Statement statement = connection.createStatement();
        MemDB.Table complementaryTable = MemDB.Table.HAM;
        if (table == MemDB.Table.HAM)
            complementaryTable = MemDB.Table.SPAM;

        if(appearances.size() > 0) {
            String insert = "MERGE INTO " + table + " using (values";
            for (Map.Entry<String, Integer> element : appearances.entrySet()) {
                insert += "('" + element.getKey() + "'," + element.getValue() + "),";
            }

            insert = insert.substring(0, insert.length() - 1);
            insert += ") as vals(x, y) ON " + table + ".word = vals.x " +
                    "when matched then update set " + table + ".times = " + table + ".times + vals.y " +
                    "when not matched then insert values vals.x, vals.y";

            statement.executeUpdate(insert);

            insertZeroValues(complementaryTable, appearances);
        }
        statement.close();*/
    }

    public void updateCounters(int ham, int spam) throws SQLException {
        /*Statement statement = connection.createStatement();
        String update ="UPDATE " + Table.MESSAGE +
                " SET HAM = HAM +" + ham + ","+
                "    SPAM = SPAM +" + spam +
                " WHERE ID = 1";
        statement.executeUpdate(update);
        statement.close();*/

        hamM += ham;
        spamM += spam;
    }


    public String selectCounters() throws SQLException {
        /*
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery("SELECT * FROM " + Table.MESSAGE);
        String result = "";
        while(res.next()) {
            result += res.getInt("HAM") + " " + res.getInt("SPAM") + "\n";
        }
        statement.close();
        return result;*/
        return hamM + " " + spamM;
    }

    public double getMessageProbabylity(Column column, double k) throws SQLException {
        /*Statement statement = connection.createStatement();
        String query = "Select ln( CAST((" + column + " + " + k + ") as DOUBLE)/CAST(((HAM + SPAM) + " + k*2 +") as DOUBLE)) as PTOTAL from " +Table.MESSAGE + " where id = 1 ";

        ResultSet rs = statement.executeQuery(query);
        float    result=0;

        while(rs.next()){
            result += rs.getDouble("PTOTAL");
        }

        statement.close();
        return result;*/
        if(column.equals(Column.HAM)){
            return Math.log((hamM+k) / ((hamM + spamM) + k * 2));
        }
        else{
            return Math.log((spamM+k) / ((hamM + spamM) + k * 2));
        }

    }

    public int getCountAlphabet(Database.Table table) throws SQLException {
        /*Statement statement = connection.createStatement();
        int result=0;
        String query = "SELECT count(*) +  (select sum(times) from "+ table +" ) as totalword " +
                "from ( SELECT word FROM spam  UNION SELECT word FROM ham) as t";

        ResultSet rs= statement.executeQuery(query);
        if(rs.next())
            result = rs.getInt("totalword");

        statement.close();
        return result;*/

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


    /*public double calculateProbability(List<String> words, Table table, double k, int totalWords) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT times from " + table;
        if(!words.isEmpty())
            query += " WHERE word IN (";
        for(String word:words){
            query+= "'"+ word + "',";
        }
        if(!words.isEmpty()) {
            query = query.substring(0,query.length()-1);
            query += ")";
        }
        ResultSet res= statement.executeQuery(query);
        double result = 0;
        while(res.next()) {
            double times = res.getInt("times");
            result += Math.log((times + k)/(totalWords + k * 2));
            //result = res.getDouble(1);
            //result += res.getString("word") + " " + res.getInt("times") + "\n";
        }
        statement.close();
        return result;

    }*/

    public double calculateProbability(List<String> words, Database.Table table, double k, int totalWords) throws SQLException {
        /*Statement statement = connection.createStatement();
        String query = "SELECT (sum(ln(CAST(times + " + k +" as FLOAT)/Cast( ("+totalWords+" + "+k+"*2) as FLOAT)))) FROM "  +table;
        if(!words.isEmpty())
            query += " WHERE word IN (";
        for(String word:words){
            query+= "'"+ word + "',";
        }
        if(!words.isEmpty()) {
            query = query.substring(0,query.length()-1);
            query += ")";
        }
        ResultSet res= statement.executeQuery(query);
        double result = 0;
        while(res.next()) {
            result = res.getDouble(1);
           //result += res.getString("word") + " " + res.getInt("times") + "\n";
        }
        statement.close();
        return result;*/

        // Set<String> wordsSet = new HashSet<>(words);
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
/*
    public int count(Table table) throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery("SELECT COUNT(*) AS COUNT FROM " + table);
        return returnInteger(res, "COUNT");
    }

    public int sum(Table table) throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery("SELECT SUM(times) AS addition FROM " + table);
        return  returnInteger(res, "addition");
    }*/

    /*private int returnInteger(ResultSet res, String label){
        try{
            if(res.next())
                return res.getInt(label);
            else
                return -1;
        }
        catch (Exception e){
            return -1;
        }
    }*/

    /*@Override
    public void closeDB() throws SQLException {
        connection.close();
    }*/

    public HashMap<String, Integer> select(Database.Table table) throws SQLException {
        /*Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery("SELECT * FROM " + table);
        HashMap<String, Integer> result = new HashMap<>();
        while(res.next()) {
            result.put(res.getString("word"), res.getInt("times"));
        }
        statement.close();
        return result;*/
        if(Database.Table.SPAM.equals(table))
            return wordsSpam;
        else
            return wordsHam;
    }

    public Pair<Integer, Integer> selectMessages() throws SQLException{
       /* Statement statement = connection.createStatement();
        ResultSet set = statement.executeQuery("SELECT HAM, SPAM FROM MESSAGE where ID = 1");
        Pair<Integer, Integer> res = null;
        while (set.next()){
            res = new Pair<>(set.getInt("HAM"), set.getInt("SPAM"));
        }
        statement.close();
        return res;*/
        return new Pair<>(hamM, spamM);
    }

    /*public void delete(Table table, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String delete = "DELETE FROM " + table;
        statement.executeUpdate(delete);
        statement.close();
    }
*/


   /* public void insertOrUpdate(MemDB.Table table, HashMap<String, Integer> appearances, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        MemDB.Table complementaryTable = MemDB.Table.HAM;
        if (table == MemDB.Table.HAM)
            complementaryTable = MemDB.Table.SPAM;

        if(appearances.size() > 0) {
            String insert = "MERGE INTO " + table + " using (values";
            for (Map.Entry<String, Integer> element : appearances.entrySet()) {
                insert += "('" + element.getKey() + "'," + element.getValue() + "),";
            }

            insert = insert.substring(0, insert.length() - 1);
            insert += ") as vals(x, y) ON " + table + ".word = vals.x " +
                    "when matched then update set " + table + ".times = " + table + ".times + vals.y " +
                    "when not matched then insert values vals.x, vals.y";

            statement.executeUpdate(insert);

            insertZeroValues(complementaryTable, appearances);
        }
        statement.close();
    }*/

    /*public void insertZeroValues(MemDB.Table table, HashMap<String, Integer> appearances, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        if(appearances.size() > 0) {
            String insert = "MERGE INTO " + table + " using (values";

            for (Map.Entry<String, Integer> element : appearances.entrySet()) {
                insert += "('" + element.getKey() + "'," + 0 + "),";
            }

            insert = insert.substring(0, insert.length() - 1);
            insert += ") as vals(x, y) ON " + table + ".word = vals.x " +
                    "when matched then update set " + table + ".times = " + table + ".times + vals.y " +
                    "when not matched then insert values vals.x, vals.y";

            statement.executeUpdate(insert);
        }
        statement.close();
    }*/

    public void insertCounters(int ham, int spam) throws SQLException {
        /*Statement statement = connection.createStatement();
        String insert = "INSERT INTO " + Table.MESSAGE + "( ID, HAM, SPAM) " +
                " VALUES( 1, "+ ham +","+ spam +")";
        statement.executeUpdate(insert);
        statement.close();*/
        hamM = ham;
        spamM = spam;
    }
}

