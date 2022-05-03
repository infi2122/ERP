package Viewers;

import Controllers.ERP;
import Controllers.manufacturingOrderController;
import Models.productionOrder;
import Models.rawMaterialOrder;
import Models.receiveOrder;
import Models.shippingOrder;

import java.util.ArrayList;

public class ERP_Viewer {

    public void showManufacturingOrders(ArrayList<manufacturingOrderController> arrayList) {
        System.out.println("Details on the Manufacturing Order");
        for (manufacturingOrderController curr : arrayList) {
            curr.getViewer().showManufacturingOrder(curr.getManufacturing_order(), curr.getClientOrderController());
        }
    }

    public void showInternalOrders(ArrayList<receiveOrder> recv, ArrayList<productionOrder> prod, ArrayList<shippingOrder> ship) {
        System.out.println("***** Internal Orders aka MES vectors *****");
        System.out.println("**** Unload Orders ****");

        for (receiveOrder curr : recv) {
            System.out.println("production ID: " + curr.getOrderID() + " starts on day: " + curr.getStartDate() +
                    " Piece to produce: " + curr.getPieceType() + " Quantity: " + curr.getReservedQty());
        }

//        int old_id = 0;
//
//        if (recv.size() != 0) {
//            receiveOrder begin = recv.get(0);
//            receiveOrder end = new receiveOrder(0, 0, 0, 0);
//
//            for (receiveOrder curr : recv) {
//
//                if (old_id == curr.getOrderID()) {
//                    end = curr;
//                } else {
//                    if (end.getOrderID() != curr.getOrderID()) {
//                        System.out.println("productionID: " + begin.getOrderID() + " starts on day: " + begin.getStartDate() + " & ends on day: " + end.getStartDate());
//                        begin = curr;
//                    }
//                    old_id = curr.getOrderID();
//                }
//            }
//            if (old_id == end.getOrderID()) {
//                System.out.println("productionID: " + begin.getOrderID() + " starts on day: " + begin.getStartDate() + " & ends on day: " + end.getStartDate());
//            }
//        }
        int old_id = 0;
        System.out.println("\n**** Production Orders ****");
        if (prod.size() != 0) {

            productionOrder begin2 = prod.get(0);
            productionOrder end2 = new productionOrder(0, 0);

            for (productionOrder curr : prod) {
                if (old_id == curr.getOrderID()) {
                    end2 = curr;
                } else {
                    if (end2.getOrderID() != curr.getOrderID()) {
                        System.out.println("productionID: " + begin2.getOrderID() + " starts on day: " + begin2.getStartDate() + " & ends on day: " + end2.getStartDate());
                        begin2 = curr;
                    }
                    old_id = curr.getOrderID();
                }
            }
            if (old_id == end2.getOrderID()) {
                System.out.println("productionID: " + begin2.getOrderID() + " starts on day: " + begin2.getStartDate() + " & ends on day: " + end2.getStartDate());
            }
        }

        System.out.println("\n**** Shipping Orders ****");
        old_id = 0;
        if (ship.size() != 0) {

            shippingOrder begin3 = ship.get(0);
            shippingOrder end3 = new shippingOrder(0, 0);

            for (shippingOrder curr : ship) {

                if (old_id == curr.getOrderID()) {
                    end3 = curr;
                } else {
                    if (end3.getOrderID() != curr.getOrderID()) {
                        System.out.println("productionID: " + begin3.getOrderID() + " starts on day: " + begin3.getStartDate() + " & ends on day: " + end3.getStartDate());
                        begin3 = curr;
                    }
                    old_id = curr.getOrderID();
                }
            }
            if (old_id == end3.getOrderID()) {
                System.out.println("productionID: " + begin3.getOrderID() + " starts on day: " + begin3.getStartDate() + " & ends on day: " + end3.getStartDate());
            }
        }
    }

    public void showRawMaterialArriving(ArrayList<rawMaterialOrder> orderArrayList, long time) {

        for (rawMaterialOrder currMaterial : orderArrayList) {
            if (currMaterial.getArrivalTime() == (int) time / 60) {
                System.out.println("***** Raw Material Arriving *****");
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
