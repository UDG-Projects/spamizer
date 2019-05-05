package spamizer.MLCore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static jdk.nashorn.internal.objects.NativeString.toLowerCase;

public class DirectoryMailReader implements Reader{
    List<Mail> mails;
    List<String> splitter;

    public DirectoryMailReader(){
    }
    /**
     * Function to read all files from folder in folderPath, and return a list of Mails.
     * @param folderPath folder path.
     * @return list of mails.
     */
    @Override
    public Collection<Mail> read(String folderPath)
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
                    String data = new String(Files.readAllBytes(Paths.get(folderPath+"\\"+file)));
                    String lowerData = toLowerCase(data).replaceAll("cc|to|from", "");
                    String subject = "";
                    if(lowerData.indexOf("subject:")!=-1){
                        subject = (lowerData.substring(lowerData.indexOf("subject:"), lowerData.indexOf("\n"))).replace("subject:","");
                        lowerData=lowerData.replace("subject:","");
                    }
                    String body = lowerData.replace(subject,"");
                    Mail m = new Mail(subject,body);
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
