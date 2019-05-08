package spamizer.MLCore;

import spamizer.entity.MemDB;
import spamizer.entity.Result;
import spamizer.interfaces.Method;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NaiveBayes implements Method {


    @Override
    public boolean isSpam(MemDB memDB, Collection<String> message, int k, int phi) throws SQLException {
        double spamProbability=0;
        double hamProbability=0;

        int hamAlphabet = memDB.getCountAlphabet(MemDB.Table.HAM);
        int spamAlphabet = memDB.getCountAlphabet(MemDB.Table.SPAM);

        List<String> words = new ArrayList<>(message);
        hamProbability = memDB.calculateProbability(words, MemDB.Table.HAM,k,hamAlphabet);
        spamProbability = memDB.calculateProbability(words, MemDB.Table.SPAM,k,spamAlphabet);

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
