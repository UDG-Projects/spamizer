package spamizer.MLCore;

import spamizer.entity.Database;
import spamizer.entity.MemDB;
import spamizer.interfaces.Filter;
import spamizer.interfaces.Reader;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

public class Trainer {

    protected MemDB memoryDataBase;

    public Trainer() throws SQLException, ClassNotFoundException {
        memoryDataBase = MemDB.getInstance();
    }

    public void train (MemDB.Table table, Reader reader, Filter filter) throws SQLException {
        Collection<Mail> mailsFiltrats = reader.read(MemDB.Table.SPAM == table);
        train(table, mailsFiltrats,filter);
    }

    public void train(MemDB.Table table, Collection<Mail> mails, Filter filter) throws SQLException {
        HashMap<String,Integer> mapinsertMailFiltered = new HashMap<>();
        int counter = 0;
        for (Mail m : mails)
        {
            m.setFilter(filter);
            if(!mapinsertMailFiltered.isEmpty()){
                m.getBodyMail().forEach(
                        (key, value) -> mapinsertMailFiltered.merge( key, value, (v1, v2) -> v1+v2));
            }else{
                mapinsertMailFiltered.putAll(m.getBodyMail());
            }
            System.out.println(counter);
            counter++;
        }
        if(table.equals(Database.Table.HAM))
            memoryDataBase.updateCounters(counter,0);
        else {
            memoryDataBase.updateCounters(0,counter);
        }
        memoryDataBase.insertOrUpdate(table,mapinsertMailFiltered);
    }



}
