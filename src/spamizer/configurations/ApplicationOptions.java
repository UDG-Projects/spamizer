package spamizer.configurations;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import spamizer.entity.MemDB;

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

        Option training = new Option(OPTION_TRAINING, true, "Directories where training mails in txt are stored, this or database argument must be present" +
                "you can set a maximum of 2 directories in this order : -t <spamDir> <hamDir>. " +
                "If only one dir is set the parameter -h or -s must be included");
        training.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(training);


        options.addOption(new Option(OPTION_DATABASE,  "Flag that indicates that data must be loaded from local database"));

        Option validation = new Option(OPTION_VALIDATION, true, "Directories where validation mails in txt are stored" +
                                                                        "you can set a maximum of 2 directories in this order : -v <spamDir> <hamDir>. ");
        validation.setArgs(2);

        options.addOption(validation);
        options.addOption(new Option(OPTION_PERSIST,  "Set the persistance of the memory database to a local database"));

        Option compute = new Option(OPTION_COMPUTE, true, "Usage : -c <spamDir> <hamDir> [-n <int>] \nReceives 2 parameters, A directory with spam mails and a directory with ham mails. "
                + "A calculation for values phi and k will be done using a selection for the mails set. The selection will be k-fold cross-validation and the heuristic method used to calculate phi and k values will be High Climbing with random restarts");
        compute.setArgs(2);
        options.addOption(compute);

        options.addOption(new Option(OPTION_COMPUTATIONS_NUMBER, true, "The number of iterations for -c mode execution."));
    }

    public static MemDB.Table getTableFromParameter(String param){
        if(param == OPTION_SPAM) return MemDB.Table.SPAM;
        else if(param == OPTION_HAM) return MemDB.Table.HAM;
        // Està fet expressament per què falli.
        else return null;
    }


    public Options getOptions(){
        return options;
    }

}
