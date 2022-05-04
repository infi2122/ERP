package Models;

import java.util.ArrayList;

public class rawMaterialOrder {

    private int id;
    private int qty;
    private ArrayList<productionInRawMaterials> idResQty_vec = new ArrayList<>();
    private int pieceType;
    private int timeToPlaceOrder;
    private int arrivalTime;
    private supplier supplier;

    public rawMaterialOrder(int id,int qty,int manufacturingOrderID, int reservedQty_per_manuID, int orderPlaceTime,  int pieceType, supplier supplier) {
        this.id = id;
        this.qty = qty;
        this.idResQty_vec.add(new productionInRawMaterials(manufacturingOrderID,reservedQty_per_manuID));
        this.timeToPlaceOrder = orderPlaceTime;
        this.arrivalTime = orderPlaceTime + supplier.getDeliveryTime();
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

    public ArrayList<productionInRawMaterials> getProductionInRawMaterials() {
        return idResQty_vec;
    }

    public void addProductionInRawMaterials(int manufacturingOrder, int reservedQty) {

        getProductionInRawMaterials().add(new productionInRawMaterials(manufacturingOrder,reservedQty));
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
