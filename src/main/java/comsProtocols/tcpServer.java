package comsProtocols;

import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class tcpServer {

    /* Thread timing */
    private int initialDelay = 0;
    private int periodicDelay = 55;

    private ServerSocket serverSocket;

    public void start(int port, shareResources erp2mes) {
        try {

            serverSocket = new ServerSocket(port);

            ScheduledExecutorService scheduler
                    = Executors.newSingleThreadScheduledExecutor();

            scheduler.scheduleAtFixedRate(new EchoClientHandler(serverSocket, erp2mes), initialDelay, periodicDelay, TimeUnit.SECONDS);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class EchoClientHandler extends Thread {
        private ServerSocket serverSocket;
        private PrintWriter out;
        private BufferedReader in;
        private shareResources erp2MES;

        public EchoClientHandler(ServerSocket socket, shareResources erp2MES) {
            this.serverSocket = socket;
            this.erp2MES = erp2MES;
        }

        public void run() {
            try {
                Socket clientSocket = serverSocket.accept();

                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);


                String inputLine;
                while ((inputLine = in.readLine()) != null) {

                    if (inputLine.equals("internalOrders")) {
                        out.println(erp2MES.readInERP2MESbuffer());
                    }
                    if (inputLine.equals("time")) {
                        out.println(erp2MES.getStartTime());
                    }
                    if(inputLine.equals("stockUpdate")){
                        out.println(erp2MES.getStockUpdate());
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
