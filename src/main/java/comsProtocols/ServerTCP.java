package comsProtocols;

import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerTCP {

    /* Thread timing */
    private int initialDelay = 0;
    private int periodicDelay = 20;

    private ServerSocket serverSocket;

    public void start(int port, sharedResources sharedBuffer) {
        try {

            serverSocket = new ServerSocket(port);

            ScheduledExecutorService scheduler
                    = Executors.newSingleThreadScheduledExecutor();

            scheduler.scheduleAtFixedRate(new clientHandler(serverSocket, sharedBuffer), initialDelay, periodicDelay, TimeUnit.SECONDS);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class clientHandler extends Thread {

        private ServerSocket serverSocket;
        private Socket socket;
        private InputStreamReader isr;
        private BufferedReader br;
        private OutputStreamWriter osw;
        private BufferedWriter bw;

        private sharedResources sharedBuffer;
        private boolean odd = true;

        public clientHandler(ServerSocket serverSocket, sharedResources erp2MES) {
            this.serverSocket = serverSocket;
            this.sharedBuffer = erp2MES;
        }

        public void run() {

            try {
                if (socket == null) {
                    socket = serverSocket.accept();
                    System.out.println("Sucessfully Connected to MES");
                }
                if (odd) {
                    acceptRequest("startTime");
                    acceptRequest("internalOrders");
                    //odd = false;
                } else {
                    createRequest("finishedOrdersTimes");
                    odd = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        private void acceptRequest(String feature) {

            try {
                isr = new InputStreamReader(socket.getInputStream());
                br = new BufferedReader(isr);
                String toSend;
                if (br.readLine().equals(feature)) {

                    switch (feature) {
                        case "startTime":
                            toSend = String.valueOf(sharedBuffer.getStartTime());
                            break;
                        case "internalOrders":
                            toSend = sharedBuffer.getInternalOrdersConcat();
                            break;
                        default:
                            toSend = " ";
                            break;
                    }
                    osw = new OutputStreamWriter(socket.getOutputStream());
                    bw = new BufferedWriter(osw);
                    bw.write(toSend + "\n");
                    bw.flush();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void createRequest(String feature) {
            try {
                osw = new OutputStreamWriter(socket.getOutputStream());
                bw = new BufferedWriter(osw);
                bw.write(feature + "\n");
                bw.flush();
                isr = new InputStreamReader(socket.getInputStream());
                br = new BufferedReader(isr);

                switch (feature) {
                    case "finishedOrdersTimes":
                        sharedBuffer.setFinishedOrdersInfo(br.readLine());
                        break;
                    default:
                        break;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
