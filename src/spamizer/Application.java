package spamizer;
import spamizer.MLCore.StanfordCoreNLPFilter;
import spamizer.entity.Database;
import spamizer.entity.LocalDB;

import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Application  {

    public static Random random;

    public static void main(String [] args)
    {
        random = new Random();

        try {
            LocalDB localDB = LocalDB.getInstance();
            localDB.insertResult(2,3,1,0,1,0);
            Database database = Database.getInstance();

            /*List<Pair<String, Integer>> values = new ArrayList<>();
            values.add(new Pair<>("hola", 1));
            values.add(new Pair<>("adeu", 2));

            database.insertOrUpdate(Database.Table.HAM, values);
            System.out.println(database.select(Database.Table.HAM));

            values.add(new Pair<>("hola", 1));
            values.add(new Pair<>("adeu", 2));

            database.insertOrUpdate(Database.Table.HAM, values);
            System.out.println(database.select(Database.Table.HAM));*/

            // Genero 1000 paraules diferents, simulem que és l'alfabet

            Instant start = Instant.now();
            System.out.println("Started word generation. ");
            String[] wordsValues = new String[1000];
            for(int i = 0; i < 1000; i++){
                wordsValues[i] = generateRandomString();
            }
            Instant end = Instant.now();
            System.out.println("Finished generation done in " + millisToString(ChronoUnit.MILLIS.between(start, end)));

            start = Instant.now();
            System.out.println("Started insertion");
            // Generem 10000 correus que tindran una mitjana aleatòria de fins a 300 paraules cada correu i fem les insercions
            for(int i = 0; i < 1000; i++){
                HashMap<String, Integer> email = new HashMap();
                int wordsForEmail = Math.abs(random.nextInt()) % 300;
                for(int j = 0; j < wordsForEmail; j++) {
                    int randomPosition = Math.abs(random.nextInt()) % 1000;
                    String word = wordsValues[randomPosition];
                    if(email.containsKey(wordsValues[randomPosition])){
                        email.replace(word, email.get(word) + 1);
                    }
                    else
                        email.put(word, 1);
                }
                database.insertOrUpdate(Database.Table.HAM, email);
            }
            end = Instant.now();
            System.out.println("Finished insertion done in " + millisToString(ChronoUnit.MILLIS.between(start, end)));

            start = Instant.now();
            System.out.println("Started selection");
            System.out.println(database.sum(Database.Table.HAM));
            end = Instant.now();
            System.out.println("Selection done in " + millisToString(ChronoUnit.MILLIS.between(start, end)));


            /*String s = "dear vince ,\n" +
                    "thank you very much for updating me on the status of my job application . ? i\n" +
                    "got another good news last week . ? i am happy to inform you i passed the 2000\n" +
                    "cfa level i examination . the pass rate for level i examination this year is\n" +
                    "52 % . i look forward to hearing from you .\n" +
                    "sincerely ,\n" +
                    "rabi de\n" +
                    "?\n" +
                    "?\n" +
                    "? vince . j . kaminski @ enron . com wrote :\n" +
                    "rabi ,\n" +
                    "thanks for your MESSAGE .\n" +
                    "everybody who interviewed you was greatly impressed with your technical\n" +
                    "skills and professional attitude .\n" +
                    "we shall extend an offer to you within a day or two .\n" +
                    "vince\n" +
                    "rabi deon 08 / 22 / 2000 02 : 57 : 37 pm\n" +
                    "to : vince kaminsky\n" +
                    "cc :\n" +
                    "subject : Follow - up interview on 8 / 21 / 00\n" +
                    "dear dr . kaminsky :\n" +
                    "thank you very much for arranging the follow - up interview with your\n" +
                    "internal clients . i visited mr . ted murphy and his staff at rac and mr .\n" +
                    "dennis benevides at ees yesterday . i was impressed with level of risk\n" +
                    "technology employed by enron to achieve its business objectives . i want to\n" +
                    "reiterate my strong interest in joining your group , which is held in very\n" +
                    "high esteem both inside and outside of enron . ? i look forward to h ! earing\n" +
                    "from you .\n" +
                    "sincerely ,\n" +
                    "rabi s . de\n" +
                    "do you yahoo ! ?\n" +
                    "yahoo ! mail - free email you can access from anywhere !\n" +
                    "do you yahoo ! ?\n" +
                    "yahoo ! mail - free email you can access from anywhere !";

            StanfordCoreNLPFilter filter = new StanfordCoreNLPFilter();

            HashMap<String, Integer> words = filter.filterText(s);

            for(Map.Entry<String, Integer> word : words.entrySet()){
                System.out.println(word.getKey() + " -> " + word.getValue());
            }*/
            System.out.println("Selection done in " + millisToString(ChronoUnit.MILLIS.between(start, end)));
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

    public static String generateRandomString(){
        int length = Math.abs(random.nextInt()) % 10 + 1;

        char[] array = new char[length]; // length is bounded by 7
        for(int i = 0; i< length; i++){
            array[i] = (char) (Math.abs(random.nextInt()) % 26 + 97);
        }
        return new String(array);
    }

    public static Integer generateRandomInteger(){
        return Math.abs(random.nextInt() % 5 + 1);
    }

    public static String millisToString(long millis){
        return String.format("%d sec, %d millis",
                TimeUnit.MILLISECONDS.toSeconds(millis),
                TimeUnit.MILLISECONDS.toMillis(millis) -
                        TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis))
        );
    }

}
