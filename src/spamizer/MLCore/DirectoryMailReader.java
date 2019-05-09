package spamizer.MLCore;

import spamizer.interfaces.Reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public Collection<Mail> read(boolean isSpam)
    {
        List<Mail> mails = new ArrayList<Mail>();
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                File file = null;
                FileReader fileReader = null;
                BufferedReader br = null;
                try {
                    file = new File (listOfFiles[i].getName());
                    String data = new String(Files.readAllBytes(Paths.get(folderPath + file)), UTF_8);
                    String lowerData = toLowerCase(data).replaceAll("cc|to|from", "");
                    String subject = "";
                    if(lowerData.indexOf("subject:")!=-1){
                        subject = (lowerData.substring(lowerData.indexOf("subject:"), lowerData.indexOf("\n"))).replace("subject:","");
                        lowerData=lowerData.replace("subject:","");
                    }
                    String body = lowerData.replace(subject,"");//.replaceAll("([^A-Za-z0-9_@-])", " ");;
                    Mail m = new Mail(subject,body, isSpam);
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
