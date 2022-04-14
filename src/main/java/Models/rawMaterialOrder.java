package Models;

import java.util.ArrayList;

public class rawMaterialOrder {

    private int qty;
    private int reserved_qty;
    private int pieceType;
    private int timeToPlaceOrder;
    private int arrivalTime;
    private supplier supplier;

    public rawMaterialOrder(int qty, int reserved_qty, int orderPlaceTime, supplier supplier, int pieceType) {
        this.qty = qty;
        this.reserved_qty = reserved_qty;
        this.timeToPlaceOrder = orderPlaceTime;
        this.arrivalTime = -1;
        this.pieceType = pieceType;
        this.supplier = supplier;
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

    public int getReserved_qty() {
        return reserved_qty;
    }

    public void setReserved_qty(int reserved_qty) {
        this.reserved_qty = reserved_qty;
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
