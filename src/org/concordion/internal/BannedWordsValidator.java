package org.concordion.internal;

import java.util.ArrayList;
import java.util.List;

public class BannedWordsValidator implements ExpressionValidator {
    
    private static final List<String> BANNED_WORDS = new ArrayList<String>() {{
       add("click");
       add("doubleClick");
       add("enter");
       add("open");
       add("press");
       add("type");
    }};
    
    public void validate(String expression) {
        for (String bannedWord : BANNED_WORDS) {
            if (expression.startsWith(bannedWord)) {
                throw new RuntimeException(
                          "Expression starts with a banned word ('" + bannedWord + "').\n"
                        + "This word strongly suggests you are writing a script.\n"
                        + "Concordion is a specification tool not a scripting tool.\n"
                        + "See the website http://www.concordion.org for more information.");
            }
        }
    }

}
