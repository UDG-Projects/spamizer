package spamizer.MLCore;

import spamizer.exceptions.BadPercentageException;
import spamizer.interfaces.Filter;
import spamizer.interfaces.Reader;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class KFoldCrossValidationSelection {

    private Queue<Mail> unknown;
    private int percentage;
    private Random random;

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
    public KFoldCrossValidationSelection(Reader spamReader, Reader hamReader, int percentage, Random random) throws BadPercentageException {
        if(percentage < 0 || percentage > 100)
            throw new BadPercentageException("Value " + percentage + " is not correct, must be between 0 and 100");
        this.spamReader = spamReader;
        this.hamReader = hamReader;
        this.percentage = percentage;
        this.random = random;
        unknown = new PriorityQueue<>();
    }

    /**
     * Selecciona aleatòriament els correus de la font de dades spam
     * @return Una collecció amb els correus considerats spam
     */
    public Collection<Mail> getSpam(){
        return getFilteredByPercentage(spamReader.read(true));
    }

    /**
     * Selecciona aleatòriament els correus de la font de dades ham
     * @return Una collecció amb els correus considerats ham
     */
    public Collection<Mail> getHam(){
        return getFilteredByPercentage(hamReader.read(false));
    }

    /**
     * PRE : --
     * POST : LLista de correus que no s'han filtrat.
     * @return un llistat aleatori de correus que no han sigut filtrats.
     */
    public Collection<Mail> getUnknown(){
        // En cas que no s'hagi accedit a cap dels dos mètodes forcem per que hi accedeixi.
        if(unknown.size() == 0){
            Collection<Mail> spam = getSpam();
            Collection<Mail> ham = getHam();
        }
        return unknown;
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
