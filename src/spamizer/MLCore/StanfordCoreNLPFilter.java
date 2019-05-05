package spamizer.MLCore;


import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

/**
 * Filtre de paraules que no ens interessen.
 * Utilitzem la llibreria StanfordNLP que realitza una tokenització assignant unes propietats a cada paraula.
 *
 */
public class StanfordCoreNLPFilter implements Filter {

    StanfordCoreNLP pipeline;

    public StanfordCoreNLPFilter(){
        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        properties.setProperty("coref.algorithm", "neural");
        pipeline = new StanfordCoreNLP(properties);
    }

    @Override
    public HashMap<String, Integer> filterText(String text) {

        Annotation document = new Annotation(text);
        List<String> lemmas = new LinkedList<>();

        pipeline.annotate(document);

        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence : sentences){
            for(CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)){
                String partOfSpeech = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                if((token.lemma().length() > 1 || token.lemma() == "i") && (partOfSpeech.startsWith("N") ||
                        partOfSpeech.startsWith("R") ||
                        partOfSpeech.startsWith("V") ||
                        partOfSpeech.startsWith("J") ||
                        partOfSpeech.startsWith("PR") ||
                        partOfSpeech.startsWith("DT"))){
                    lemmas.add(token.get(CoreAnnotations.LemmaAnnotation.class));
                }
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