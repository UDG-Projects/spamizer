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

        /*ByteBuffer byteBuff = StandardCharsets.UTF_8.encode(text);// .UTF-8.encode(s);
        String partial = new String( byteBuff.array(), "UTF-8");*/
        text = text.replaceAll("((?![\\x00-\\x7F]).)", "");
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
