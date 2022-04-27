import Controllers.ERP;
import UDP.shareResources;
import Models.higherDeadline;
import UDP.tcpServer;
import UDP.udpServer;
import Viewers.ERP_Viewer;

import java.net.SocketException;
import java.util.ArrayList;


public class App {

    private static int portMES = 20000;
    private static int portClientOrders = 54321;

    public static void main(String args[]) {

        ERP erp = new ERP(new higherDeadline(), new ArrayList<>(), new ERP_Viewer());
        //SQL sql = new SQL();
        //sql.createSQLtables();

        /* share resource for comunications  */
        shareResources shareResources = new shareResources();
        /* *************************************** */

        /* TCP/IP for MES communications */
        tcpServer MESserver = new tcpServer();
        MESserver.start(portMES, shareResources);
        /* *************************************** */

        /* UDP Listener for new orders */
        udpServer udpThread = new udpServer();
        udpThread.start(portClientOrders, shareResources);
        /* *************************************** */

        try {
            while (true) {
                Thread.sleep(300);
                erp.countTime();
                erp.checkNewOrders(shareResources.getClientOrders());
                erp.send2MESinteralOrders(shareResources);
                //erp.displayManufacturingOrders();
                erp.displayInternalOrder();
                erp.placeRawMaterialOrder();
                //erp.displayRawMaterialOrdered();
                erp.displayRawMaterialArriving();

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
