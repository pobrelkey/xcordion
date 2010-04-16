package xcordion.test;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RunWith(XCordionRunner.class)
public class JUnit4Test {

    static private Map<String, Set<String>> SONG_CATALOG = new HashMap<String, Set<String>>(){{
        put("Big River", new HashSet<String>(){{
            add("Johnny Cash");
            add("Grateful Dead");
        }});
        put("My Sharona", new HashSet<String>(){{
            add("The Knack");
            add("Nirvana");
        }});
    }};

    public boolean artistDoesSong(String artist, String song){
        return SONG_CATALOG.containsKey(song) && SONG_CATALOG.get(song).contains(artist);
    }

}
