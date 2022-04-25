package UDP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class tcpServer {

    /* Thread timing */
    private int initialDelay = 50;
    private int periodicDelay = 60;

    private ServerSocket serverSocket;

    public void start(int port, shareResources erp2mes) {
        try {

            serverSocket = new ServerSocket(port);

            ScheduledExecutorService scheduler
                    = Executors.newSingleThreadScheduledExecutor();

            Runnable task = new EchoClientHandler(serverSocket.accept(), erp2mes);

            scheduler.scheduleAtFixedRate(task, initialDelay, periodicDelay, TimeUnit.SECONDS);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private shareResources erp2MES;

        public EchoClientHandler(Socket socket, shareResources erp2MES) {
            this.clientSocket = socket;
            this.erp2MES = erp2MES;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {

                    if (".".equals(inputLine)) {
                        out.println("bye");
                        break;
                    }
                    if (inputLine.equals("internalOrders")) {

                        out.println(erp2MES.readInERP2MESbuffer());

                    }

                }

                in.close();
                out.close();
                clientSocket.close();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        tcpServer server = new tcpServer();
        server.start(5555, new shareResources());
    }

}
