package Models;

import java.util.ArrayList;

public class rawMaterialOrder {

    private int id;
    private int qty;
    private ArrayList<idResQty> idResQty_vec = new ArrayList<>();
    private int pieceType;
    private int timeToPlaceOrder;
    private int arrivalTime;
    private supplier supplier;

    public rawMaterialOrder(int id,int qty,int manufacturingOrderID, int reservedQty_per_manuID, int orderPlaceTime, int arrivalTime,  int pieceType, supplier supplier) {
        this.id = id;
        this.qty = qty;
        this.idResQty_vec.add(new idResQty(manufacturingOrderID,reservedQty_per_manuID));
        this.timeToPlaceOrder = orderPlaceTime;
        this.arrivalTime = arrivalTime;
        this.pieceType = pieceType;
        this.supplier = supplier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }
    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }
    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public supplier getSupplier() {
        return supplier;
    }
    public void setSupplier(supplier supplier) {
        this.supplier = supplier;
    }

    public ArrayList<idResQty> getIDResQty_vec() {
        return idResQty_vec;
    }

    public void addIDResQty_vec(int manufacturingOrder, int reservedQty) {

        getIDResQty_vec().add(new idResQty(manufacturingOrder,reservedQty));
    }

    public int getPieceType() {
        return pieceType;
    }
    public void setPieceType(int pieceType) {
        this.pieceType = pieceType;
    }

    public int getOrderPlaceTime() {
        return timeToPlaceOrder;
    }
    public void setOrderPlaceTime(int orderPlaceTime) {
        this.timeToPlaceOrder = orderPlaceTime;
    }
}
