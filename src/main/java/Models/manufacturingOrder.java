package Models;


import java.util.ArrayList;

public class manufacturingOrder {

    private int productionID;
    private float productionTime;
    private int beginDate;
    private int endDate;
    private clientOrder clientOrder;
//    private ArrayList<rawMaterialOrder> rawMaterialOrder = new ArrayList<>();

    public manufacturingOrder(int productionID, Models.clientOrder clientOrder) {
        this.productionID = productionID;
        this.clientOrder = clientOrder;
    }

    public int getProductionID() {
        return productionID;
    }

    public void setProductionID(int productionID) {
        this.productionID = productionID;
    }

//    public ArrayList<rawMaterialOrder> getRawMaterialOrder() {
//        return rawMaterialOrder;
//    }
//
//    public boolean addNewRawMaterialOrder(rawMaterialOrder rawMaterialOrder) {
//        if (getRawMaterialOrder().add(rawMaterialOrder))
//            return true;
//        else
//            return false;
//    }

    public float getProductionTime() {
        return productionTime;
    }

    public void setProductionTime(float productionTime) {
        this.productionTime = productionTime;
    }

    public int getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(int beginDate) {
        this.beginDate = beginDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public clientOrder getClientOrder() {
        return clientOrder;
    }

    public void setClientOrder(clientOrder clientOrder) {
        this.clientOrder = clientOrder;
    }



}
