package org.xcordion.ide.intellij;

import junit.framework.TestCase;
import org.xcordion.ide.intellij.story.StoryRunnerAction;
import org.xcordion.ide.intellij.story.StoryRunnerActionHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoryPageParserTest extends TestCase {

    private static final String FIRST_TEST_HTML = "docs/provide/nvn/SuccessfullyProvisionNvnWithAddressReference.html";
    private final static String FQ_NAME = "active-documentation/order-provisioning/" + FIRST_TEST_HTML;
    private final static String FQ_NAME_WITH_DOTS = "../../../" + FQ_NAME;

    private final static String STORY_PAGE = "<html>\n" +
            "<head>\n" +
            "    <title>Story Overview Page</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <h2>Story Overview</h2>\n" +
            "    <ul>\n" +
            "        <li><a href=\"../../../" + FQ_NAME + "\">Successfully Provision NVN with Address Reference</a></li>\n" +
            "        <li><a href=\"../../../../anything/test/test/src/org/test/FirstXcordion.html\">First Test</a></li>\n" +
            "        <li><a href=\"../../../../anything/test/test/src/org/test/SecondXcordion.html\">Second Test</a></li>\n" +
            "        <li><a href=\"../../../../anything/test/test/src/org/test/ThirdXcordion.html\">Third Test</a></li>\n" +
            "    </ul>\n" +
            "</body>\n" +
            "</html>";

    public void testParsingForTestNames() {
        Pattern pattern = StoryRunnerActionHandler.FULLY_QUALIFIED_NAME_PATTERN;
        Matcher matcher = pattern.matcher("anything/test/test/src/org/test/Test.html");
        String result = null;
        if (matcher.find()) {
            result = matcher.group(1);
        }
        assertEquals("org.test.Test", result.replace("/", "."));
    }

    public void testParsingForFullyQualifiedTestNames() {
        Pattern pattern = StoryRunnerActionHandler.CONCORDION_TEST_FILE_NAMES;

        Matcher matcher = pattern.matcher(STORY_PAGE);
        String result = null;
        if (matcher.find()) {
            result = matcher.group(1);
        }
        assertEquals(FQ_NAME, result);
    }

    public void testParsingForCorrectCss() {
        Pattern pattern = StoryRunnerAction.STYLESHEET_PATTERN;
        Matcher matcher = pattern.matcher("<link rel=\"stylesheet\" type=\"text/css\" href=\"http://confluence.is.uk.easynet.net:9454/download/attachments/29667/story_overview.css\" />");
        assertTrue(matcher.find());
    }

    public void testReplacesCorrectHrefInStoryPage() throws Exception {
        Pattern pattern = Pattern.compile("href=\"([../]*[\\w-]+/[\\w-]+/" + FIRST_TEST_HTML + ")\"");
        Matcher matcher = pattern.matcher(STORY_PAGE);
        assertTrue(matcher.find());

        assertEquals(1, matcher.groupCount());
        assertEquals(FQ_NAME_WITH_DOTS, matcher.group(1));
    }

}
