package spamizer.entity;

import javafx.util.Pair;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class LocalDB extends Database {



    private static LocalDB database;
    protected static Connection connection;

    private LocalDB(){}

    private static void init() throws ClassNotFoundException, SQLException {
        database = new LocalDB();
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        database.connection = DriverManager.getConnection("jdbc:hsqldb:file:db/result_db;shutdown=true;hsqldb.write_delay=false;","spamizer", "spamizer");

        /**
         * Configurem la base de dades per que s'utilitzi la sintaxis mysql.
         */
        Statement statement = connection.createStatement();
        statement.execute("SET DATABASE SQL SYNTAX MYS TRUE");
        statement.close();
    }

    public static LocalDB getInstance() throws SQLException, ClassNotFoundException {
        if(database == null) {
            database.init();
        }
        return database;
    }

    public void insertResult(double phi, double k, int tp, int tn, int fp, int fn) throws SQLException {
        Statement statement = connection.createStatement();
        String insert = "INSERT INTO "+ Table.RESULTS + "(phi,k,true_positive,true_negative,false_positive,false_negative) " +
                "VALUES (" + phi + "," + k + "," + tp + "," + tn + "," + fp + "," + fn + ")";

        statement.executeUpdate(insert);
        statement.closeOnCompletion();
    }


    public void delete(Database.Table table) throws SQLException {
        delete(table, connection);
    }

    @Override
    public HashMap<String, Integer> select(Database.Table table) throws SQLException {
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


    public void closeDB() throws SQLException {
        while(!connection.isClosed()){
            //wait for ending
            connection.commit();
            connection.close();
        }
    }

    @Override
    public void insertOrUpdate(Database.Table table, HashMap<String, Integer> appearances) throws SQLException {
        insertOrUpdate(table, appearances, connection);
    }

    @Override
    public void insertZeroValues(Database.Table table, HashMap<String, Integer> appearances) throws SQLException {
        insertZeroValues(table, appearances, connection);
    }


}
