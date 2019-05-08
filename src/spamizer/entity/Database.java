package spamizer.entity;

import javafx.util.Pair;
import org.omg.PortableInterceptor.INACTIVE;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public abstract class Database {

    public enum Table {
        RESULTS("RESULT"),
        SPAM("SPAM"),
        HAM("HAM"),
        MESSAGE("MESSAGE")
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


    public abstract void closeDB() throws SQLException;

    public abstract void insertOrUpdate(Database.Table table, HashMap<String, Integer> appearances)  throws SQLException;
    public abstract void insertZeroValues(Database.Table table, HashMap<String, Integer> appearances) throws SQLException;
    public abstract void delete(Database.Table table) throws SQLException;
    public abstract HashMap<String, Integer> select(Database.Table table) throws SQLException;
    public abstract Pair<Integer, Integer> selectMessages() throws SQLException;
    public abstract void insertCounters(int ham, int spam) throws SQLException;

    private void insertOrUpdate(Statement statement, Table table, String word, int times) throws SQLException {

        // En la següent query es fa un insert or update
/*        String insert = "MERGE INTO " + table + " AS t USING (" +
                            "VALUES('"+ word + "'," + times + ")" +
                        ") AS vals(word, times) ON t.WORD=VALS.word " +
                        "WHEN MATCHED THEN UPDATE SET t.times=t.times+vals.times " +
                        "WHEN NOT MATCHED THEN INSERT VALUES vals.word, vals.times";*/

        String insert = "INSERT INTO " + table + "(word, times) " +
                "VALUES ('" + word + "'," + times + ") as times" +
                "ON DUPLICATE KEY UPDATE times = times + " + times;

        statement.executeUpdate(insert);

    }

    public HashMap<String, Integer> select(Table table, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery("SELECT * FROM " + table);
        HashMap<String, Integer> result = new HashMap<>();
        while(res.next()) {
            result.put(res.getString("word"), res.getInt("times"));
        }
        statement.close();
        return result;
    }

    public Pair<Integer, Integer> selectMessages(Connection connection) throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet set = statement.executeQuery("SELECT HAM, SPAM FROM MESSAGE where ID = 1");
        Pair<Integer, Integer> res = null;
        while (set.next()){
            res = new Pair<>(set.getInt("HAM"), set.getInt("SPAM"));
        }
        statement.close();
        return res;
    }

    public void delete(Table table, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String delete = "DELETE FROM " + table;
        statement.executeUpdate(delete);
        statement.close();
    }



    public void insertOrUpdate(Database.Table table, HashMap<String, Integer> appearances, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        Database.Table complementaryTable = Database.Table.HAM;
        if (table == Database.Table.HAM)
            complementaryTable = Database.Table.SPAM;

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
    }

    public void insertZeroValues(Database.Table table, HashMap<String, Integer> appearances, Connection connection) throws SQLException {
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
    }

    public void insertCounters(Connection connection, int ham, int spam) throws SQLException {
        Statement statement = connection.createStatement();
        String insert = "INSERT INTO " + Table.MESSAGE + "( ID, HAM, SPAM) " +
                " VALUES( 1, "+ ham +","+ spam +")";
        statement.executeUpdate(insert);
        statement.close();
    }


}
