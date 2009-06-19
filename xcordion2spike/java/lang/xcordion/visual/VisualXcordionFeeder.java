package xcordion.visual;

import xcordion.api.XcordionEventListener;
import xcordion.api.events.XcordionEvent;
import xcordion.lang.java.JDomTestDocument;

import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class VisualXcordionFeeder implements XcordionEventListener {
    private static final String PROPERTY_VISUAL_HOST = "xcordion.visual.host";
    private static final String PROPERTY_VISUAL_PORT = "xcordion.visual.port";

    private final JDomTestDocument testDocument;
    private Socket socket = null;
    private ObjectOutputStream serializer = null;

    public VisualXcordionFeeder(JDomTestDocument testDocument) {
        this.testDocument = testDocument;
    }

    private void feed() {
        try {
            InetAddress host;
            String hostName = System.getProperty(PROPERTY_VISUAL_HOST);
            if (hostName != null) {
                host = InetAddress.getByName(hostName);
            } else {
                host = InetAddress.getLocalHost();
            }
            int port = VisualXcordionServer.NOTIFICATIONS_PORT;
            String portString = System.getProperty(PROPERTY_VISUAL_PORT);
            if (portString != null) {
                port = Integer.parseInt(portString);
            }
            socket = new Socket(host, port);
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

    public void handleEvent(XcordionEvent xcordionEvent) {
        feed();
    }
}
