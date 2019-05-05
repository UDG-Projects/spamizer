package spamizer.MLCore;

import spamizer.entity.Database;
import spamizer.interfaces.Method;

import java.util.Collection;

public class NaiveBayes implements Method {

    @Override
    public boolean isSpam(Database database, Collection<String> message, int k, int phi) {
        double spamProbability=0;
        double hamProbability=0;




        //comparem les probabilitats obtingudes aplicant el parametre phi.
        return spamProbability > (hamProbability*phi);
    }
}
