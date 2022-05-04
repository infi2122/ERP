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

    public void showInternalOrders(ArrayList<rawMaterialOrder> recv, ArrayList<productionOrder> prod, ArrayList<shippingOrder> ship) {
        System.out.println("***** Internal Orders aka MES vectors *****");
        System.out.println("**** Raw Material Orders ****");

        for (rawMaterialOrder curr : recv) {
            System.out.println("*** rawMaterial ID: " + curr.getID() + " arrives on day: " + curr.getArrivalTime() +
                    " Type: " + curr.getPieceType() + " Quantity: " + curr.getQty() +" ***");

            for(productionInRawMaterials curr2: curr.getProductionInRawMaterials()){
                System.out.println("**** manufacuringID: " + curr2.getOrderID() + " quantity: " + curr2.getReservedQty() + " ****" );
            }
        }

        System.out.println("\n**** Production Orders ****");
        for (productionOrder curr : prod) {
            System.out.println("manufacuringID: " + curr.getManufacturingID() + " starts on day: " + curr.getStartProdutionDate());
        }

        System.out.println("\n**** Shipping Orders ****");
        for (shippingOrder curr : ship) {
            System.out.println("manufacuringID: " + curr.getManufacturingID() + " starts on day: " + curr.getStartShippingDate());
        }
    }


    public void showRawMaterialArriving(ArrayList<rawMaterialOrder> rawMaterialOrders, int currDay) {
        boolean first = true;
        for (rawMaterialOrder currMaterial : rawMaterialOrders) {
            if (currMaterial.getArrivalTime() == currDay) {
                if (first) {
                    System.out.println("***** Raw Material Arriving --> Should put the pieces in the conveyor *****");
                    first = false;
                }
                System.out.println("** Piece Type: " + currMaterial.getPieceType() + " Quantity: " + currMaterial.getQty() + " **");
            }
        }


    }

    public void showRawMaterialsOrdered(ArrayList<rawMaterialOrder> arrayList) {
        for (rawMaterialOrder curr : arrayList) {
            System.out.println("***** Raw Material Ordered *****");
            System.out.println("** Supplier: " + curr.getSupplier().getName() +
                    " to be placed on day: " + curr.getOrderPlaceTime() +
                    " of type: " + curr.getPieceType() +
                    " quantity: " + curr.getQty() +
                    " **");
        }
    }


}
