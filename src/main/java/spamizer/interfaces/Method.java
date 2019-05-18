package main.java.spamizer.interfaces;

import main.java.spamizer.entity.MemDB;

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
