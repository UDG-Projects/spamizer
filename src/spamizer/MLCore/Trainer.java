package spamizer.MLCore;

import spamizer.entity.Database;

import java.sql.SQLException;
import java.util.Collection;

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

    public void trainning (){
        Collection<Mail> mailsSpam = reader.read(this.spamDirectoryPath);
        Collection<Mail> mailsHam = reader.read(this.hamDirectoryPath);



    }

}
