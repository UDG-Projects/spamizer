package spamizer.interfaces;

import spamizer.entity.MemDB;

import java.sql.SQLException;
import java.util.Collection;

public interface Method {

    /**
     *
     * @param memDB
     * @param message Collection of strings of the message to be calssificated
     * @param k
     * @return true if message is classificated as spam
     */
    boolean isSpam(MemDB memDB, Collection<String> message, double k, double phi) ;
}
