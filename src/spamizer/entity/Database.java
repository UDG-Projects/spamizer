package spamizer.entity;

import java.sql.*;
import java.util.ArrayList;

public class Database {

    public enum Table {
        SPAM("SPAM"),
        HAM("HAM")
        ;

        private final String table;

        /**
         * @param table
         */
        Table(final String table) {
            this.table = table;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return table;
        }
    }

    private static Database database;
    private static Connection connection;



    private Database(){}

    private static void init() throws ClassNotFoundException, SQLException {
        database = new Database();
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        database.connection = DriverManager.getConnection("jdbc:hsqldb:mem:spamizer_db","spamizer", ""); //"SA", "");

        /**
         * Configurem la base de dades per que s'utilitzi la sintaxis mysql.
         */
        Statement statement = connection.createStatement();
        statement.execute("SET DATABASE SQL SYNTAX MYS TRUE");
        statement.close();

        /** Es creen dues taules degut a que sinó es repeteix la clau primària
         *  Si s'utilitza le booleà queda tot bifurcat, el codi quedarà més senzill si passem el nom de la taula per paràmetre.
         */
        database.createTable(Table.HAM);
        database.createTable(Table.SPAM);

    }

    public static Database getInstance() throws SQLException, ClassNotFoundException {
        if(database == null) {
            database.init();
        }
        return database;
    }

    private void createTable(Table tableName) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE " + tableName + "(word VARCHAR(255) not NULL, times INTEGER, PRIMARY KEY (word))");
        statement.close();
    }

    public void insert(Table table, String word, int times) throws SQLException {
        Statement statement = connection.createStatement();
        String insert = "INSERT INTO " + table + "(word, times) " +
                                     "VALUES('"+ word + "'," + times + ") " +
                                     "ON DUPLICATE KEY UPDATE times = times + " + times;
        statement.executeUpdate(insert);
        statement.close();


        /*
        String query = "INSERT INTO " + table + "(word, times) VALUES ";
        for(String values : words){
            query +="('"+ values + "'," + times + "),";
        }
        // Li trec la última coma.
        query = query.substring(0, query.length() -1) + " ";
        query += "ON DUPLICATE KEY UPDATE times = times + 1";
         */
    }

    public String select(Table table) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery("SELECT * FROM " + table);
        statement.close();
        String result = "";
        while(res.next()) {
            result += res.getString("word") + " " + res.getInt("times") + "\n";
        }
        return result;

    }
}

