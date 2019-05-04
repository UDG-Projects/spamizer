package spamizer.MLCore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MailReader implements Reader{
    private Mail mail;

    @Override
    public String read(String folderPath)
    {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                File file = null;
                FileReader fileReader = null;
                BufferedReader br = null;
                try {
                    file = new File (listOfFiles[i].getName());
                    fileReader = new FileReader (file);
                    br = new BufferedReader(fileReader);
                    String mail="";
                    String subject;
                    String body;
                    String line;
                    while((line=br.readLine())!=null){
                        //TODO: AQUI ÉS ON ES FILTREN LES PARAULES

                    }
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


    }
}
