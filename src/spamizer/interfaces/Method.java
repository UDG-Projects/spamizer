package spamizer.interfaces;

import spamizer.MLCore.ProbabylityResult;
import spamizer.entity.Database;

import java.util.Collection;

public interface Method {

    ProbabylityResult getProbabilities(Database database, Collection<String> words);
}
