package org.xcordion.ide.intellij.story;

import junit.framework.TestCase;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.Project;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import java.util.Collections;
import java.util.List;

public class StoryPageResultsTest extends TestCase {
        private final static String STORY_PAGE = "<html>\n" +
            "<head>\n" +
            "    <title>Story Overview Page</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <h2>Story Overview</h2>\n" +
            "    <ul>\n" +
            "        <li><a href=\"../../../../anything/test/test/test/test/test/test/org/xcordion/ide/intellij/story/StoryPageResults.html\">First Test</a></li>\n" +
            "        <li><a href=\"../../../../anything/test/org/test/SecondXcordion.html\">Second Test</a></li>\n" +
            "        <li><a href=\"../../../../anything/test/org/test/ThirdXcordion.html\">Third Test</a></li>\n" +
            "    </ul>\n" +
            "</body>\n" +
            "</html>";

    private Mockery mockery;

    @Override
    public void setUp(){
        mockery = new Mockery();
        mockery.setImposteriser(ClassImposteriser.INSTANCE);
    }

    public void testSave() {
        TestResultLogger junitBuildLogger = new TestResultLogger(getClass().getName());

        final String eventMessage = "." + StoryPageResults.getJavaTmpDirectory() + "some-new-href";
        final BuildEvent event = mockery.mock(BuildEvent.class);

        mockery.checking(new Expectations(){{
            oneOf(event).getPriority();         will(returnValue(Project.MSG_INFO));
            exactly(1).of(event).getMessage();  will(returnValue(eventMessage));
        }});

        junitBuildLogger.messageLogged(event);
        List<TestResultLogger> testResults = Collections.singletonList(junitBuildLogger);
        StoryPageResults results = new StoryPageResults("SomeFileName.html", STORY_PAGE, testResults);

        results.save();
        String saveResult = results.getFinalText();
        String expectedHrefText = "href=\"" + eventMessage.substring(1) + "\"";
        assertTrue(saveResult.contains(expectedHrefText));
    }
}
