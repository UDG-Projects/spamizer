package spamizer.MLCore;

import spamizer.interfaces.Reader;

import java.util.Collection;
import java.util.Queue;

public class KFoldCrossValidationSelection {


    private Reader reader;
    private Queue<Mail> spam;
    private Queue<Mail> ham;
    private Queue<Mail> unknown;

    public KFoldCrossValidationSelection(Reader reader){
        this.reader = reader;
    }

    public void read(){
        Collection<Mail> mails = reader.read();
    }





}
