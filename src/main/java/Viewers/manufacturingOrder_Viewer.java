package Viewers;

import Controllers.clientOrderController;
import Models.manufacturingOrder;
import Models.rawMaterialOrder;
import Models.rawPiece;
import Models.supplier;

import java.util.ArrayList;

public class manufacturingOrder_Viewer {

    public void showManufacturingOrder(manufacturingOrder order, clientOrderController clientOrderController) {
        System.out.print("Production Time: " + order.getProductionTime());
        clientOrderController.getViewer().showClientOrder(order.getClientOrder());
    }

    public void showSuppliersItems(ArrayList<supplier> suppliers) {

        // Kind of viewer dos suppliers do .xml
        for (supplier curr : suppliers) {
            System.out.println("Name: " + curr.getName() + " MinQty: " + curr.getMinQty() + " DeliveryTime: " + curr.getDeliveryTime());
            for (rawPiece curr2 : curr.getRawPieces()) {
                System.out.print(" PieceType: " + curr2.getType() + " UnitCost: " + curr2.getUnitCost());
                System.out.println();
            }

        }
    }

    public void showRawMaterialOrder(rawMaterialOrder order) {
        System.out.println("Raw Material Order");
        System.out.println("Supplier: " + order.getSupplier().getName() +
                " Delivery:" + order.getSupplier().getDeliveryTime() + " days" +
                " Quantity Ordered: " + order.getQty() +
                " Type: "+order.getPieceType() +
                " Place order in day: " + order.getOrderPlaceTime() +
                " Free amount: " +(order.getQty()-order.getReserved_qty()));
    }

}
