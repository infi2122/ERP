package Models;

public class productionInRawMaterials {

    private int orderID;
    private int reservedQty;

    public productionInRawMaterials(int orderID, int reservedQty) {
        this.orderID = orderID;
        this.reservedQty = reservedQty;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getReservedQty() {
        return reservedQty;
    }

    public void setReservedQty(int reservedQty) {
        this.reservedQty = reservedQty;
    }
}
