package spamizer.MLCore;

import spamizer.entity.Database;

import java.util.Collection;

public interface Method {

    ProbabylityResult getProbabilities(Database database, Collection<String> words);
}
