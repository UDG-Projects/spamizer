package spamizer.interfaces;

import spamizer.MLCore.Mail;

import java.util.Collection;

public interface Reader {

    public Collection<Mail> read(boolean isSpam);
}
