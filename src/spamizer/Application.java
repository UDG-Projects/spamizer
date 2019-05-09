package spamizer;
import javafx.util.Pair;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import spamizer.MLCore.*;
import spamizer.configurations.ApplicationOptions;
import spamizer.entity.Database;
import spamizer.entity.LocalDB;
import spamizer.entity.MemDB;
import spamizer.entity.Result;
import spamizer.exceptions.BadArgumentsException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import spamizer.exceptions.BadPercentageException;
import spamizer.exceptions.CustomException;
import spamizer.interfaces.Filter;
import spamizer.interfaces.Reader;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.Random;

import static spamizer.entity.Database.Table.HAM;

/**
 * Application Spamizer
 */
public class Application  {

    public static int MIN_PERC = 5;
    public static int MAX_PERC = 15;

    public static Random random;
    public static Result result;

    public static Filter filter;

    /**
     * Procés de creació i validació en funció dels arguments de la instrucció que es reb per paràmetre.
     *
     * Comencem actualitzant i carregant les dades en memòria
     * 1- Carreguem bd si s'afegeix la opció.
     * 2- Carreguem mails si s'afegeix la opció.
     * 3- Validem si s'afegeix la opció.
     * 4- Persistim la bd en memòria a un fitxer local si s'afegeix la opció.
     */
    public static void start(CommandLine options) throws BadArgumentsException, SQLException, ClassNotFoundException, BadPercentageException {

        // TODO : No està implementat encara la lpògica per el mètode "-c" càlcul de phi i k i el paràmetre -n
        if(options.hasOption(ApplicationOptions.OPTION_COMPUTE)){
            compute(options);
        }
        else {
            if(!options.hasOption(ApplicationOptions.OPTION_VALIDATION) && !options.hasOption(ApplicationOptions.OPTION_TRAINING) && !options.hasOption(ApplicationOptions.OPTION_DATABASE))
                throw new BadArgumentsException("No option selected. ");

            if ((options.hasOption(ApplicationOptions.OPTION_TRAINING) &&
                    options.getOptionValues(ApplicationOptions.OPTION_TRAINING).length == 1 &&
                    !options.hasOption(ApplicationOptions.OPTION_HAM) &&
                    !options.hasOption(ApplicationOptions.OPTION_SPAM)))
                throw new BadArgumentsException("To train database from Directory files must include arguments ham or spam [-h | -s].");

            if(options.hasOption(ApplicationOptions.OPTION_TRAINING) &&
                    options.getOptionValues(ApplicationOptions.OPTION_TRAINING).length == 1 &&
                    options.hasOption(ApplicationOptions.OPTION_HAM) &&
                    options.hasOption(ApplicationOptions.OPTION_SPAM))
                throw new BadArgumentsException("To train database from Directory files only one value for ham or spam must be included [-h | -s].");

            if ((options.hasOption(ApplicationOptions.OPTION_VALIDATION) &&
                    !options.hasOption(ApplicationOptions.OPTION_HAM) &&
                    !options.hasOption(ApplicationOptions.OPTION_SPAM)))
                throw new BadArgumentsException("The Validation -v option must include [-h | -s] parameters.");


            if(options.hasOption(ApplicationOptions.OPTION_DATABASE)) {
                // En aquest cas com que la base de dades que estem carregant des d'un fitxer ja conté totes les taules no cal que distingim entre ham o spam
                System.out.println("## TODO : Actualitzem la bd amb una altre bd anomenada : " + options.getOptionValue(ApplicationOptions.OPTION_DATABASE));
                LocalDB localDb = LocalDB.getInstance();
                MemDB memDb = MemDB.getInstance();

                memDb.insertOrUpdate(Database.Table.SPAM, localDb.select(Database.Table.SPAM));
                memDb.insertOrUpdate(HAM, localDb.select(HAM));

                Pair<Integer, Integer> messagesCounters = localDb.selectMessages();
                memDb.insertCounters(messagesCounters.getKey(), messagesCounters.getValue());

                System.out.println("## TODO : Carregada la informació en memòria : " + options.getOptionValue(ApplicationOptions.OPTION_DATABASE));

            }


            if(options.hasOption(ApplicationOptions.OPTION_TRAINING)) {
                Trainer trainer = new Trainer();
                if(options.getOptionValues(ApplicationOptions.OPTION_TRAINING).length == 1) {

                    if (options.hasOption(ApplicationOptions.OPTION_HAM)) {
                        System.out.println("## TODO : Actualitzem la bd amb un directori de mails anomenat : \"" + options.getOptionValue(ApplicationOptions.OPTION_TRAINING) + "\" sabent que és ham");

                        trainer.train(ApplicationOptions.getTableFromParameter(ApplicationOptions.OPTION_HAM),
                                new DirectoryMailReader(options.getOptionValue(ApplicationOptions.OPTION_TRAINING)),
                                //new CustomFilter());
                                StanfordCoreNLPFilter.getInstance());
                        System.out.println("Trained HAM in " + Application.millisToString(trainer.getExecutionMillis()));

                        System.out.println(MemDB.getInstance().select(Database.Table.HAM));

                    } else {

                        System.out.println("## TODO : Actualitzem la bd amb un directori de mails anomenat : \"" + options.getOptionValue(ApplicationOptions.OPTION_TRAINING) + "\" sabent que és spam");

                        trainer.train(ApplicationOptions.getTableFromParameter(ApplicationOptions.OPTION_SPAM),
                                new DirectoryMailReader(options.getOptionValue(ApplicationOptions.OPTION_TRAINING)),
                                StanfordCoreNLPFilter.getInstance());
                        System.out.println("Trained SPAM in " + Application.millisToString(trainer.getExecutionMillis()));
                    }
                }
                else {
                    String directorySpam = options.getOptionValues(ApplicationOptions.OPTION_TRAINING)[0];
                    String directoryHam = options.getOptionValues(ApplicationOptions.OPTION_TRAINING)[1];
                    trainer.train(Database.Table.SPAM,
                            new DirectoryMailReader(directorySpam),
                            StanfordCoreNLPFilter.getInstance());

                    System.out.println("Trained HAM in " + Application.millisToString(trainer.getExecutionMillis()));

                    trainer.train(HAM,
                            new DirectoryMailReader(directoryHam),
                            StanfordCoreNLPFilter.getInstance());

                    System.out.println("Trained SPAM in " + Application.millisToString(trainer.getExecutionMillis()));
                }

            }

            if(options.hasOption(ApplicationOptions.OPTION_VALIDATION)){

                String validateDir = options.getOptionValues(ApplicationOptions.OPTION_VALIDATION)[0];
                System.out.println("## TODO : Llencem el procés de validació amb el directori ." + options.getOptionValue(ApplicationOptions.OPTION_VALIDATION));
                Validator validator = new Validator(new NaiveBayes(), result);

                validator.validate(new DirectoryMailReader(validateDir).read(options.hasOption(ApplicationOptions.OPTION_SPAM), filter),1,1);
                System.out.println("Validation finished in " + Application.millisToString(validator.getExecutionMillis()));

            }

            // Persistim els canvis a la base de dades local sí o sí
            if(options.hasOption(ApplicationOptions.OPTION_PERSIST)){
                System.out.println("## TODO : Peristim la base de dades final al fitxer :" + options.getOptionValue(ApplicationOptions.OPTION_PERSIST));

                MemDB memory = MemDB.getInstance();
                LocalDB file = LocalDB.getInstance();

                // Eliminem les entrades en local i inserim la base de dades en local.
                file.delete(HAM);
                file.delete(Database.Table.SPAM);
                file.delete(Database.Table.MESSAGE);

                file.insertOrUpdate(HAM, memory.select(HAM));
                file.insertOrUpdate(Database.Table.SPAM, memory.select(Database.Table.SPAM));

                Pair<Integer, Integer> messagesCounters = memory.selectMessages();
                file.insertCounters(messagesCounters.getKey(), messagesCounters.getValue());
            }
        }

    }

    /**
     * Mètode per calcular la phi i la k
     *
     * Si no hi ha nombre d'iteracions només ho farà un sol cop.
     * Per l'ordre següent es realitza la lectura de spam i després de ham dels directoris.
     * @param options les opcions on hi ha els directoris spam i ham i el nombre de iteracions que ha de realitzar
     */
    public static void compute(CommandLine options) throws BadArgumentsException, BadPercentageException, SQLException, ClassNotFoundException {

        System.out.println("---------------------------------------------------------");
        System.out.println("---------------------------------------------------------");
        System.out.println("Started Phi and k Computation ... ");
        System.out.println("---------------------------------------------------------");

        int iterations = 1;
        if(options.hasOption(ApplicationOptions.OPTION_COMPUTATIONS_NUMBER)){
            iterations = Integer.valueOf(options.getOptionValue(ApplicationOptions.OPTION_COMPUTATIONS_NUMBER));
        }

        // Llegim els directoris per spam i ham
        String[] dirs = options.getOptionValues(ApplicationOptions.OPTION_COMPUTE);

        // Validem l'existència dels dos fitxers.
        if(!(dirs.length > 1 && dirs.length < 3)) throw new BadArgumentsException("Els directoris que s'han passat no son correctes per la opció -c");
        String spamDir = dirs[0];
        String hamDir = dirs[1];

        // A la documentació marca que el directori spam primer i després el directori ham peró per prevenció fem un petit check que ens ho validi amb el nom del directori per no corrompir la BD.
        if(spamDir.toLowerCase().indexOf("ham") > 0 || hamDir.toLowerCase().indexOf("spam") > 0) throw new BadArgumentsException("Comprova que el directori spam va primer i el ham va segon -c");

        Reader spamReader = new DirectoryMailReader(spamDir);
        Reader hamReader = new DirectoryMailReader(hamDir);

        double phi, k=0;


        /**
         * Per cada una de les iteracions demanades :
         * 1. Generem nombre aleatòri per el percentatge de correus que discriminarà dins del rang entre 5 i 15.
         * 2. Generem nombres aleatoris per phi i k.
         */
        for(int count = 0; count < iterations; count++){
            try {
                Instant start = Instant.now();

                int percentage = ThreadLocalRandom.current().nextInt(MIN_PERC, MAX_PERC + 1);
                // TODO : S'ha de fer el generador de phi i k amb high climbing.

                // El nombre de vegades el pes que ha de tenir un correu ham per que sigui considerat spam
                phi = ThreadLocalRandom.current().nextDouble(1,5);
                //phi = ThreadLocalRandom.current().nextDouble(2.1030,2.1035);
                // EL pes que li donem a una paraula que no existeix.

                while(k==0)
                    k = ThreadLocalRandom.current().nextDouble(0, 3);
                    //k = ThreadLocalRandom.current().nextDouble(0.811, 0.8215);

                System.out.println("Kfold Started ... ");
                //KFoldCrossValidationSelection selection = new KFoldCrossValidationSelection(spamReader, hamReader, percentage, random, result);
                KFoldCrossValidationSelection selector = new KFoldCrossValidationSelection(
                        spamReader,
                        hamReader,
                        percentage,
                        random,
                        result,
                        filter
                );
                System.out.println("Kfold finished ... ");

                Validator validator = new Validator(new NaiveBayes(), result);

                validator.train(HAM, selector.getHam(), filter);//StanfordCoreNLPFilter.getInstance());
                System.out.println("Trained HAM in " +Application.millisToString( validator.getExecutionMillis()));
                validator.train(Database.Table.SPAM, selector.getSpam(), filter);//StanfordCoreNLPFilter.getInstance());
                System.out.println("Trained SPAM in " + Application.millisToString(validator.getExecutionMillis()));
                validator.validate(selector.getUnknown(), k, phi);
                System.out.println("Validation done in " + Application.millisToString(validator.getExecutionMillis()));

                Instant end = Instant.now();
                result.setTotalMillis(ChronoUnit.MILLIS.between(start, end));

                System.out.println(result.mountString(count));
                MemDB.getInstance().clearDB();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String [] args) {
        random = new Random();
        result = new Result();
        filter = new CustomFilter();
        ApplicationOptions applicationOptions = new ApplicationOptions();
        try {

            // TODO: Ens assegurem que la bd no està ocupada abans de començar l'execució.
            LocalDB.getInstance().select(HAM);

            Instant start = Instant.now();

            DefaultParser parser = new DefaultParser();
            CommandLine options = parser.parse(applicationOptions.getOptions(), args);
            start(options);

            LocalDB.getInstance().closeDB();

            Instant end = Instant.now();
            System.out.println("Final de l'execució, total de temps transcorregut : " + ChronoUnit.MILLIS.between(start, end));

        } catch (CustomException e) {
            System.err.println(e.getCustomMessage());
            System.err.println("------------------------------------------------------------------------------");
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.err.println("------------------------------------------------------------------------------");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("spamizer", applicationOptions.getOptions());
        } catch (ParseException e) {
            System.err.println("Something went wrong parsing arguments.");
            System.err.println("------------------------------------------------------------------------------");
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.err.println("------------------------------------------------------------------------------");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("spamizer", applicationOptions.getOptions());
        } catch (SQLException e) {
            System.err.println("------------------------------------------------------------------------------");
            System.err.println("HA PETAT L'SQL!!");
            System.err.println("------------------------------------------------------------------------------");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("------------------------------------------------------------------------------");
            System.err.println("HA PETAT L'SQL!!");
            System.err.println("------------------------------------------------------------------------------");
            e.printStackTrace();
        }

    }
    /**
     * DEBUG METHOD
     * @return
     */
    public static String generateRandomString(){
        int length = Math.abs(random.nextInt()) % 10 + 1;

        char[] array = new char[length]; // length is bounded by 7
        for(int i = 0; i< length; i++){
            array[i] = (char) (Math.abs(random.nextInt()) % 26 + 97);
        }
        return new String(array);
    }


    /**
     * DEBUG METHOD
     * @return
     */
    public static Integer generateRandomInteger(){
        return Math.abs(random.nextInt() % 5 + 1);
    }


    /**
     * TIMING METHOD
     * @return
     */
    public static String millisToString(long millis){
        double seconds = millis / 1000.0;
        double minutes = 0;
        if(seconds >= 60) {
            minutes = seconds / 60;
            seconds = seconds % 60;
        }
        return minutes +" m " +  seconds + " s";

    }
}
