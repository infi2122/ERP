import Controllers.ERP;
import UDP.ERP2MES;
import Models.higherDeadline;
import SQL.SQL;
import UDP.MESserver;
import UDP.clientOrderServer;
import Viewers.ERP_Viewer;

import java.util.ArrayList;
import java.util.concurrent.Executors;

public class App {

    private static int portMES = 20000;
    private static int portClientOrders = 54321;

    public static void main(String args[]) {

        ERP erp = new ERP(new higherDeadline(), new ArrayList<>(), new ERP_Viewer());
        SQL sql = new SQL();

        /* share resource for comunications to MES */
        ERP2MES erp2mes = new ERP2MES();
        /* *************************************** */
        //sql.createSQLtables();

        MESserver server = new MESserver();
        try {
            Executors.newSingleThreadExecutor().submit(() -> server.start(portMES, erp2mes));
            Thread.sleep(100);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        clientOrderServer clientOrderServer = new clientOrderServer();
        try {
            Executors.newSingleThreadExecutor().submit(() -> clientOrderServer.start(portClientOrders));
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
                //erp.displayRawMaterialOrdered();
                erp.displayRawMaterialArriving();

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
