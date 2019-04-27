package spamizer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


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
            System.out.println(c.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
