package main.java.spamizer.interfaces;

import main.java.spamizer.entity.Mail;

import java.util.Collection;

public interface Reader {

    public Collection<Mail> read(boolean isSpam, Filter filter);
}
