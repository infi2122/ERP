package Models;

public class clientOrder {

    private String clientName;
    private int orderNum;
    private int pieceType;
    private int qty;
    private int deliveryDate;
    private float penDelay;
    private float penAdvance;

    public clientOrder(String clientName, int orderNum, int pieceType, int qty, int deliveryDate, float penDelay, float penAdvance) {
        this.clientName = clientName;
        this.orderNum = orderNum;
        this.pieceType = pieceType;
        this.qty = qty;
        this.deliveryDate = deliveryDate;
        this.penDelay = penDelay;
        this.penAdvance = penAdvance;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public int getPieceType() {
        return pieceType;
    }

    public void setPieceType(int pieceType) {
        this.pieceType = pieceType;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(int deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public float getPenDelay() {
        return penDelay;
    }

    public void setPenDelay(float penDelay) {
        this.penDelay = penDelay;
    }

    public float getPenAdvance() {
        return penAdvance;
    }

    public void setPenAdvance(float penAdvance) {
        this.penAdvance = penAdvance;
    }


}
