package spamizer.MLCore;

import spamizer.interfaces.Filter;
import spamizer.interfaces.Reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static jdk.nashorn.internal.objects.NativeString.toLowerCase;

public class DirectoryMailReader implements Reader {

    String folderPath;

    public DirectoryMailReader(String folderPath){
        if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
            this.folderPath = folderPath + "\\";
        else
            this.folderPath = folderPath + "/";
    }
    /**
     * Function to read all files from folder in folderPath, and return a list of Mails.
     * @return list of mails.
     */
    @Override
    public Collection<Mail> read(boolean isSpam, Filter filter)
    {
        List<Mail> mails = new ArrayList<Mail>();

        File folder = new File(folderPath);
        //File[] listOfFiles = folder.listFiles();

        List<File> listOfFiles = Arrays.asList(folder.listFiles());
        Collections.shuffle(listOfFiles);

        Iterator<File> it = listOfFiles.iterator();

        //for (int i = 0; i < listOfFiles.length; i++) {
        while(it.hasNext()){
            File f = it.next();
            if (f.isFile() && !f.getName().equals(".DS_Store")) {
                File file = null;
                FileReader fileReader = null;
                BufferedReader br = null;
                try {
                    file = new File (f.getName());

                    String data = new String(Files.readAllBytes(Paths.get(folderPath + file)),  "US-ASCII");
                    String lowerData = toLowerCase(data).replaceAll("cc|to|from", "");
                    Mail m = new Mail(f.getName(), lowerData, isSpam, new CustomFilter());
                    mails.add(m);
                }
                catch(Exception e){
                    e.printStackTrace();
                }finally{
                    try{
                        if( null != fileReader ){
                            fileReader.close();
                        }
                    }catch (Exception e2){
                        e2.printStackTrace();
                    }
                }
            }
        }
        return mails;
    }

}
