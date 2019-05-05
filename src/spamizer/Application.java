package spamizer;

import spamizer.MLCore.DirectoryMailReader;
import spamizer.MLCore.Mail;
import spamizer.MLCore.Reader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;


public class Application  {

    public static Random random;
    static String   FOLDERPATH = "C:/Users/Gil/Desktop/mails";
    public static void main(String [] args) {
        random = new Random();
        HashMap<String,Integer> mailFiltrat = new HashMap<>();
        Reader mailreader = new DirectoryMailReader();
        Collection<Mail> mailsFiltrats = mailreader.read("C:\\Users\\Gil\\Desktop\\mails");
        for (Mail m : mailsFiltrats)
        {
          mailFiltrat.putAll(m.getBodyMail());
        }

        System.out.println("HELLOOO");
    }
}
