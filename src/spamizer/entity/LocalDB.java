package spamizer.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class LocalDB {

    public enum Table {
        RESULTS("RESULT")
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

    private static LocalDB database;
    private static Connection connection;

    private LocalDB(){}

    private static void init() throws ClassNotFoundException, SQLException {
        database = new LocalDB();
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        database.connection = DriverManager.getConnection("jdbc:hsqldb:file:db/result_db","spamizer", "spamizer");

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
        statement.close();
    }

}
