package spamizer.interfaces;

import spamizer.MLCore.ProbabylityResult;
import spamizer.entity.Database;

import java.util.Collection;

public interface Method {

    /**
     *
     * @param database
     * @param message Collection of strings of the message to be calssificated
     * @param k
     * @return true if message is classificated as spam
     */
    boolean isSpam(Database database, Collection<String> message, int k, int phi);
}
