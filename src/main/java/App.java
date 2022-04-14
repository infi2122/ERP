import Controllers.ERP;
import UDP.ERP2MES;
import Models.higherDeadline;
import SQL.SQL;
import UDP.multiServer;
import Viewers.ERP_Viewer;

import java.util.ArrayList;
import java.util.concurrent.Executors;

public class App {

    private static int port = 20000;

    public static void main(String args[]) {

        ERP erp = new ERP(new higherDeadline(), new ArrayList<>(), new ERP_Viewer());
        SQL sql = new SQL();

        /* share resource for comunications to MES */
        ERP2MES erp2mes = new ERP2MES();
        /* *************************************** */
        //sql.createSQLtables();

        multiServer server = new multiServer();
        try {
            Executors.newSingleThreadExecutor().submit(() -> server.start(port, erp2mes));
            Thread.sleep(100);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            while (true) {
                Thread.sleep(300);
                erp.countTime();
                erp.checkNewOrders();
                erp.send2MESinteralOrders(erp2mes);
                //erp.displayManufacturingOrders();
                erp.displayInternalOrder();
                erp.placeRawMaterialOrder();
                erp.displayRawMaterialArriving();

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
