package xcordion.impl.command;

import junit.framework.TestCase;
import junit.framework.Assert;
import org.mockito.Mockito;
import org.junit.Before;
import xcordion.api.Xcordion;
import xcordion.api.XcordionEventListener;
import xcordion.lang.java.JDomTestDocument;
import xcordion.util.DocumentFactory;

public abstract class AbstractCommandTest {
    protected Xcordion xcordion;
    protected XcordionEventListener broadcaster;
    protected JDomTestDocument.JDomTestElement emptyElement;

    @Before
    public void setUp() throws Exception {
        xcordion = Mockito.mock(Xcordion.class);
        broadcaster = Mockito.mock(XcordionEventListener.class);
        Mockito.when(xcordion.getBroadcaster()).thenReturn(broadcaster);

        emptyElement = new JDomTestDocument(DocumentFactory.document("<xml/>")).getRootElement();
    }

    public void runChildren() {
        Assert.fail("WRITE ME");
    }
}
