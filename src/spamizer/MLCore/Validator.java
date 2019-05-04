package spamizer.MLCore;

import java.sql.SQLException;

public class Validator extends Trainer {

    private Method classifcationMethod;

    public Validator(Method classifcationMethod) throws SQLException, ClassNotFoundException {
        super("","");
        this.classifcationMethod = classifcationMethod;
    }
}
