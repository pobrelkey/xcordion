package xcordion.visual;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.EOFException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class VisualXcordionServer {
    static final int SERIAL_PORT = 9997;
    private static final int HTTP_PORT = 9999;

    private final Server server;
    private ServerSocket serverSocket;
    private boolean running = true;
    private byte[] latestDocument = null;


    static public void main(String[] args) {
        new VisualXcordionServer().start();
    }

    public VisualXcordionServer() {
        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
            serverSocket = new ServerSocket(SERIAL_PORT, 0, localhost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server = new Server();
    }

    public void start() {
        Thread serialReaderThread = new Thread(new SerialPortReader());
        serialReaderThread.setDaemon(true);
        serialReaderThread.start();

        SocketConnector connector = new SocketConnector();
        connector.setPort(HTTP_PORT);
        server.addConnector(connector);

        ServletHandler servletHandler = new ServletHandler();
        ServletHolder servletHolder = new ServletHolder(new VisualXcordionServlet(this));
        servletHandler.addServletWithMapping(servletHolder, "/");
        server.addHandler(servletHandler);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getLatestDocument() {
        return latestDocument;
    }

    private class SerialPortReader implements Runnable {
        public void run() {
            while (running) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    while (true) {
                        Object o = inputStream.readObject();
                        if (o == null) {
                            break;
                        } else if (o instanceof byte[]) {
                            latestDocument = (byte[]) o;
                        }
                    }
                } catch (EOFException e) {
                    // ignore
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
