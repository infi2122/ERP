package Viewers;

import Models.clientOrder;

public class clientOrder_Viewer {

    public void showClientOrder (clientOrder clientOrder){

        System.out.println(" Name: " + clientOrder.getClientName()+
                           " DeliveryDate: " + clientOrder.getDeliveryDate()+
                           " OrderNumber: " + clientOrder.getOrderNum() +
                           " PieceType: " + clientOrder.getPieceType() +
                           " Quantity: " + clientOrder.getQty() +
                           " Penalization: " + clientOrder.getPenDelay());


    }
}
