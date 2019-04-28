package spamizer;
import javafx.util.Pair;
import spamizer.entity.Database;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class Application  {

    public static Random random;

    public static void main(String [] args)
    {
        random = new Random();

        try {
            Database database = Database.getInstance();
            // Genero 1000 paraules diferents, simulem que és l'alfabet

            Instant start = Instant.now();
            System.out.println("Started word generation. ");
            String[] words = new String[1000];
            for(int i = 0; i < 1000; i++){
                words[i] = generateRandomString();
            }
            Instant end = Instant.now();
            System.out.println("Finished generation done in " + millisToString(ChronoUnit.MILLIS.between(start, end)));

            start = Instant.now();
            System.out.println("Started insertion");
            // Generem 10000 correus que tindran una mitjana aleatòria de fins a 300 paraules cada correu i fem les insercions
            for(int i = 0; i < 10000; i++){
                List<Pair<String, Integer>> email = new ArrayList<>();
                int wordsForEmail = Math.abs(random.nextInt()) % 300;
                for(int j = 0; j < wordsForEmail; j++) {
                    int randomPosition = Math.abs(random.nextInt()) % 1000;
                    email.add(new Pair<>(words[randomPosition], generateRandomInteger()));
                }
                database.insertOrUpdate(Database.Table.HAM, email);
            }
            end = Instant.now();
            System.out.println("Finished insertion done in " + millisToString(ChronoUnit.MILLIS.between(start, end)));

            start = Instant.now();
            System.out.println("Started selection");
            database.select(Database.Table.HAM);
            end = Instant.now();
            System.out.println("Selection done in " + millisToString(ChronoUnit.MILLIS.between(start, end)));

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
