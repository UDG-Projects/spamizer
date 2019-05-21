package main.java.spamizer.interfaces;

import main.java.spamizer.entity.Mail;

import java.util.Collection;

public interface Selector {



    Collection<Mail> getUnknown();
    Collection<Mail> getHam();
    Collection<Mail> getSpam();

}
