package spamizer.MLCore;

import spamizer.interfaces.Filter;

import java.util.HashMap;

/**
 * El filtre per defecte que assignem a la classe Mail, és l'StandfordCoreNLPFilter
 */
public class Mail {

    private String subject;
    private String body;
    private Filter filter;

    public Mail(String subject,String body){
        this.subject = subject;
        this.body = body;
        this.filter = new StanfordCoreNLPFilter();
    }
    /**
     * Returns set of Pair<String,Integer> represents body's words and their appear frecuency
     * @return
     */
    public HashMap<String,Integer> getSubjectMail(){
            return filter.filterText(this.subject);
    }
    /**
     * Returns set of Pair<String,Integer> represents subject's words and their appear frecuency
     * @return
     */
    public HashMap<String,Integer> getBodyMail(){

        return filter.filterText(this.body);
    }

    /**
     * Get body from mail
     * @return body
     */
    public String getBody(){
       return body;
    }
    /**
     * Get subject from mail
     * @return subject
     */
    public String getSubject(){
        return subject;
    }

    public void setFilter(Filter filter){
        this.filter = filter;
    }
}
