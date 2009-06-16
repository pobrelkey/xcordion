package xcordion.visual;

import xcordion.api.XcordionEvents;
import xcordion.api.TestElement;
import xcordion.api.RowNavigator;
import xcordion.api.ResourceReference;
import xcordion.lang.java.JDomTestDocument;

import java.util.List;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class VisualXcordionFeeder implements XcordionEvents {
    private final JDomTestDocument testDocument;
    private final List<ResourceReference<JDomTestDocument.JDomTestElement>> resourceReferences;
    private Socket socket = null;
    private ObjectOutputStream serializer = null;

    public VisualXcordionFeeder(JDomTestDocument testDocument, List<ResourceReference<JDomTestDocument.JDomTestElement>> resourceReferences) {
        this.testDocument = testDocument;
        this.resourceReferences = resourceReferences;
    }

    private void feed() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            socket = new Socket(localhost, VisualXcordionServer.SERIAL_PORT);
            serializer = new ObjectOutputStream(socket.getOutputStream());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            testDocument.write(out);
            serializer.writeObject(out.toByteArray());

            serializer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void begin(TestElement target) {
        feed();
    }

    public void succesfulSet(TestElement target, String expression, Object value) {
        feed();
    }

    public void exception(TestElement target, String expression, Throwable e) {
        feed();
    }

    public void succesfulExecute(TestElement target, String expression) {
        feed();
    }

    public void insertText(TestElement target, String expression, Object result) {
        feed();
    }

    public void successfulAssertBoolean(TestElement target, String expression, boolean value) {
        feed();
    }

    public void failedAssertBoolean(TestElement target, String expression, boolean expected, Object actual) {
        feed();
    }

    public void successfulAssertEquals(TestElement target, String expression, Object expected) {
        feed();
    }

    public void failedAssertEquals(TestElement target, String expression, Object expected, Object actual) {
        feed();
    }

    public void successfulAssertContains(TestElement target, String expression, Object expected, Object actual) {
        feed();
    }

    public void failedAssertContains(TestElement target, String expression, Object expected, Object actual) {
        feed();
    }

    public void missingRow(RowNavigator row) {
        feed();
    }

    public void surplusRow(RowNavigator row) {
        feed();
    }

    public void end(TestElement target) {
        feed();
    }
}