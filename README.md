# Spamizer

## Base de dades

S'utilitza una base de dades en memòria anomenada [HyperSQL](http://hsqldb.org/).

## Instal·lació del JDBC Driver per HSQLDB.

És necessari utilitzar jetbrains intellijidea per realitzar aquest tutorial.  

A la pestanya superior dreta de l'intellijidea hi ha Database. Per descarregar el driver simplement s'ha de fer com si et connectessis a la base de daes desplegant la pestanya i prement sobre el signe + seleccionant la base de dades HSQLDB. Un cop en la finestra de configuració de la connexió selecciona el text blau a la part inferior que diu descarregar drivers. 

Un cop descarregats els drivers ja tenim el que necessitem, podem tencar la finestra. Selecciona file -> project structure, el menú libraries i afegeix una nova llibreria amb el signe + que apareix a la columna entre el menú i la lateral dreta. Selecciona el fitxer hsqldb.jar situat a l'arrel del projecte. 

Play i a gaudir!! Happy Coding!!

## Estudi sobre les dades 

Les dades que ens entren son correus que poden considerar-se o no spam. Un pas previ al desenvolupament ha de ser el d'evaluar com son majoritàirament aquests correus en longitud. Estudiar la longitud ens ajudarà a veure les dimensions de les dades a processar. També tal i com posa a l'enunciat de la pràctica (com a millora) s'hauria de purgar els correus de mots que no tenen semàntica, tals com proposicions, adverbis o connectors gramaticals. 

Per tant caldrà : 

- **POSAR LA LLARGADA MITJANA DELS EMAILS** 
- **EXCLOURE ELS MOTS QUE NO TENEN SEMÀNTICA I EXPLICAR COM ES FA EL PROCÉS**

 