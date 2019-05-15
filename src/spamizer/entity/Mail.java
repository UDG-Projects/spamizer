package spamizer.entity;

import spamizer.interfaces.Filter;

import java.util.HashMap;

/**
 * El filtre per defecte que assignem a la classe Mail, és l'StandfordCoreNLPFilter
 */
public class Mail implements Comparable<Mail> {

    //private String subject;
    private String fileName;
    private String body;
    private Filter filter;
    private Boolean isSpam;


    public Mail(String fileName, String body, boolean isSpam, Filter filter) { //,String subject,){){
        this.fileName = fileName;
        this.body = body;
        this.isSpam = isSpam;
        this.filter = filter;
    }

    /**
     * Returns set of Pair<String,Integer> represents body's words and their appear frecuency
     * @return
     */
    /*public HashMap<String,Integer> getSubjectMail(){
            return filter.filterText(this.subject);
    }*/
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
    /*public String getSubject(){
        return subject;
    }*/

    public void setFilter(Filter filter){
        this.filter = filter;
    }

    public Boolean getSpam() {
        return isSpam;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int compareTo(Mail o) {
        return this.fileName.compareTo(o.fileName);
    }
}
