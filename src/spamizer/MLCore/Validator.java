package spamizer.MLCore;

import spamizer.entity.Database.Table;

import spamizer.entity.LocalDB;
import spamizer.interfaces.Method;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

public class Validator extends Trainer {

    private Method classifcationMethod;

    public Validator(Method classifcationMethod) throws SQLException, ClassNotFoundException {
        super();
        this.classifcationMethod = classifcationMethod;
    }


    public void validate(Collection<Mail> testMail, int k, int phi) throws SQLException, ClassNotFoundException {
        int tp=0, tn=0, fp=0, fn=0;
        for(Mail m: testMail){
            HashMap<String,Integer> words = m.getBodyMail();
            //abans de classificar s'ha d'assegurar que les paraules hi son com a minim un cop a les 2 taules
            memoryDataBase.insertZeroValues(Table.HAM,words);
            memoryDataBase.insertZeroValues(Table.SPAM,words);
            boolean isSpam = classifcationMethod.isSpam(memoryDataBase,words.keySet(),k,phi);

            insertClassificatedMail(words,isSpam);
            //actualitzar comtadors
            if(!m.getSpam()){
                if(!isSpam)
                    tp++; //Missatge HAM classificat correctament com a HAM
                else
                    fp++; //Missatge HAM classificat com a SPAM
            }
            else{
                if(isSpam)
                    tn++; //Missatge SPAM classificat correctament com spam
                else
                    fn++; //Missatge SPAM classificat com a HAM
            }
        }

        //Un cop finalitzada la cassificacio inserir resultats
        // TODO : Falta actualtizar la taula amb els valors de temps i els nombres correus que s'han fet servir tant com a ham i com a spam i com unknown.
        LocalDB.getInstance().insertResult(phi,k,tp,tn,fp,fn);

        System.out.println("---------------------------------------------------------");
        System.out.println("---------------------------------------------------------");
        System.out.println(" PHI : " + phi);
        System.out.println(" K   : " + k);
        System.out.println("---------------------------------------------------------");
        System.out.println(" Missatge HAM classificat correctament com a HAM : " + tp);
        System.out.println(" Missatge HAM classificat com a SPAM : " + fp);
        System.out.println(" Missatge SPAM classificat correctament com spam : " + tn);
        System.out.println(" Missatge SPAM classificat com a HAM : " + fn);
        System.out.println("---------------------------------------------------------");
        System.out.println("---------------------------------------------------------");
    }

    private void insertClassificatedMail(HashMap<String,Integer> words, boolean isSpam) throws SQLException {
        Table table = Table.HAM;
        if (isSpam)
            table= Table.SPAM;

        memoryDataBase.insertOrUpdate(table,words);
    }
}
