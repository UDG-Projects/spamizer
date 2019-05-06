package spamizer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import spamizer.MLCore.DirectoryMailReader;
import spamizer.MLCore.KFoldCrossValidationSelection;
import spamizer.MLCore.StanfordCoreNLPFilter;
import spamizer.configurations.ApplicationOptions;
import spamizer.entity.MemDB;
import spamizer.exceptions.BadArgumentsException;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import spamizer.MLCore.Trainer;
import spamizer.exceptions.BadPercentageException;
import spamizer.exceptions.CustomException;
import spamizer.interfaces.Reader;

import java.sql.SQLException;
import java.util.Random;

/**
 * Application Spamizer
 */
public class Application  {

    public static int MIN_PERC = 5;
    public static int MAX_PERC = 15;

    public static Random random;

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

            if(options.hasOption(ApplicationOptions.OPTION_VALIDATION) && options.getOptionValues(ApplicationOptions.OPTION_VALIDATION).length != 2)
                throw new BadArgumentsException("The Validation -v option must include 2 and only 2 directories.");

            if(options.hasOption(ApplicationOptions.OPTION_DATABASE)) {
                // En aquest cas com que la base de dades que estem carregant des d'un fitxer ja conté totes les taules no cal que distingim entre ham o spam
                System.out.println("## TODO : Actualitzem la bd amb una altre bd anomenada : " + options.getOptionValue(ApplicationOptions.OPTION_DATABASE));
            }


            if(options.hasOption(ApplicationOptions.OPTION_TRAINING)) {
                Trainer trainer = new Trainer();
                if(options.getOptionValues(ApplicationOptions.OPTION_TRAINING).length == 1) {

                    if (options.hasOption(ApplicationOptions.OPTION_HAM)) {
                        System.out.println("## TODO : Actualitzem la bd amb un directori de mails anomenat : \"" + options.getOptionValue(ApplicationOptions.OPTION_TRAINING) + "\" sabent que és ham");

                        trainer.train(ApplicationOptions.getTableFromParameter(ApplicationOptions.OPTION_HAM),
                                new DirectoryMailReader(options.getOptionValue(ApplicationOptions.OPTION_TRAINING)),
                                new StanfordCoreNLPFilter());

                    } else {

                        System.out.println("## TODO : Actualitzem la bd amb un directori de mails anomenat : \"" + options.getOptionValue(ApplicationOptions.OPTION_TRAINING) + "\" sabent que és spam");

                        trainer.train(ApplicationOptions.getTableFromParameter(ApplicationOptions.OPTION_SPAM),
                                new DirectoryMailReader(options.getOptionValue(ApplicationOptions.OPTION_TRAINING)),
                                new StanfordCoreNLPFilter());
                    }
                }
                else {
                    String directorySpam = options.getOptionValues(ApplicationOptions.OPTION_TRAINING)[0];
                    String directoryHam = options.getOptionValues(ApplicationOptions.OPTION_TRAINING)[1];
                    trainer.train(MemDB.Table.SPAM,
                            new DirectoryMailReader(directorySpam),
                            new StanfordCoreNLPFilter());

                    trainer.train(MemDB.Table.HAM,
                            new DirectoryMailReader(directoryHam),
                            new StanfordCoreNLPFilter());

                }

            }

            if(options.hasOption(ApplicationOptions.OPTION_VALIDATION)){
                String spamDir = options.getOptionValues(ApplicationOptions.OPTION_VALIDATION)[0];
                String hamDir = options.getOptionValues(ApplicationOptions.OPTION_VALIDATION)[0];
                System.out.println("## TODO : Llencem el procés de validació amb el directori ." + options.getOptionValue(ApplicationOptions.OPTION_VALIDATION));
            }

            // Persistim els canvis a la base de dades local sí o sí
            if(options.hasOption(ApplicationOptions.OPTION_PERSIST)){
                System.out.println("## TODO : Peristim la base de dades final al fitxer :" + options.getOptionValue(ApplicationOptions.OPTION_PERSIST));
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
    public static void compute(CommandLine options) throws BadArgumentsException, BadPercentageException {

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

        int phi, k;

        /**
         * Per cada una de les iteracions demanades :
         * 1. Generem nombre aleatòri per el percentatge de correus que discriminarà dins del rang entre 5 i 15.
         * 2. Generem nombres aleatoris per phi i k.
         */
        for(int count = 0; count < iterations; count++){
            int percentage = ThreadLocalRandom.current().nextInt(MIN_PERC, MAX_PERC + 1);
            // TODO : S'ha de fer el generador de phi i k amb high climbing.

            KFoldCrossValidationSelection selection = new KFoldCrossValidationSelection(spamReader, hamReader, percentage, random);
            // TODO : a selection hi ha els mails carregats sense filtrar.
            // TODO : Primer s'ha d'accedir a getSpam i getHam i després accedir a getUnknown.

            // TODO : S'ha de llençar la validació , rebre els resultats ok i els resultats bad
            // TODO : S'ha d'inserir la fila de l'execució amb la phi i la k generades.
        }

    }

    public static void main(String [] args) {
        random = new Random();
        ApplicationOptions applicationOptions = new ApplicationOptions();
        try {

            DefaultParser parser = new DefaultParser();
            CommandLine options = parser.parse(applicationOptions.getOptions(), args);
            start(options);

            System.out.println(MemDB.getInstance().select(MemDB.Table.HAM));
            System.out.println(MemDB.getInstance().select(MemDB.Table.SPAM));

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
        return String.format("%d sec, %d millis",
                TimeUnit.MILLISECONDS.toSeconds(millis),
                TimeUnit.MILLISECONDS.toMillis(millis) -
                        TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis))
        );
    }















/*

        try {
            LocalDB localDB = LocalDB.getInstance();
            localDB.insertResult(2,3,1,0,1,0);

            MemDB database = MemDB.getInstance();

            /*List<Pair<String, Integer>> values = new ArrayList<>();
            values.add(new Pair<>("hola", 1));
            values.add(new Pair<>("adeu", 2));

            database.insertOrUpdate(MemDB.Table.HAM, values);
            System.out.println(database.select(MemDB.Table.HAM));

            values.add(new Pair<>("hola", 1));
            values.add(new Pair<>("adeu", 2));

            database.insertOrUpdate(MemDB.Table.HAM, values);
            System.out.println(database.select(MemDB.Table.HAM));*/

    // Genero 1000 paraules diferents, simulem que és l'alfabet

            /*Instant start = Instant.now();
            System.out.println("Started word generation. ");
            String[] wordsValues = new String[1000];
            for(int i = 0; i < 1000; i++){
                wordsValues[i] = generateRandomString();
            }
            Instant end = Instant.now();
            System.out.println("Finished generation done in " + millisToString(ChronoUnit.MILLIS.between(start, end)));

            start = Instant.now();
            System.out.println("Started insertion");
            // Generem 10000 correus que tindran una mitjana aleatòria de fins a 300 paraules cada correu i fem les insercions
            for(int i = 0; i < 1000; i++){
                HashMap<String, Integer> email = new HashMap();
                int wordsForEmail = Math.abs(random.nextInt()) % 300;
                for(int j = 0; j < wordsForEmail; j++) {
                    int randomPosition = Math.abs(random.nextInt()) % 1000;
                    String word = wordsValues[randomPosition];
                    if(email.containsKey(wordsValues[randomPosition])){
                        email.replace(word, email.get(word) + 1);
                    }
                    else
                        email.put(word, 1);
                }
                database.insertOrUpdate(MemDB.Table.HAM, email);
            }
            end = Instant.now();
            System.out.println("Finished insertion done in " + millisToString(ChronoUnit.MILLIS.between(start, end)));

            start = Instant.now();
            System.out.println("Started selection");
            System.out.println(database.sum(MemDB.Table.HAM));
            end = Instant.now();
            System.out.println("Selection done in " + millisToString(ChronoUnit.MILLIS.between(start, end)));


            /*String s = "dear vince ,\n" +
                    "thank you very much for updating me on the status of my job application . ? i\n" +
                    "got another good news last week . ? i am happy to inform you i passed the 2000\n" +
                    "cfa level i examination . the pass rate for level i examination this year is\n" +
                    "52 % . i look forward to hearing from you .\n" +
                    "sincerely ,\n" +
                    "rabi de\n" +
                    "?\n" +
                    "?\n" +
                    "? vince . j . kaminski @ enron . com wrote :\n" +
                    "rabi ,\n" +
                    "thanks for your MESSAGE .\n" +
                    "everybody who interviewed you was greatly impressed with your technical\n" +
                    "skills and professional attitude .\n" +
                    "we shall extend an offer to you within a day or two .\n" +
                    "vince\n" +
                    "rabi deon 08 / 22 / 2000 02 : 57 : 37 pm\n" +
                    "to : vince kaminsky\n" +
                    "cc :\n" +
                    "subject : Follow - up interview on 8 / 21 / 00\n" +
                    "dear dr . kaminsky :\n" +
                    "thank you very much for arranging the follow - up interview with your\n" +
                    "internal clients . i visited mr . ted murphy and his staff at rac and mr .\n" +
                    "dennis benevides at ees yesterday . i was impressed with level of risk\n" +
                    "technology employed by enron to achieve its business objectives . i want to\n" +
                    "reiterate my strong interest in joining your group , which is held in very\n" +
                    "high esteem both inside and outside of enron . ? i look forward to h ! earing\n" +
                    "from you .\n" +
                    "sincerely ,\n" +
                    "rabi s . de\n" +
                    "do you yahoo ! ?\n" +
                    "yahoo ! mail - free email you can access from anywhere !\n" +
                    "do you yahoo ! ?\n" +
                    "yahoo ! mail - free email you can access from anywhere !";

            StanfordCoreNLPFilter filter = new StanfordCoreNLPFilter();

            HashMap<String, Integer> words = filter.filterText(s);

            for(Map.Entry<String, Integer> word : words.entrySet()){
                System.out.println(word.getKey() + " -> " + word.getValue());
            }*/

    //System.out.println("Selection done in " + millisToString(ChronoUnit.MILLIS.between(start, end)));
            /*System.out.println(database.select(MemDB.Table.HAM));


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Falta el driver i possiblement afegir el jar com a llibreria del projecte.");
            System.out.println("Consulta el README.md i segueix els passos descrits.");
            System.out.println("Happy coding!");
            e.printStackTrace();
        }*/














}
