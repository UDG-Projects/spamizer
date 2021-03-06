package main.java.spamizer.MLCore;

import main.java.spamizer.entity.MemDB;
import main.java.spamizer.entity.TableEnumeration;
import main.java.spamizer.interfaces.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NaiveBayes implements Method {


    @Override
    public boolean isSpam(MemDB memDB, Collection<String> message, double k, double phi)  {
        double spamProbability=0;
        double hamProbability=0;

        int hamAlphabet = memDB.getCountAlphabet(TableEnumeration.Table.HAM);
        int spamAlphabet = memDB.getCountAlphabet(TableEnumeration.Table.SPAM);

        List<String> words = new ArrayList<>(message);
        hamProbability = memDB.calculateProbability(words, TableEnumeration.Table.HAM,k,hamAlphabet);
        spamProbability = memDB.calculateProbability(words, TableEnumeration.Table.SPAM,k,spamAlphabet);

        double pTotalSpam = memDB.getMessageProbabylity(MemDB.Column.SPAM,k);
        double pTotalHam = memDB.getMessageProbabylity(MemDB.Column.HAM,k);

        //Tenim la probatilitat sumada de cada una de les paraules del missatge en forma de logaritme
        //sumem les probatilitats de que els missatges siguin spam o ham en forma de logaritme
        hamProbability+= pTotalHam;
        spamProbability += pTotalSpam;

        //comparem les probabilitats obtingudes aplicant el parametre phi.
//        return Math.exp(spamProbability) > (Math.exp(hamProbability)*(phi));
        return (spamProbability) > ((hamProbability)+ Math.log(phi));


    }
}
