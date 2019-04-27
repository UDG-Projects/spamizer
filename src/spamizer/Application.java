package spamizer;
import java.sql.*;


public class Application  {

    public static void main(String [] args)
    {
        System.out.println("Hello world");

        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver" );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Instal·la el driver per HSQLDB des del mateix idea.");
        }

        try {
            Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:spamizer_db", "SA", "");
            Statement statement = c.createStatement();

            String sqlCreateTable = "CREATE TABLE HISTOGRAMA(word VARCHAR(255) not NULL, times INTEGER, is_ham BOOLEAN, PRIMARY KEY (word))";
            statement.executeUpdate(sqlCreateTable);

            statement.executeUpdate("INSERT INTO HISTOGRAMA(word, times, is_ham) values('hola',1,1)");
            ResultSet res = statement.executeQuery("SELECT * FROM HISTOGRAMA");
            while(res.next()){
                System.out.println(res.getString("word") + " " + res.getInt("times") + " " + res.getBoolean("is_ham"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
