package spamizer.interfaces;

import spamizer.MLCore.Mail;

import java.util.Collection;

public interface Selector {



    Collection<Mail> getUnknown();
    Collection<Mail> getHam();
    Collection<Mail> getSpam();

}
