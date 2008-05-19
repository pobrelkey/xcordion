package spec.examples;

import org.concordion.integration.junit3.ConcordionTestCase;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class PartialMatchesTest extends ConcordionTestCase {

    private Set<String> usernamesInSystem = new HashSet<String>();
    
    public void setUpUser(String username) {
        usernamesInSystem.add(username);
    }
    
    public SortedSet<String> getSearchResultsFor(String searchString) {
        SortedSet<String> matches = new TreeSet<String>();
        for (String username : usernamesInSystem) {
            if (username.contains(searchString)) {
                matches.add(username);
            }
        }
        return matches;
    }

    public String getMatchFromResults(Set<String> results, String match) {
        for (String s : results) {
            if (s.equals(match)) {
                return match;
            }
        }
        return null;
    }
}
