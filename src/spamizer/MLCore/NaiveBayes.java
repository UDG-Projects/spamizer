package spamizer.MLCore;

import spamizer.entity.Database;
import spamizer.interfaces.Method;

import java.util.Collection;

public class NaiveBayes implements Method {

    @Override
    public ProbabylityResult getProbabilities(Database database, Collection<String> words) {
        ProbabylityResult result= new ProbabylityResult();




        return result;
    }
}
