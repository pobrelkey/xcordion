package xcordion.util;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;

public class DocumentFactory {
    static public Document document(String s) throws JDOMException {
        try {
            return document(new StringReader(s));
        } catch (IOException e) {
            throw new XcordionBug("StringReader threw an IOException - check out window for flying pigs", e);
        }
    }

    static public Document document(Reader r) throws IOException, JDOMException {
        return document(new InputSource(r));
    }

    static public Document document(InputStream s) throws IOException, JDOMException {
        return document(new InputSource(s));
    }

    static public Document document(InputSource inputSource) throws IOException, JDOMException {
        SAXBuilder builder;
        try {
            // not everyone has jdom-contrib.jar on their classpath, and that's OK
            builder = (SAXBuilder) Class.forName("org.jdom.contrib.input.LineNumberSAXBuilder").newInstance();
        } catch (Throwable e) {
            builder = new SAXBuilder();
        }
        builder.setValidation(false);
        builder.setReuseParser(false);
        return builder.build(inputSource);
    }
}
