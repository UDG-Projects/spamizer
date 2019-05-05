package spamizer.MLCore;

import java.util.HashMap;

public class Mail {

    private String subject;
    private String body;
    private Filter filter;

    public Mail(){

    }
    public Mail(String subject, String body){
        this.subject = subject;
        this.body = body;
    }

    /**
     * Returns set of Pair<String,Integer> represents body's words and their appear frecuency
     * @param isfiltered
     * @return
     */
    public HashMap<String,Integer> getSubjectMail(boolean isfiltered){
            return filter.Filter(subjectSet);
    }
    /**
     * Returns set of Pair<String,Integer> represents subject's words and their appear frecuency
     * @param isfiltered
     * @return
     */
    public HashMap<String,Integer> getBodyMail(boolean isfiltered){
            return filter.Filter(bodySet);
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
}
