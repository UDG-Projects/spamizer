package spamizer.configurations;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.omg.CORBA.DATA_CONVERSION;
import spamizer.entity.Database;

public class ApplicationOptions {

    public static final String OPTION_COMPUTE = "c";
    public static final String OPTION_COMPUTATIONS_NUMBER = "n";
    public static final String OPTION_HAM = "h";
    public static final String OPTION_SPAM = "s";
    public static final String OPTION_TRAINING = "t";
    public static final String OPTION_DATABASE = "d";
    public static final String OPTION_VALIDATION = "v";
    public static final String OPTION_PERSIST = "p";

    private Options options;

    public ApplicationOptions(){
        options = new Options();

        options.addOption(new Option(OPTION_HAM,  "Set training mails as ham, adding this argument -s must not be present"));
        options.addOption(new Option(OPTION_SPAM,  "Set training mails as spam, adding this argument -h must not be present"));
        options.addOption(new Option(OPTION_TRAINING, true, "Directory where training mails in txt are stored, this or database argument must be present"));
        options.addOption(new Option(OPTION_DATABASE, true, "Database file with other execution data, this or directory training argument must be present"));
        options.addOption(new Option(OPTION_VALIDATION, true, "Directory where validation mails in txt are stored"));
        options.addOption(new Option(OPTION_PERSIST, true, "Directory where final database will be persisted"));
        options.addOption(new Option(OPTION_COMPUTE, true, "Receives 2 parameters, A directory with spam mails and a directory with ham mails. "
                + "A calculation for values phi and k will be done using a selection for the mails set. The selection will be k-fold cross-validation and the heuristic method used to calculate phi and k values will be High Climbing with random restarts"));
        options.addOption(new Option(OPTION_COMPUTATIONS_NUMBER, true, "The number of iterations for -c mode execution."));
    }

    public static Database.Table getTableFromParameter(String param){
        if(param == OPTION_SPAM) return Database.Table.SPAM;
        else if(param == OPTION_HAM) return Database.Table.HAM;
        // Està fet expressament per què falli.
        else return null;
    }


    public Options getOptions(){
        return options;
    }

}
