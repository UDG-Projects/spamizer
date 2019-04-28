package spamizer.entity;

import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    private void insertOrUpdate(Statement statement, Table table, String word, int times) throws SQLException {

        // En la següent query es fa un insert or update
        String insert = "MERGE INTO " + table + " AS t USING (" +
                            "VALUES('"+ word + "'," + times + ")" +
                        ") AS vals(word, times) ON t.WORD=VALS.word " +
                        "WHEN MATCHED THEN UPDATE SET t.times=t.times+vals.times " +
                        "WHEN NOT MATCHED THEN INSERT VALUES vals.word, vals.times";
        statement.executeUpdate(insert);

    }

    public void insertOrUpdate(Table table, List<Pair<String, Integer>> appearances) throws SQLException {
        Statement statement = connection.createStatement();
        for(Pair<String, Integer> element : appearances){
            insertOrUpdate(statement, table, element.getKey(), element.getValue());
        }
        statement.close();
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

