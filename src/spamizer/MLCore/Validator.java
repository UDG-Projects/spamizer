package spamizer.MLCore;

import spamizer.entity.Database.Table;

import spamizer.entity.LocalDB;
import spamizer.entity.Result;
import spamizer.interfaces.Method;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;

public class Validator extends Trainer {

    private Method classifcationMethod;
    private Result result;

    public Validator(Method classifcationMethod, Result result) throws SQLException, ClassNotFoundException {
        this(classifcationMethod);
        this.result = result;
    }

    public Validator(Method classifcationMethod) throws SQLException, ClassNotFoundException {
        super();
        this.classifcationMethod = classifcationMethod;
    }

    public void validate(Collection<Mail> testMail, double k, double phi) throws SQLException, ClassNotFoundException {
        start = Instant.now();
        int tp=0, tn=0, fp=0, fn=0;
        int totalHam=0;
        int totalSpam=0;
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
            catch (SQLException sqlEx){
                System.err.println("No s'ha pogut fer el calcul de SPAM --------------------");
                sqlEx.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        end = Instant.now();
        //Un cop finalitzada la cassificacio inserir resultats
        result.setPhi(phi);
        result.setK(k);
        result.setTp(tp);
        result.setFp(fp);
        result.setTn(tn);
        result.setFn(fn);
        LocalDB.getInstance().insertResult(phi,k,tp,tn,fp,fn,totalHam,totalSpam);
    }

    private void insertClassificatedMail(HashMap<String,Integer> words, boolean isSpam) throws SQLException {
        Table table = Table.HAM;
        if (isSpam)
            table= Table.SPAM;

        memoryDataBase.insertOrUpdate(table,words);
    }
}
