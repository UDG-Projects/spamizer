package spamizer.configurations;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

public class ApplicationOptions {

    public static final String OPTION_HAM = "h";
    public static final String OPTION_SPAM = "s";
    public static final String OPTION_TRAINING = "t";
    public static final String OPTION_DATABASE = "d";
    public static final String OPTION_VALIDATION = "v";
    public static final String OPTION_PERSIST = "p";

    private boolean ham;
    private boolean spam;
    private String training;
    private String database;
    private String validation;
    private String persist;
    private Options options;

    public ApplicationOptions(){
        options = new Options();
        options.addOption(new Option(OPTION_HAM,  "Set training mails as ham, adding this argument -s must not be present"));
        options.addOption(new Option(OPTION_SPAM,  "Set training mails as spam, adding this argument -h must not be present"));
        options.addOption(new Option(OPTION_TRAINING, true, "Directory where training mails in txt are stored, this or database argument must be present"));
        options.addOption(new Option(OPTION_DATABASE, true, "Database file with other execution data, this or directory training argument must be present"));
        options.addOption(new Option(OPTION_VALIDATION, true, "Directory where validation mails in txt are stored"));
        options.addOption(new Option(OPTION_PERSIST, true, "Directory where final database will be persisted"));
    }

    public Options getOptions(){
        return options;
    }

}
