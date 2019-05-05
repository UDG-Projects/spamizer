package spamizer;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.scene.control.Tab;
import spamizer.entity.Database;
import spamizer.entity.LocalDB;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TestValidation {


    public static void main(String [] args)
    {


        try {
            LocalDB localDB = LocalDB.getInstance();
            localDB.insertResult(2,3,1,0,1,0);
            Database database = Database.getInstance();


            List<String> spamMessages = new ArrayList<>();
            spamMessages.add("OFFER IS SECRET");
            spamMessages.add("CLICK SECRET LINK");
            spamMessages.add("SECRET SPORTS LINK");

            List<String> hamMessages = new ArrayList<>();
            hamMessages.add("PLAY SPORTS TODAY");
            hamMessages.add("WENT PLAY SPORTS");
            hamMessages.add("SECRET SPORTS EVENT");
            hamMessages.add("SPORT IS TODAY");
            hamMessages.add("SPORT COSTS MONEY");


            HashMap<String, Integer> ham = populateMap(hamMessages);
            HashMap<String, Integer> spam = populateMap(spamMessages);

            database.insertOrUpdate(Database.Table.HAM, ham);
            database.insertOrUpdate(Database.Table.SPAM,spam);

            database.updateCounters(hamMessages.size(),spamMessages.size());
            System.out.println("Contadors actuals");
            System.out.println(database.selectCounters());

            System.out.println("Probabilitats");
            List<String> words = new ArrayList<>();
            words.add("SPORTS");
            words.add("SECRET");
            words.add("PLAY");
            System.out.println(database.calculateProbability(words, Database.Table.HAM));

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Falta el driver i possiblement afegir el jar com a llibreria del projecte.");
            System.out.println("Consulta el README.md i segueix els passos descrits.");
            System.out.println("Happy coding!");
            e.printStackTrace();
        }
    }


    public static HashMap<String,Integer> populateMap(List<String> messages) {
        HashMap<String, Integer> result = new HashMap();
        for (String mail : messages) {
            for (String word : mail.split(" ")) {
                if (result.containsKey(word))
                    result.replace(word, result.get(word) + 1);
                else
                    result.put(word, 1);
            }
        }
        return result;
    }

}
