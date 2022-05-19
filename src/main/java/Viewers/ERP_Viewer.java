package Viewers;

import Models.*;

import java.util.ArrayList;

public class ERP_Viewer {

    public void showInternalOrders(ArrayList<rawMaterialOrder> recv, ArrayList<productionOrder> prod, ArrayList<shippingOrder> ship, int currDay) {
        if (recv.size() == 0 && prod.size() == 0 && ship.size() == 0)
            return;

        //showInternalOrdersHistory(recv,prod,ship,currDay);
        System.out.println("************************ Internal Orders  *************************");
        System.out.println("---------------------> Raw Material Orders <-----------------------");

        for (rawMaterialOrder curr : recv) {
            //if(curr.getArrivalTime() >= currDay )
            System.out.println(" rawMaterial ID: " + curr.getID() + " arrives on day: " + curr.getArrivalTime() +
                    " Type: " + curr.getPieceType() + " Quantity: " + curr.getQty());

            for (productionInRawMaterials curr2 : curr.getProductionInRawMaterials()) {
                System.out.println("     manufacturingID: " + curr2.getOrderID() + " quantity: " + curr2.getReservedQty());
            }

        }

        System.out.println("\n----------------------> Production Orders <-----------------------");
        for (productionOrder curr : prod) {
            // if (curr.getStartProdutionDate() >= currDay)
            System.out.println(" manufacturingID: " + curr.getManufacturingID() + " starts on day: " + curr.getStartProdutionDate()
                    + " Type: " + curr.getFinalType() + " Qty: " + curr.getQty());
        }

        System.out.println("\n------------------------> Shipping Orders <-----------------------");
        for (shippingOrder curr : ship) {
            // if (curr.getStartShippingDate() >= currDay)
            System.out.println(" manufacturingID: " + curr.getManufacturingID() + " starts on day: " + curr.getStartShippingDate()
                    + " Qty: " + curr.getQty());
        }
        System.out.println("*******************************************************************");
        System.out.println(" ");

    }


    public void showRawMaterialArriving(ArrayList<rawMaterialOrder> rawMaterialOrders, int currDay) {
        boolean first = true;
        for (rawMaterialOrder currMaterial : rawMaterialOrders) {
            if (currMaterial.getArrivalTime() == currDay) {
                if (first) {
                    System.out.println("+--------------------- Raw Material ARRIVAL  ---------------------+");
                    System.out.println("+--------------- place the pieces in the conveyor  ---------------+");

                    first = false;
                }
                System.out.println("     Piece Type: " + currMaterial.getPieceType() + " Quantity: " + currMaterial.getQty());
            }
        }
        if (!first) {

            System.out.println("-------------------------------------------------------------------");
            System.out.println(" ");
        }
    }

    public void showRawMaterialsOrdered(ArrayList<rawMaterialOrder> arrayList) {
        if (arrayList.size() > 0) {
            System.out.println("********************** Raw Material Ordered  **********************");

            for (rawMaterialOrder curr : arrayList) {

                System.out.println(" Supplier: " + curr.getSupplier().getName() +
                        " to be placed on day: " + curr.getOrderPlaceTime() +
                        " of type: " + curr.getPieceType() +
                        " quantity: " + curr.getQty());
                for (productionInRawMaterials curr2 : curr.getProductionInRawMaterials()) {
                    System.out.println("     manufacturingID: " + curr2.getOrderID() + " Qty: " + curr2.getReservedQty());
                }
            }
            System.out.println("*******************************************************************");
        }
    }

    public void showManufacturingOrdersCosts(ArrayList<manufacturingOrder> arrayList, ArrayList<rawMaterialOrder> rawMaterialOrders) {
        if (arrayList.size() > 0) {
            System.out.println("*********************** Manufacturing Costs  **********************");

            for (manufacturingOrder curr : arrayList) {
                System.out.println(" manufacturingID: " + curr.getProductionID() +
                        " total Cost: " + curr.getTotalCost() +"€");
                System.out.println("     Client: " + curr.getClientOrder().getClientName() +
                        " Quantity: " + curr.getClientOrder().getQty() +
                        " of type: P" + curr.getClientOrder().getPieceType() +
                        " for day: " + curr.getClientOrder().getDeliveryDate());
                supplier tempSupplier = new supplier();
//                rawMaterialOrder tempRawMaterial = null;
                for (rawMaterialOrder curr2 : rawMaterialOrders) {
                    for (productionInRawMaterials curr3 : curr2.getProductionInRawMaterials()) {
                        if (curr3.getOrderID() == curr.getProductionID()) {
                            tempSupplier = curr2.getSupplier();
                            break;
                        }
                    }
                }
                System.out.println("        Supplier: " + tempSupplier.getName());

            }
            System.out.println("*******************************************************************");
        }
    }


    public void cleanScreen() {

        for (int i = 0; i < 4; i++)
            System.out.println(" ");
    }

}
