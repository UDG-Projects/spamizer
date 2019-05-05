package spamizer.MLCore;

import spamizer.entity.Database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

public class Trainer {

    protected Reader reader;
    protected String hamDirectoryPath;
    protected String spamDirectoryPath;
    protected Database memoryDataBase;

    public Trainer(String hamDirectoryPath, String spamDirectoryPath) throws SQLException, ClassNotFoundException {
        reader = new DirectoryMailReader();
        //si el constructor no pot inicialitzar la DB fa un Throw cap a fora.
        memoryDataBase = Database.getInstance();
        this.hamDirectoryPath = hamDirectoryPath;
        this.spamDirectoryPath = spamDirectoryPath;

    }

    public void trainning () throws SQLException {
        trainningSpam(Database.Table.SPAM,spamDirectoryPath);
    }
    private void trainningSpam(Database.Table table,String pathDirectory) throws SQLException {
        Collection<Mail> mailsFiltrats = reader.read(this.spamDirectoryPath);
        HashMap<String,Integer> mapinsertMailFiltered = new HashMap<>();
        for (Mail m : mailsFiltrats)
        {
            m.setFilter(new StanfordCoreNLPFilter());
            if(!mapinsertMailFiltered.isEmpty()){
                m.getBodyMail().forEach(
                        (key, value) -> mapinsertMailFiltered.merge( key, value, (v1, v2) -> v1+v2));
            }else{
                mapinsertMailFiltered.putAll(m.getBodyMail());
            }

        }
        System.out.println(mapinsertMailFiltered);
        memoryDataBase.insertOrUpdate(table,mapinsertMailFiltered);
    }

}
