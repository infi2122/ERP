package Models;

import java.util.ArrayList;

public class productionOrder {

    private int manufacturingID;
    private int quantity;
    private int finalType;
    private int startProdutionDate;
    private ArrayList<rawMaterialOrderID_QtyRawMaterial> rawMaterialOrderID_qtyRawMaterials;

    public productionOrder(int manufacturingID, int quantity, int finalType, int startProdutionDate, ArrayList<rawMaterialOrderID_QtyRawMaterial> rawMaterialOrderID_qtyRawMaterials) {
        this.manufacturingID = manufacturingID;
        this.quantity = quantity;
        this.finalType = finalType;
        this.startProdutionDate = startProdutionDate;
        this.rawMaterialOrderID_qtyRawMaterials = new ArrayList<>(rawMaterialOrderID_qtyRawMaterials);
    }

    public int getManufacturingID() {
        return manufacturingID;
    }

    public int getQty() {
        return quantity;
    }

    public int getFinalType() {
        return finalType;
    }

    public int getStartProdutionDate() {
        return startProdutionDate;
    }

    public ArrayList<rawMaterialOrderID_QtyRawMaterial> getRawMaterialOrderID_qtyRawMaterials() {
        return rawMaterialOrderID_qtyRawMaterials;
    }
}
