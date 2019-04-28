package spamizer;
import spamizer.entity.Database;

import java.sql.*;
import java.util.ArrayList;


public class Application  {

    public static void main(String [] args)
    {
        try {

            Database database = Database.getInstance();

            /*ArrayList<String> words = new ArrayList<>();
            words.add("un");
            words.add("un");
            words.add("un");
            words.add("un");
            words.add("un");
            words.add("dos");
            words.add("dos");
            words.add("tres");
            database.insert(Database.Table.HAM, words, 1);*/

            database.insert(Database.Table.HAM,"tres", 1);
            database.insert(Database.Table.HAM,"un", 4);
            database.insert(Database.Table.SPAM,"tres", 1);
            database.insert(Database.Table.HAM,"un", 1);
            database.insert(Database.Table.HAM,"un", 1);
            database.insert(Database.Table.SPAM,"dos", 1);
            System.out.println(database.select(Database.Table.HAM));

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Falta el driver i possiblement afegir el jar com a llibreria del projecte.");
            System.out.println("Consulta el README.md i segueix els passos descrits.");
            System.out.println("Happy coding!");
            e.printStackTrace();
        }
    }

}
