package Models;

public class stockUpdater {

    private int orderID;
    private int pieceType;
    private int qty;

    public stockUpdater(int orderID, int pieceType, int qty) {
        this.orderID = orderID;
        this.pieceType = pieceType;
        this.qty = qty;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getPieceType() {
        return pieceType;
    }

    public int getQty() {
        return qty;
    }

}
