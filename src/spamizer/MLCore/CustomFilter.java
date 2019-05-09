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

        System.out.println(text);
        String cleaned = text.replace("[^A-Za-z0-9 ]", "");
        cleaned = cleaned.replace("\\.", "");
        System.out.println(cleaned);

        String[] parts = text.split(" ");
        HashMap<String, Integer> mails = new HashMap<>();
        for(String s : parts){
            if(mails.containsKey(s)){
                mails.put(s, mails.get(s));
            }
            else{
                mails.put(s, 1);
            }
        }

        return mails;

    }
}
