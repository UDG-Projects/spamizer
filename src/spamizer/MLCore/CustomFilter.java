package spamizer.MLCore;

import spamizer.interfaces.Filter;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class CustomFilter implements Filter {


    @Override
    public HashMap<String, Integer> filterText(String text) {

        //String cleaned = text.replaceAll("[^A-Za-z0-9 ]", " ");
        //cleaned = cleaned.replaceAll("[s|S]ubject[ |:]?[ |:]?", "");
        String[] parts = text.split(" ");
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
