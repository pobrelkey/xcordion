package xcordion.lang.java;

import junit.framework.TestCase;
import org.jdom.Document;
import xcordion.util.DocumentFactory;

public class JDomTestDocumentTest extends TestCase {

    static private final String FOO_URI = "http://www.foo.int/";
    static private final String TEST_DOCUMENT =
            "<xml xmlns:foo=\"" + FOO_URI + "\">\n" +
            "\t<a>Apple</a>\n" +
            "\t<b color=\"red\">Ball</b>\n" +
            "\t<c>Cookie <x>(good enough for me)</x></c>\n" +
            "\t<d foo:bar=\"baz\" foo:answer=\"42\">Deoxyribonucleic Acid</d>\n" +
            "</xml>";

    private JDomTestDocument testDoc;

    protected void setUp() throws Exception {
        super.setUp();
        Document doc = DocumentFactory.document(TEST_DOCUMENT);
        testDoc = new JDomTestDocument(doc);
    }

    public void testSimpleHappyPath() throws Exception {
        JDomTestDocument.JDomTestElement rootElement = testDoc.getRootElement();

        assertEquals("xml", rootElement.getLocalName());
        assertEquals((Integer) 1, rootElement.getStartLine());
        assertEquals(4, rootElement.getChildren().size());

        JDomTestDocument.JDomTestElement a = rootElement.getChildren().get(0);
        assertEquals("a", a.getLocalName());
        assertEquals("Apple", a.getValue());
        assertEquals((Integer) 2, a.getStartLine());

        JDomTestDocument.JDomTestElement b = rootElement.getChildren().get(1);
        assertEquals("b", b.getLocalName());
        assertEquals("Ball", b.getValue());
        assertEquals((Integer) 3, b.getStartLine());
        assertEquals("red", b.getAttribute(null, "color"));

        JDomTestDocument.JDomTestElement c = rootElement.getChildren().get(2);
        assertEquals("c", c.getLocalName());
        assertEquals("Cookie (good enough for me)", c.getValue());
        assertEquals((Integer) 4, c.getStartLine());
        assertEquals(1, c.getChildren().size());
        JDomTestDocument.JDomTestElement x = c.getChildren().get(0);
        assertEquals("x", x.getLocalName());
        assertEquals("(good enough for me)", x.getValue());
        assertEquals((Integer) 4, x.getStartLine());

        JDomTestDocument.JDomTestElement d = rootElement.getChildren().get(3);
        assertEquals("d", d.getLocalName());
        assertEquals("Deoxyribonucleic Acid", d.getValue());
        assertEquals((Integer) 5, d.getStartLine());
        assertEquals("baz", d.getAttribute(FOO_URI, "bar"));
        assertEquals(42, d.getIntAttribute(FOO_URI, "answer"));
    }

    public void testSetText() throws Exception {
        JDomTestDocument.JDomTestElement c = testDoc.getRootElement().getChildren().get(2);
        c.getChildren().get(0).setText("(a sometimes food)");
        assertEquals("<c>Cookie <x>(a sometimes food)</x></c>", c.asXml());
        assertEquals("Cookie (good enough for me)", c.getValue());  // should return original value
    }

    public void testInsertChildAfter() {
        // TODO WRITEME
//        fail("WRITE ME");
    }
    public void testRemove() {
        // TODO WRITEME
//        fail("WRITE ME");
    }
    // TODO: any other methods?

}
