package Models;

public class rawMaterialOrderID_QtyRawMaterial {

    private int rawMaterialOrderID;
    private int qtyRawMaterial;

    public rawMaterialOrderID_QtyRawMaterial(int rawMaterialOrderID, int qtyRawMaterial) {
        this.rawMaterialOrderID = rawMaterialOrderID;
        this.qtyRawMaterial = qtyRawMaterial;
    }

    public int getRawMaterialOrderID() {
        return rawMaterialOrderID;
    }

    public int getQtyRawMaterial() {
        return qtyRawMaterial;
    }
}
