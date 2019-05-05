package spamizer.MLCore;

import spamizer.entity.Database;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class Trainer {

    protected Reader reader;
    protected String hamDirectoryPath;
    protected String spamDirectoryPath;
    protected Database memoryDataBase;

    public Trainer(String hamDirectoryPath, String spamDirectoryPath) throws SQLException, ClassNotFoundException {
        //TODO: Instanciar el reader

        //si el constructor no pot inicialitzar la DB fa un Throw cap a fora.
        memoryDataBase = Database.getInstance();

        this.hamDirectoryPath = hamDirectoryPath;
        this.spamDirectoryPath = spamDirectoryPath;
    }

}
