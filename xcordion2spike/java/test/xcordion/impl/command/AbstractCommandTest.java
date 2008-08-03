package xcordion.impl.command;

import junit.framework.TestCase;
import org.jmock.Mockery;
import org.jmock.Expectations;
import xcordion.api.Xcordion;
import xcordion.api.XcordionEvents;
import xcordion.lang.java.JDomTestDocument;
import xcordion.util.DocumentFactory;

public class AbstractCommandTest extends TestCase {
    protected Mockery context;
    protected Xcordion xcordion;
    protected XcordionEvents broadcaster;
    protected JDomTestDocument.JDomTestElement emptyElement;

    protected void setUp() throws Exception {
        super.setUp();
        context = new Mockery();
        xcordion = context.mock(Xcordion.class);
        broadcaster = context.mock(XcordionEvents.class);

        context.checking(new Expectations() {{
            allowing(xcordion).getBroadcaster();
            will(returnValue(broadcaster));
        }});

        emptyElement = new JDomTestDocument(DocumentFactory.document("<xml/>")).getRootElement();
    }

    protected void tearDown() throws Exception {
        context.assertIsSatisfied();
        super.tearDown();
    }

    public void runChildren() {
        fail("WRITE ME");
    }
}
