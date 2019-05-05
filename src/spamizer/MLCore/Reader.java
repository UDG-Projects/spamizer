package spamizer.MLCore;

import java.util.Collection;

public interface Reader {

    public Collection<Mail> read(String pathFolder);
}
