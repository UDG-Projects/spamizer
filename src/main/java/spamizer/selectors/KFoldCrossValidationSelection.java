package main.java.spamizer.selectors;

import main.java.spamizer.entity.Mail;
import main.java.spamizer.entity.Result;
import main.java.spamizer.exceptions.BadPercentageException;
import main.java.spamizer.interfaces.Filter;
import main.java.spamizer.interfaces.Reader;
import main.java.spamizer.interfaces.Selector;

import java.util.*;

public class KFoldCrossValidationSelection implements Selector {

    private Set<Mail> unknown;
    private int percentage;
    private Random random;
    private Result result;
    private Filter filter;

    private Reader spamReader;
    private Reader hamReader;

    /**
     *
     * @param spamReader El lector de spam (instanciat amb la font de dades dels correus spam)
     * @param hamReader El lector de ham (instanciat amb la font de dades dels correus ham)
     * @param percentage El percentatge de mails que s'utilitzaran per ser discriminats com spam o ham (el valor petit)
     * @param random Una llavor per generar nombres aleatòris.
     * @throws BadPercentageException
     */
    public KFoldCrossValidationSelection(Reader spamReader, Reader hamReader, int percentage, Random random, Result result, Filter filter) throws BadPercentageException {
        if(percentage < 0 || percentage > 100)
            throw new BadPercentageException("Value " + percentage + " is not correct, must be between 0 and 100");
        this.spamReader = spamReader;
        this.hamReader = hamReader;
        this.percentage = percentage;
        this.random = random;
        unknown = new HashSet<>();
        this.result = result;
        this.filter = filter;
    }

    /**
     * Selecciona aleatòriament els correus de la font de dades spam
     * @return Una collecció amb els correus considerats spam
     */
    @Override
    public Collection<Mail> getSpam(){
        Collection<Mail> mails =getFilteredByPercentage(spamReader.read(true, filter));
        this.result.setSpamNumber(mails.size());
        return mails;
    }

    /**
     * Selecciona aleatòriament els correus de la font de dades ham
     * @return Una collecció amb els correus considerats ham
     */
    @Override
    public Collection<Mail> getHam(){
        Collection<Mail> mails = getFilteredByPercentage(hamReader.read(false, filter));
        this.result.setHamNumber(mails.size());
        return mails;
    }

    /**
     * PRE : --
     * POST : LLista de correus que no s'han filtrat.
     * @return un llistat aleatori de correus que no han sigut filtrats.
     */
    @Override
    public Collection<Mail> getUnknown(){
        // En cas que no s'hagi accedit a cap dels dos mètodes forcem per que hi accedeixi.
        if(unknown.size() == 0){
            Collection<Mail> spam = getSpam();
            Collection<Mail> ham = getHam();
        }
        List<Mail> result = new ArrayList<>(unknown);
        Collections.shuffle(result);
        this.result.setValidateNumber(result.size());
        return result;
    }

    /**
     * Aplica el percentatge de filtre per les lectures.
     * @param mails Font de dades
     * @return Retorna la collecció de correus que no s'ha discriminat
     */
    private Collection<Mail> getFilteredByPercentage(Collection<Mail> mails){
        Iterator<Mail> iterator = mails.iterator();
        LinkedList<Mail> filtered = new LinkedList<>();
        while (iterator.hasNext()){
            int randValue = (Math.abs(random.nextInt()) % 101); // Hi ha 101 possibilitats de 0 - 100 en percentatges.
            if(randValue <= percentage){ // Per ser exactes és +=
                unknown.add(iterator.next());
            }
            else {
                filtered.add(iterator.next());
            }
        }
        return filtered;
    }








}
