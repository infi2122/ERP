package UDP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.io.*;

public class multiServer {

    private static final Logger LOG = LoggerFactory.getLogger(multiServer.class);

    private ServerSocket serverSocket;

    public void start(int port,ERP2MES erp2mes) {
        try {

            serverSocket = new ServerSocket(port);
            while (true)
                new EchoClientHandler(serverSocket.accept(),erp2mes).start();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private ERP2MES erp2MES;

        public EchoClientHandler(Socket socket,ERP2MES erp2MES) {
            this.clientSocket = socket;
            this.erp2MES = erp2MES;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    //System.out.println(inputLine);
                    if (".".equals(inputLine)) {
                        out.println("bye");
                        break;
                    }
                    if(inputLine.equals("internalOrders")){

                        out.println(erp2MES.readInERP2MESbuffer());
                        //break;
                    }

                }

                in.close();
                out.close();
                clientSocket.close();

            } catch (IOException e) {
                LOG.debug(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        multiServer server = new multiServer();
        server.start(5555,new ERP2MES());
    }

}
