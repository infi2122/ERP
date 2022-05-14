package comsProtocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class udpServer {

    /* Thread timing */
    private int initialDelay = 0;
    private int periodicDelay = 1;

    private DatagramSocket socket;

    public void start(int port, sharedResources sharedBuffer) {

        try {
            socket = new DatagramSocket(port);

            ScheduledExecutorService scheduler
                    = Executors.newSingleThreadScheduledExecutor();

            Runnable task =  new Handler(socket,sharedBuffer);

            scheduler.scheduleAtFixedRate(task,initialDelay,periodicDelay, TimeUnit.SECONDS);

        } catch (SocketException e) {
            e.printStackTrace();

        }
    }


    private static class Handler extends Thread {

        DatagramSocket socket;
        private sharedResources buffer;

        Handler(DatagramSocket socket, sharedResources sharedBuffer) {
            this.socket = socket;
            this.buffer = sharedBuffer;
        }

        public void run() {

            byte[] rbuf = new byte[2048];
            DatagramPacket rpkt = new DatagramPacket(rbuf, rbuf.length);

            try {

                socket.receive(rpkt);
            } catch (IOException e) {
                e.printStackTrace();
            }

            InetAddress address = rpkt.getAddress();
            int port = rpkt.getPort();
            rpkt = new DatagramPacket(rbuf, rbuf.length, address, port);

            String received
                    = new String(rpkt.getData(), rpkt.getOffset(), rpkt.getLength()).trim();
            buffer.setClientOrders(received);

        }
    }

//    public static void main(String args[]) {
//        udpServer server = new udpServer();
//        server.start(54321, new shareResources());
//    }

}
