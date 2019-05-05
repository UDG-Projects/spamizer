package spamizer;

import spamizer.MLCore.Trainer;

import java.sql.SQLException;
import java.util.Random;


public class Application  {

    public static Random random;
    static String   FOLDERPATH = "C:/Users/Gil/Desktop/mails";
    public static void main(String [] args) throws SQLException, ClassNotFoundException {
        random = new Random();
        Trainer trainer = new Trainer("C:/Users/Gil/Desktop/mails","C:/Users/Gil/Desktop/mails");
        trainer.trainning();
    }
}
