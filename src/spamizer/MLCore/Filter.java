package spamizer.MLCore;

import java.util.HashMap;
public interface Filter {

    HashMap<String, Integer> filterText(String text);


}
