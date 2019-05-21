package main.java.spamizer.filters;

import main.java.spamizer.interfaces.Filter;

import java.util.HashMap;

public class CustomFilter implements Filter {


    @Override
    public HashMap<String, Integer> filterText(String text) {

        //String cleaned = text.replaceAll("[^A-Za-z0-9 ]", " ");

        String cleaned = text.replaceAll("[s|S]ubject[ |:]?[ |:]?", "");
        cleaned = cleaned.replaceAll("cc[ |:]?[ :]?", "");
        cleaned = cleaned.replaceAll("from[ |:]?[ :]?", "");
        cleaned = cleaned.replaceAll("to[ |:]?[ :]?", "");
        cleaned = cleaned.replaceAll("re[ |:]?[ :]?", "");
        cleaned = cleaned.replaceAll("[\\n\\r,;]"," ");
        cleaned = cleaned.toLowerCase();
        String[] parts = cleaned.split(" ");
        HashMap<String, Integer> mails = new HashMap<>();
        for(String s : parts){
            if(s.length() > 0) {
                if (mails.containsKey(s)) {
                    mails.put(s, mails.get(s));
                } else {
                    mails.put(s, 1);
                }
            }
        }

        return mails;

    }
}
