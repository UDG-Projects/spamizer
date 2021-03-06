package main.java.spamizer.filters;


import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.CoreMap;
import main.java.spamizer.interfaces.Filter;

import java.util.*;

/**
 * Filtre de paraules que no ens interessen.
 * Utilitzem la llibreria StanfordNLP que realitza una tokenització assignant unes propietats a cada paraula.
 *
 */
public class StanfordCoreNLPFilter implements Filter {

    private static StanfordCoreNLPFilter filter;
    StanfordCoreNLP pipeline;

    private StanfordCoreNLPFilter(){
        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        properties.setProperty("coref.algorithm", "neural");
        properties.setProperty("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
        properties.put("threads", "8");


        pipeline = new StanfordCoreNLP(properties);
    }

    public static StanfordCoreNLPFilter getInstance(){
        if(filter == null){
            filter = new StanfordCoreNLPFilter();
        }
        return filter;
    }

    @Override
    public HashMap<String, Integer> filterText(String text) {

        String cleaned = text.replaceAll("[s|S][u|U][b|B][j|J][e|E][c|C][t|T][ |:]?[ |:]?", "");
        cleaned = cleaned.replaceAll("cc[ |:]?[ |:]?", "");
        cleaned = cleaned.replaceAll("from[ |:]?[ |:]?", "");
        cleaned = cleaned.replaceAll("to[ |:]?[ |:]?", "");
        Annotation document = new Annotation(cleaned);
        List<String> lemmas = new LinkedList<>();

        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence : sentences){
            for(CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)){
                //String partOfSpeech = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                if(token.lemma().length() > 1) {
                    lemmas.add(token.lemma());
                }
                else{
                    lemmas.add(token.value());
                }
                /*if((token.lemma().length() > 1 || token.lemma() == "i") && (partOfSpeech.startsWith("N") ||
                        partOfSpeech.startsWith("R") ||
                        partOfSpeech.startsWith("V") ||
                        partOfSpeech.startsWith("J") ||
                        partOfSpeech.startsWith("PR") //||
                        //partOfSpeech.startsWith("DT")
                )){*/

                //}

            }
        }


        HashMap<String, Integer> lemmaMap = new HashMap<>();
        for(String lemma : lemmas){
            if(lemmaMap.containsKey(lemma)){
                lemmaMap.replace(lemma, lemmaMap.get(lemma) + 1);
            }
            else {
                lemmaMap.put(lemma, 1);
            }
        }

        return lemmaMap;
    }
}
