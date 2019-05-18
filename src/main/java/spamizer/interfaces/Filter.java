package main.java.spamizer.interfaces;

import java.util.HashMap;
public interface Filter {

    HashMap<String, Integer> filterText(String text);


}
