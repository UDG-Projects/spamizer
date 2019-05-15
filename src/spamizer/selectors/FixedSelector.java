package spamizer.selectors;

import spamizer.entity.Mail;
import spamizer.entity.Result;
import spamizer.interfaces.Filter;
import spamizer.interfaces.Reader;
import spamizer.interfaces.Selector;

import java.util.*;

public class FixedSelector implements Selector {


    private Set<Mail> unknown;
    private Result result;
    private Filter filter;
    private Reader spamReader;
    private Reader hamReader;
    private int percentage;


    public FixedSelector(Reader spamReader, Reader hamReader, int percentage, Result result, Filter filter) {
        this.spamReader = spamReader;
        this.hamReader = hamReader;
        unknown = new HashSet<>();
        this.result = result;
        this.filter = filter;
        this.percentage = percentage;
    }



    @Override
    public Collection<Mail> getUnknown() {
        // En cas que no s'hagi accedit a cap dels dos mètodes forcem per que hi accedeixi.
        if(unknown.size() == 0){
            Collection<Mail> spam = getSpam();
            Collection<Mail> ham = getHam();
        }
        List<Mail> result = new ArrayList<>(unknown);
        Collections.sort(result);
        this.result.setValidateNumber(result.size());
        return result;
    }

    @Override
    public Collection<Mail> getHam() {
        Collection<Mail> mails  = getMails(hamReader.read(false, filter));
        this.result.setHamNumber(mails.size());
        return mails;
    }

    @Override
    public Collection<Mail> getSpam() {
        Collection<Mail> mails  = getMails(spamReader.read(true, filter));
        this.result.setSpamNumber(mails.size());
        return mails;
    }

    private Collection<Mail> getMails(Collection<Mail> mails){
        int divider = mails.size() * percentage / 100;
        ArrayList<Mail> ordered = new ArrayList<Mail>(mails);
        Collections.sort(ordered);
        Iterator<Mail> iterator = ordered.iterator();
        int counter = 0;
        while(iterator.hasNext() && counter < divider){
            this.unknown.add(iterator.next());
            counter++;
        }
        List mailsResult = new ArrayList<>();
        while (iterator.hasNext()){
            mailsResult.add(iterator.next());
        }
        return mailsResult;
    }

}
