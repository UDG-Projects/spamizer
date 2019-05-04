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

## Estudi sobre les dades 

Les dades que ens entren son correus que poden considerar-se o no spam. Un pas previ al desenvolupament ha de ser el d'evaluar com son majoritàirament aquests correus en longitud. Estudiar la longitud ens ajudarà a veure les dimensions de les dades a processar. També tal i com posa a l'enunciat de la pràctica (com a millora) s'hauria de purgar els correus de mots que no tenen semàntica, tals com proposicions, adverbis o connectors gramaticals. 

Per tant caldrà : 

- **POSAR LA LLARGADA MITJANA DELS EMAILS** 
- **EXCLOURE ELS MOTS QUE NO TENEN SEMÀNTICA I EXPLICAR COM ES FA EL PROCÉS**

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