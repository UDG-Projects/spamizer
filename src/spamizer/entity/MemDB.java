package spamizer.entity;

import javafx.util.Pair;

import java.sql.*;
import java.util.*;

public class MemDB extends Database {


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
    private static Connection connection;


    private MemDB(){}

    private static void init() throws ClassNotFoundException, SQLException {
        memDB = new MemDB();
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        memDB.connection = DriverManager.getConnection("jdbc:hsqldb:mem:spamizer_db","spamizer", ""); //"SA", "");

        /**
         * Configurem la base de dades per que s'utilitzi la sintaxis mysql.
         */
        Statement statement = connection.createStatement();
        statement.execute("SET DATABASE SQL SYNTAX MYS TRUE");
        statement.close();

        /** Es creen dues taules degut a que sinó es repeteix la clau primària
         *  Si s'utilitza le booleà queda tot bifurcat, el codi quedarà més senzill si passem el nom de la taula per paràmetre.
         */
        memDB.createTable(Table.HAM);
        memDB.createTable(Table.SPAM);
        memDB.createTable(Table.MESSAGE,"(id INTEGER, ham integer, spam integer)");
        //inserir els primers contadors
        memDB.insertCounters(0,0);

    }

    public static MemDB getInstance() throws SQLException, ClassNotFoundException {
        if(memDB == null) {
            memDB.init();
        }
        return memDB;
    }

    private void createTable(Table tableName) throws SQLException {
       createTable(tableName, "(word VARCHAR(255) not NULL, times INTEGER, PRIMARY KEY (word))");
    }

    private void createTable(Table tableName, String columnQuery) throws SQLException{
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE " + tableName + columnQuery);
        statement.close();
    }


    @Override
    public void insertOrUpdate(Database.Table table, HashMap<String, Integer> appearances) throws SQLException {
        insertOrUpdate(table, appearances, connection);
    }

    @Override
    public void insertZeroValues(Database.Table table, HashMap<String, Integer> appearances) throws SQLException {
        insertZeroValues(table, appearances, connection);
    }

    @Override
    public void delete(Table table) throws SQLException {
        delete(table, connection);
    }

    @Override
    public HashMap<String, Integer> select(Table table) throws SQLException {
        return select(table, connection);
    }

    @Override
    public Pair<Integer, Integer> selectMessages() throws SQLException {
        return selectMessages(connection);
    }

    @Override
    public void insertCounters(int ham, int spam) throws SQLException {
        insertCounters(connection, ham, spam);
    }

    public void updateCounters(int ham, int spam) throws SQLException {
        Statement statement = connection.createStatement();
        String update ="UPDATE " + Table.MESSAGE +
                " SET HAM = HAM +" + ham + ","+
                "    SPAM = SPAM +" + spam +
                " WHERE ID = 1";
        statement.executeUpdate(update);
        statement.close();
    }


    public String selectCounters() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery("SELECT * FROM " + Table.MESSAGE);
        String result = "";
        while(res.next()) {
            result += res.getInt("HAM") + " " + res.getInt("SPAM") + "\n";
        }
        statement.close();
        return result;
    }

    public double getMessageProbabylity(Column column, double k) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "Select ln( CAST((" + column + " + " + k + ") as DOUBLE)/CAST(((HAM + SPAM) + " + k*2 +") as DOUBLE)) as PTOTAL from " +Table.MESSAGE + " where id = 1 ";

        ResultSet rs = statement.executeQuery(query);
        float    result=0;

        while(rs.next()){
            result += rs.getDouble("PTOTAL");
        }

        statement.close();
        return result;
    }

    public int getCountAlphabet(Table table) throws SQLException {
        Statement statement = connection.createStatement();
        int result=0;
        String query = "SELECT count(*) +  (select sum(times) from "+ table +" ) as totalword " +
                "from ( SELECT word FROM spam  UNION SELECT word FROM ham) as t";

        ResultSet rs= statement.executeQuery(query);
        if(rs.next())
            result = rs.getInt("totalword");

        statement.close();
        return result;
    }

    public double calculateProbability(List<String> words, Table table, double k, int totalWords) throws SQLException {
        Statement statement = connection.createStatement();
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
        return result;

    }

    public int count(Table table) throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery("SELECT COUNT(*) AS COUNT FROM " + table);
        return returnInteger(res, "COUNT");
    }

    public int sum(Table table) throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery("SELECT SUM(times) AS addition FROM " + table);
        return  returnInteger(res, "addition");
    }

    private int returnInteger(ResultSet res, String label){
        try{
            if(res.next())
                return res.getInt(label);
            else
                return -1;
        }
        catch (Exception e){
            return -1;
        }
    }

    @Override
    public void closeDB() throws SQLException {
        connection.close();
    }
}

