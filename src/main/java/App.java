import Controllers.ERP;
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

    private static int portMES = 20000;
    private static int portClientOrders = 54321;

    public static void main(String args[]) {

        /* share resource for comunications  */
        shareResources shareResources = new shareResources();
        /* *************************************** */

        ERP erp = new ERP(new higherDeadline(), new ArrayList<>(), new ERP_Viewer(),shareResources);
        //SQL sql = new SQL();
        //sql.createSQLtables();

        /* TCP/IP for MES communications */
        tcpServer MESserver = new tcpServer();
        MESserver.start(portMES, shareResources);
        /* *************************************** */

        /* UDP Listener for new orders */
        udpServer udpThread = new udpServer();
        udpThread.start(portClientOrders, shareResources);
        /* *************************************** */

        ScheduledExecutorService schedulerERP = Executors.newScheduledThreadPool(2);
        schedulerERP.scheduleAtFixedRate(new myTimer(erp), 0, 1, TimeUnit.SECONDS);
        schedulerERP.scheduleAtFixedRate(new myERP(erp, shareResources), 1, 20, TimeUnit.SECONDS);

    }

    private static class myERP extends Thread {

        private final ERP erp;
        private shareResources shareResources;

        public myERP(ERP erp1, shareResources shared) {
            this.erp = erp1;
            this.shareResources = shared;
        }

        @Override
        public void run() {

            synchronized (erp) {
                erp.checkNewOrders(shareResources.getClientOrders());
                erp.updateStockinSharedResources(shareResources);
                erp.send2MESinteralOrders(shareResources);
                //erp.displayManufacturingOrders();
                erp.displayInternalOrder();
                erp.placeRawMaterialOrder();
                //erp.displayRawMaterialOrdered();
                //erp.displayRawMaterialArriving();
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
