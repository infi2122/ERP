import Controllers.ERP;
import comsProtocols.ERP_to_MES;
import comsProtocols.shareResources;
import Models.higherDeadline;
import comsProtocols.tcpServer;
import comsProtocols.udpServer;
import Viewers.ERP_Viewer;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class App {

    private static String ERP_IP = "127.0.0.1";
    private static int ERP_to_MES_port = 20000;
    private static int MES_to_ERP_port = 20001;
    private static int portClientOrders = 54321;

    public static void main(String args[]) {

        /* share resource for comunications  */
        shareResources shareResources = new shareResources();
        /* *************************************** */

        ERP erp = new ERP(new higherDeadline(), new ArrayList<>(), new ERP_Viewer(),shareResources);
        //SQL sql = new SQL();
        //sql.createSQLtables();

        /* TCP/IP for MES communications */
        // ERP como cliente -> Para pedir os tempos de produção das encomendas,
        ERP_to_MES erp2mes = new ERP_to_MES();
        erp2mes.openConnection(ERP_to_MES_port,ERP_IP);
        erp.setErp2mes(erp2mes);

        // ERP como servidor -> Para mandar ordens de produção, e sincronização do t=0
        tcpServer ERPserver = new tcpServer();
        ERPserver.start(ERP_to_MES_port, shareResources);
        /* *************************************** */

        /* UDP Listener for new orders */
        udpServer udpThread = new udpServer();
        udpThread.start(portClientOrders, shareResources);
        /* *************************************** */

        ScheduledExecutorService schedulerERP = Executors.newScheduledThreadPool(2);
        schedulerERP.scheduleAtFixedRate(new myTimer(erp), 0, 1, TimeUnit.SECONDS);
        schedulerERP.scheduleAtFixedRate(new myERP(erp), 0, 60, TimeUnit.SECONDS);

    }

    private static class myERP extends Thread {

        private final ERP erp;

        public myERP(ERP erp1) {
            this.erp = erp1;

        }

        @Override
        public void run() {

            synchronized (erp) {
                erp.checkNewOrders();
                erp.send2MESinteralOrders();
                erp.displayInternalOrder();
                erp.displayRawMaterialArriving();
                erp.displayRawMaterialOrdered();

            }
        }

    }

    private static class myTimer extends Thread {

        private final ERP erp;

        public myTimer(ERP erp1) {
            this.erp = erp1;
        }

        @Override
        public void run() {
            synchronized (erp) {
                erp.countTime();
            }
        }
    }

}
