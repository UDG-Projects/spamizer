package spamizer.MLCore;

import spamizer.entity.Mail;
import spamizer.entity.MemDB;
import spamizer.entity.TableEnumeration;
import spamizer.interfaces.Filter;
import spamizer.interfaces.Reader;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;

public class Trainer {

    protected MemDB memoryDataBase;
    protected Instant start;
    protected Instant end;


    public Trainer() {
        memoryDataBase = MemDB.getInstance();
    }

    /**
     * Creates an instant of trainer object
     * @param table Defines class for mail training [HAM or SPAM]
     * @param reader Object used to read from source
     * @param filter Text Cleaner to delete characters not used to spam filter
     */
    public void train (TableEnumeration.Table table, Reader reader, Filter filter)  {
        Collection<Mail> filteredMails = reader.read(TableEnumeration.Table.SPAM == table, filter);
        train(table, filteredMails,filter);
    }

    /**
     * Trainer process to insert all mails into memory database
     * @param table Defines class for mail training [HAM or SPAM]
     * @param mails To be inserted
     * @param filter To clean mails
     */
    public void train(TableEnumeration.Table table, Collection<Mail> mails, Filter filter) {
        start = Instant.now();
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
            if(counter % 500 == 0 && counter > 0) {
                System.out.println("Readed and filtered " + counter + " messages.");
            }
            counter++;
        }
        if(table.equals(TableEnumeration.Table.HAM))
            memoryDataBase.updateCounters(counter,0);
        else {
            memoryDataBase.updateCounters(0,counter);
        }
        memoryDataBase.insertOrUpdate(table,mapinsertMailFiltered);
        end = Instant.now();
    }

    public long getExecutionMillis(){
        return ChronoUnit.MILLIS.between(start, end);
    }



}
