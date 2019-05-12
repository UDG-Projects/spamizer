package spamizer;

import spamizer.MLCore.CustomFilter;
import spamizer.MLCore.Mail;
import spamizer.MLCore.NaiveBayes;
import spamizer.MLCore.Validator;
import spamizer.entity.Database;
import spamizer.entity.MemDB;
import spamizer.entity.LocalDB;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestValidation {


    public static void main(String [] args)
    {


        try {
            LocalDB.getInstance();
            MemDB memDB = MemDB.getInstance();


            List<String> spamMessages = new ArrayList<>();
            spamMessages.add("OFFER IS SECRET");
            spamMessages.add("CLICK SECRET LINK");
            spamMessages.add("SECRET SPORTS LINK");

            List<String> hamMessages = new ArrayList<>();
            hamMessages.add("PLAY SPORTS TODAY");
            hamMessages.add("WENT PLAY SPORTS");
            hamMessages.add("SECRET SPORTS EVENT");
            hamMessages.add("SPORTS IS TODAY");
            hamMessages.add("SPORTS COSTS MONEY");


            HashMap<String, Integer> ham = populateMap(hamMessages);
            HashMap<String, Integer> spam = populateMap(spamMessages);

            memDB.insertOrUpdate(Database.Table.HAM, ham);
            memDB.insertOrUpdate(Database.Table.SPAM,spam);

            memDB.updateCounters(hamMessages.size(),spamMessages.size());
            System.out.println("Contadors actuals");
            System.out.println(memDB.selectCounters());
            System.out.println();

            System.out.println("Alfabet");
            int wordsHam = memDB.getCountAlphabet(Database.Table.HAM);
            System.out.println( wordsHam);
            int wordsSpam = memDB.getCountAlphabet(Database.Table.SPAM);
            System.out.println(wordsSpam);
            System.out.println();

            System.out.println("Probabilitats ");
            System.out.println();
            List<String> words = new ArrayList<>();
            words.add("TODAY");
            words.add("SECRET");
            words.add("IS");
            System.out.println("Numerador de paraules HAM sense pTHam ");
            System.out.println(Math.exp(memDB.calculateProbability(words, Database.Table.HAM,1, wordsHam)));
            System.out.println("Numerador de paraules SPAM sense pTSpam ");
            System.out.println(Math.exp(memDB.calculateProbability(words, Database.Table.SPAM,1, wordsSpam)));

            System.out.println();
            System.out.println("pTHam ");
            System.out.println(Math.exp(memDB.getMessageProbabylity(MemDB.Column.HAM,1)));
            System.out.println("pTSpam ");
            System.out.println(Math.exp(memDB.getMessageProbabylity(MemDB.Column.SPAM,1)));
            System.out.println();
            System.out.println("Prova Validator ");
            Validator validator = new Validator(new NaiveBayes());
            List<Mail> mails = new ArrayList<>();
            mails.add(new Mail("This is a shit", "TODAY IS SECRET",false, new CustomFilter()));
            validator.validate(mails,1,1);

            //Thread.sleep(1000);
            LocalDB.getInstance().closeDB();

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
