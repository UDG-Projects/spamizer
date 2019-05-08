package spamizer.MLCore;

import spamizer.entity.Database;
import spamizer.entity.MemDB;
import spamizer.entity.Result;
import spamizer.interfaces.Method;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NaiveBayes implements Method {


    @Override
    public boolean isSpam(MemDB memDB, Collection<String> message, double k, double phi) throws SQLException {
        double spamProbability=0;
        double hamProbability=0;

        int hamAlphabet = memDB.getCountAlphabet(Database.Table.HAM);
        int spamAlphabet = memDB.getCountAlphabet(Database.Table.SPAM);

        List<String> words = new ArrayList<>(message);
        hamProbability = memDB.calculateProbability(words, Database.Table.HAM,k,hamAlphabet);
        spamProbability = memDB.calculateProbability(words, Database.Table.SPAM,k,spamAlphabet);

        double pTotalSpam = memDB.getMessageProbabylity(MemDB.Column.SPAM,k);
        double pTotalHam = memDB.getMessageProbabylity(MemDB.Column.HAM,k);

        //Tenim la probatilitat sumada de cada una de les paraules del missatge en forma de logaritme
        //sumem les probatilitats de que els missatges siguin spam o ham en forma de logaritme
        hamProbability+= pTotalHam;
        spamProbability += pTotalSpam;

        //comparem les probabilitats obtingudes aplicant el parametre phi.
        return Math.exp(spamProbability) > (Math.exp(hamProbability)*phi);
    }
}
