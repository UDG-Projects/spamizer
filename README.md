# Spamizer

## Configuració projecte a idea

Per poder donar-li al play només has d'afegir una configuració nova (botó esquerra del play), prèmer el signe + i afegir una Application. Un cop fet aquest pas només s'ha de dir quina és la classe que conté el main i deixar que intellijidea faci màgia.

## Base de dades

S'utilitza una base de dades en memòria anomenada [HyperSQL](http://hsqldb.org/).

### Instal·lació del JDBC Driver per HSQLDB.

És necessari utilitzar jetbrains intellijidea per realitzar aquest tutorial.  

A la pestanya superior dreta de l'intellijidea hi ha Database. Per descarregar el driver simplement s'ha de fer com si et connectessis a la base de daes desplegant la pestanya i prement sobre el signe + seleccionant la base de dades HSQLDB. Un cop en la finestra de configuració de la connexió selecciona el text blau a la part inferior que diu descarregar drivers. 

Un cop descarregats els drivers ja tenim el que necessitem, podem tencar la finestra. Selecciona file -> project structure, el menú libraries i afegeix una nova llibreria amb el signe + que apareix a la columna entre el menú i la lateral dreta. Selecciona el fitxer hsqldb.jar situat a l'arrel del projecte. 

Play i a gaudir!! Happy Coding!!

### Instal·lació de la llibreria Stanford Core NLP 

- Descarregar [aixó](https://stanfordnlp.github.io/CoreNLP/download.html). 
- Descomprimir-ho.
- Anar a File -> Project Structure i afegir el directori descomprimit com a Library.

### Instal·lació de la llibreria Commons Cli

La llibreria commons cli s'utilitza per a la lectura dels paràmetres des del main com si es tractés d'una aplicació en C. 

- Anar a File -> Project Structure i afegir la Library commons-cli-1.4.jar inclosa a l'arrel del projecte.

Si no es troba la llibreria al directori arrel s'ha de descarregar de la [pàgina oficial d'apache](https://commons.apache.org/proper/commons-cli/download_cli.cgi). Només ens interessa el fitxer commons-cli-1.4.jar.


## Estudi sobre les dades 

Les dades que ens entren son correus que poden considerar-se o no spam. Un pas previ al desenvolupament ha de ser el d'evaluar com son majoritàirament aquests correus en longitud. Estudiar la longitud ens ajudarà a veure les dimensions de les dades a processar. També tal i com posa a l'enunciat de la pràctica (com a millora) s'hauria de purgar els correus de mots que no tenen semàntica, tals com proposicions, adverbis o connectors gramaticals. 

Per tant caldrà : 

- **POSAR LA LLARGADA MITJANA DELS EMAILS** 
- **EXCLOURE ELS MOTS QUE NO TENEN SEMÀNTICA I EXPLICAR COM ES FA EL PROCÉS**

## Instruccions per a l'execució 

Per executar el programa mitjançant un paquet jar ens trobarem les següents opcions : 

```{java}
usage: spamizer
 -c <arg>   Usage : -c <spamDir> <hamDir> [-n <int>]
            Receives 2 parameters, A directory with spam mails and a
            directory with ham mails. A calculation for values phi and k
            will be done using a selection for the mails set. The
            selection will be k-fold cross-validation and the heuristic
            method used to calculate phi and k values will be High
            Climbing with random restarts
 -d         Flag that indicates that data must be loaded from local
            database
 -h         Set training mails as ham, adding this argument -s must not be
            present
 -n <arg>   The number of iterations for -c mode execution.
 -p <arg>   Directory where final database will be persisted
 -s         Set training mails as spam, adding this argument -h must not
            be present
 -t <arg>   Directories where training mails in txt are stored, this or
            database argument must be presentyou can set a maximum of 2
            directories in this order : -t <spamDir> <hamDir>. If only one
            dir is set the parameter -h or -s must be included
 -v <arg>   Directories where validation mails in txt are storedyou can
            set a maximum of 2 directories in this order : -v <spamDir>
            <hamDir>.

```

El programa permet la interacció amb el machine learning en diferents modes, el mode entrenament (training) que permet nodrid la base de dades en memòria tant amb una base de dades desada en un fitxer com amb un conjunt finit de correus llegits dins d'un directori.

### Mode Training

Si es pretén llegir un conjunt finit de correus des d'un directori s'ha d'especificar si aquests correus son spam o ham mitjançant els paràmetres -h o -s, el programa no accepta els dos paràmetres a la vegada o la inexistència dels dos en cas que la opció -t (training des d'un directori) estigui especificada. Per part del mode training mitjançant una base de dades no hi ha restricció sobre els paràmetres ham o spam ja que l'estructura de la base de dades ha de ser la mateixa que la que està creada en memòria. 

### Mode Validation

El mode validation reb un conjunt finit de correus procedents d'un directori que s'especifica per paràmetre. Aquest procediment exporta els resultats dels correus llegits en el format TP, FP, TN i FN per tots els correus inserits. 

### Mode Phi and K Evaluation

Reb dos directoris per aquest ordre <dirSpam> <dirHam> en cas que detecti que no els reb o que per el nom del directori els reb malament llençarà una excepció. 

El paràmetre -c realitza una execució per el càlcul dels valors phi i k. S'utilitza el mètode heurístic High Climbing amb random restarts per ajustar-los i el procés de selecció dels correus electrònics és mitjançant un k-fold cross-validation (selecció aleatòria). Reb 2 directoris, el directori spam i el directori ham per aquest ordre. Requereix del paràmetre -n per estipular un nombre finit d'execucions, en cas que no se li assigni el paràmetre -n s'executarà només una vegada. 

El paràmetre -n només acompanya el nombre d'execucions que s'han de produïr utilitzant el paràmetre -c. En cas que no aparegui aquest paràmetre només es realitzarà una sola execució. El valor de referència per phi i k s'assignarà de manera aleatòria. 

### Other parameters

El paràmetre -p estipula que es desi la base de dades en memòria a algun fitxer que pugui ser restaurat en una altre execució per així no perdre el possible entrenament realitzat. 


## Classes i Packages

### Package reader

Es planteja la lectura dels mails com un mòdul a part entenent que en aquest primera versió la lectura es realitza de fitxers i directoris peró podria venir donada per lectures de fluxos de dades o bé de crowlers. 

#### Mòdul Mail Reader

El mòdul mail reader és el mòdul encarregat de llegir els corrues d'una font seleccionada. Es planteja la font amb un patró Strategy, patró que ens permet canviar la font de lectura en temps d'execució. En la implementació actual d'aquesta primera versió aquest mòdul s'alimenta de directoris on a dintre hi han fitxers txt que cada fitxer conté un correu sencer. 

La instanciació del reader reb 2 directoris, el directori ham i el directori spam. 

#### Mòdul Filter

El mòdul filter és una interfície que pot ser implementada per qualsevol mètode de filtrat de paraules que es desitgi. En aquest cas es crea la classe StanfordCoreNLP que s'utilitzarà per parsejar les paraules no desitjades per l'entrenament. Entre altres aquest mòdul neteja els textos de conjuncions, disjuncions, adbervis, preposicions i altres formes gramaticals sense sentit semàntic i que no aporten a la creació del mateix fitlre d'spam. 

#### Mòdul Trainer 

Trainer és el mòdul que permet interactuar amb la base de dades en memòria HSQL. Entre altres permet la inserció dels diferents correus acumolant els nombres d'aparicions de les paraules.   

#### Mòdul MemDB

És un mòdul de control per la base de dades en memòria. 

#### Mòdul Validator



#### Mòdul LocalDB

#### Mòdul NaiveBayes





### MLCore Package

Dins d'aquest package s'hi desa el cor del machine learning. Conté les instàncies de les bases de dades, les classes de validació i les d'entrenament.

#### COMANDES AUXILIARS
Comanda grep que ens serveix per mirar si una paraula ens apareix a un seguit de fitxers.
grep -c paraula directori -d read -r
-c -> busca la paraula al fitxer
-d read -> passes un directori i li dius que llegeixi
-r -> que ho faci de manera recursiva per tots els fitxers del directori