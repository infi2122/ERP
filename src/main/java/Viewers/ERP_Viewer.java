package Viewers;

import Controllers.manufacturingOrderController;
import Models.productionInRawMaterials;
import Models.productionOrder;
import Models.rawMaterialOrder;
import Models.shippingOrder;

import java.util.ArrayList;

public class ERP_Viewer {

    public void showManufacturingOrders(ArrayList<manufacturingOrderController> arrayList) {
        System.out.println("Details on the Manufacturing Order");
//        for (manufacturingOrderController curr : arrayList) {
//            curr.getViewer().showManufacturingOrder(curr.getManufacturing_order(), curr.getClientOrderController());
//        }
    }

    public void showInternalOrders(ArrayList<rawMaterialOrder> recv, ArrayList<productionOrder> prod, ArrayList<shippingOrder> ship, int currDay) {
        if (recv.size() == 0 && prod.size() == 0 && ship.size() == 0)
            return;
        System.out.println("**************** Internal Orders  ****************");
        System.out.println("--> Raw Material Orders <--");

        for (rawMaterialOrder curr : recv) {
            if(curr.getArrivalTime() >= currDay )
            System.out.println("rawMaterial ID: " + curr.getID() + " arrives on day: " + curr.getArrivalTime() +
                    " Type: " + curr.getPieceType() + " Quantity: " + curr.getQty() );

            for (productionInRawMaterials curr2 : curr.getProductionInRawMaterials()) {
                System.out.println("     manufacuringID: " + curr2.getOrderID() + " quantity: " + curr2.getReservedQty());
            }
        }

        System.out.println("\n--> Production Orders <--");
        for (productionOrder curr : prod) {
            if(curr.getStartProdutionDate() >= currDay )
            System.out.println("manufacuringID: " + curr.getManufacturingID() + " starts on day: " + curr.getStartProdutionDate()
            + " Type: " + curr.getFinalType() + " Qty: " + curr.getQty());
        }

        System.out.println("\n--> Shipping Orders <--");
        for (shippingOrder curr : ship) {
            if(curr.getStartShippingDate() >= currDay )
            System.out.println("manufacuringID: " + curr.getManufacturingID() + " starts on day: " + curr.getStartShippingDate()
            + " Qty: " + curr.getQty());
        }
        System.out.println("**************************************************");
        System.out.println(" ");

    }


    public void showRawMaterialArriving(ArrayList<rawMaterialOrder> rawMaterialOrders, int currDay) {
        boolean first = true;
        for (rawMaterialOrder currMaterial : rawMaterialOrders) {
            if (currMaterial.getArrivalTime() == currDay) {
                if (first) {
                    System.out.println("**************** Raw Material ARRIVAL  ****************");
                    System.out.println("********** place the pieces in the conveyor  **********");
                    first = false;
                }
                System.out.println("Piece Type: " + currMaterial.getPieceType() + " Quantity: " + currMaterial.getQty() );
            }
        }
        if(!first){
        System.out.println("**************************************************");
        System.out.println(" ");}
    }

    public void showRawMaterialsOrdered(ArrayList<rawMaterialOrder> arrayList) {
        System.out.println("**************** Raw Material Ordered  ****************");
        for (rawMaterialOrder curr : arrayList) {


            System.out.println(" Supplier: " + curr.getSupplier().getName() +
                    " to be placed on day: " + curr.getOrderPlaceTime() +
                    " of type: " + curr.getPieceType() +
                    " quantity: " + curr.getQty());
            for(productionInRawMaterials curr2: curr.getProductionInRawMaterials()){
                System.out.println("     manufacturingID: " + curr2.getOrderID() + " Qty: " + curr2.getReservedQty());
            }
        }
        System.out.println("*******************************************************");
    }

    public void cleanScreen (){

        for (int i=0; i<10;i++)
            System.out.println(" ");
    }

}
