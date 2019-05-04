package spamizer.MLCore;
import javafx.util.Pair;
import java.util.HashSet;
import java.util.Set;

public class Mail {

    private String subject;
    private String body;
    private Set<Pair<String,Integer>> subjectSet;
    private Set<Pair<String,Integer>> bodySet;
    private Filter filter;

    public Mail(){

    }
    public Mail(String subject, String body,Set<Pair<String,Integer>> subjectSet,Set<Pair<String,Integer>> bodySet){
        subject ="";
        body = "";
        subjectSet = new HashSet();
        bodySet = new HashSet();
    }

    /**
     * Returns set of Pair<String,Integer> represents body's words and their appear frecuency
     * @param isfiltered
     * @return
     */
    public Set<Pair<String,Integer>> getSubjectMail(boolean isfiltered){
        if(isfiltered){
            return filter.Filter(subjectSet);
        }else{
            return subjectSet;
        }
    }
    /**
     * Returns set of Pair<String,Integer> represents subject's words and their appear frecuency
     * @param isfiltered
     * @return
     */
    public Set<Pair<String,Integer>> getBodyMail(boolean isfiltered){
        if(isfiltered){
            return filter.Filter(bodySet);
        }else{
            return bodySet;
        }
    }


}
