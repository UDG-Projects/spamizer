package spamizer.MLCore;

import spamizer.entity.TableEnumeration.Table;

import spamizer.entity.Mail;
import spamizer.entity.Result;
import spamizer.interfaces.Method;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;

public class Validator extends Trainer {

    private Method classifcationMethod;
    private Result result;

    /**
     * Creates an instance of Trainer and validator object
     * @param classifcationMethod Method used to eval if mails are spam or ham
     * @param result Global application results
     */
    public Validator(Method classifcationMethod, Result result) {
        this(classifcationMethod);
        this.result = result;
    }

    public Validator(Method classifcationMethod) {
        super();
        this.classifcationMethod = classifcationMethod;
    }

    /**
     * Validates testMail content with k and phi constants, updates the result variable.
     * @param testMail Collection of mails to validate
     * @param k Constant
     * @param phi Constant
     */
    public void validate(Collection<Mail> testMail, double k, double phi) {
        start = Instant.now();
        int tp=0, tn=0, fp=0, fn=0;
        int totalHam=0;
        int totalSpam=0;
        result.setPhi(phi);
        result.setK(k);
        for(Mail m: testMail){
            try {
                HashMap<String, Integer> words = m.getBodyMail();
                //abans de classificar s'ha d'assegurar que les paraules hi son com a minim un cop a les 2 taules
                boolean isSpam = classifcationMethod.isSpam(memoryDataBase, words.keySet(), k, phi);

                if(isSpam){
                    result.setSpamNumber(result.getSpamNumber() + 1);
                }
                else {
                    result.setHamNumber(result.getHamNumber() + 1);
                }

                insertClassificatedMail(words, isSpam);
                //actualitzar comtadors
                if (!m.getSpam()) {
                    totalHam++;
                    if (!isSpam)
                        tp++; //Missatge HAM classificat correctament com a HAM
                    else
                        fp++; //Missatge HAM classificat com a SPAM
                } else {
                    totalSpam++;
                    if (isSpam)
                        tn++; //Missatge SPAM classificat correctament com spam
                    else
                        fn++; //Missatge SPAM classificat com a HAM
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        end = Instant.now();
        //Un cop finalitzada la cassificacio inserir resultats al resultat global.

        result.setTp(tp);
        result.setFp(fp);
        result.setTn(tn);
        result.setFn(fn);
    }

    private void insertClassificatedMail(HashMap<String,Integer> words, boolean isSpam)  {
        Table table = Table.HAM;
        if (isSpam)
            table= Table.SPAM;

        memoryDataBase.insertOrUpdate(table,words);
    }
}
