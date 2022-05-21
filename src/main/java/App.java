import Controllers.ERP;
import SQL.dbFunctions;
import comsProtocols.sharedResources;
import Models.higherDeadline;
import comsProtocols.ServerTCP;
import comsProtocols.udpServer;
import Viewers.ERP_Viewer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class App {

    private static final int ERP_to_MES_port = 20000;
    private static final int portClientOrders = 54321;

    public static void main(String[] args) {

        /* share resource for comunications  */
        sharedResources shareResources = new sharedResources();
        shareResources.setInternalOrdersConcat("empty");
        shareResources.setFinishedOrdersInfo("empty");
        /* *************************************** */

        dbFunctions sql = new dbFunctions();
        sql.dropSQLtables();
        sql.createSQLtables();
        ERP erp = new ERP(new higherDeadline(), new ArrayList<>(), new ERP_Viewer(), shareResources, sql);

        /* UDP Listener for new orders */
        udpServer udpThread = new udpServer();
        udpThread.start(portClientOrders, shareResources, 0, 5);
        /* *************************************** */

        /* TCP/IP for MES communications */
        ServerTCP server = new ServerTCP();
        server.start(ERP_to_MES_port, shareResources, 0, 59);
        /* *************************************** */

        // ATENÇAO AOS PERIODOS DAS THREADS, alguma alteração pode levar a coisas estranhas!!!
        ScheduledExecutorService schedulerERP = Executors.newScheduledThreadPool(3);
        schedulerERP.scheduleAtFixedRate(new myTimer(erp), 0, 1, TimeUnit.SECONDS);
        schedulerERP.scheduleAtFixedRate(new myERP(erp), 0, 60, TimeUnit.SECONDS);
        schedulerERP.scheduleAtFixedRate(new myERPClientOrders(erp), 5, 20, TimeUnit.SECONDS);

        Scanner input = new Scanner(System.in);
        // CONTEXT MENU
        boolean exit = true;
        while (exit) {
            erp.displayMenu();
            switch (input.nextInt()) {
                case 1 -> erp.displayInternalOrder();
                case 2 -> erp.displayRawMaterialOrdered();
                case 3 -> erp.displayManufacturingOrdersCosts();
                case 0 -> exit = false;
                default -> {
                }
            }
            erp.cleanTerminal();
            if (exit)
                erp.displayCurrentDay();
        }

        System.exit(0);

    }

    private static class myERP extends Thread {

        private final ERP erp;

        public myERP(ERP erp1) {
            this.erp = erp1;
        }

        @Override
        public void run() {

            synchronized (erp) {
                //  erp.displayInternalOrder();
                // erp.displayRawMaterialOrdered();
                // erp.displayManufacturingOrdersCosts();
                erp.displayRawMaterialArriving();
            }
        }

    }

    private static class myERPClientOrders extends Thread {
        private final ERP erp;

        public myERPClientOrders(ERP erp2) {
            this.erp = erp2;
        }

        @Override
        public void run() {
            synchronized (erp) {
                erp.checkNewOrders();
                erp.send2MESinteralOrders();
                erp.calculateCosts();
                erp.updateDB();
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
