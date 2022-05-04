package Models;


public class manufacturingOrder {

    private int productionID;
    private int expectedRawMaterialArrival;
    private int expectedProdutionStart;
    private int expectedShippingStart;
    private float productionTime;
    private int entryWHarrival_t;
    private int productionStart_t;
    private int productionEnd_t;
    private int shippingStar_t;
    private int shippingEnd_t;
    private clientOrder clientOrder;

    public manufacturingOrder(int productionID, clientOrder clientOrder) {
        this.productionID = productionID;
        this.clientOrder = clientOrder;
    }

    public int getProductionID() {
        return productionID;
    }

    public int getExpectedRawMaterialArrival() {
        return expectedRawMaterialArrival;
    }

    public void setExpectedRawMaterialArrival(int expectedRawMaterialArrival) {
        this.expectedRawMaterialArrival = expectedRawMaterialArrival;
    }

    public int getExpectedProdutionStart() {
        return expectedProdutionStart;
    }

    public void setExpectedProdutionStart(int expectedProdutionStart) {
        this.expectedProdutionStart = expectedProdutionStart;
    }

    public int getExpectedShippingStart() {
        return expectedShippingStart;
    }

    public void setExpectedShippingStart(int expectedShippingStart) {
        this.expectedShippingStart = expectedShippingStart;
    }

    public float getProductionTime() {
        return productionTime;
    }

    public void setProductionTime(float productionTime) {
        this.productionTime = productionTime;
    }

    public int getEntryWHarrival_t() {
        return entryWHarrival_t;
    }

    public void setEntryWHarrival_t(int entryWHarrival_t) {
        this.entryWHarrival_t = entryWHarrival_t;
    }

    public int getProductionStart_t() {
        return productionStart_t;
    }

    public void setProductionStart_t(int productionStart_t) {
        this.productionStart_t = productionStart_t;
    }

    public int getProductionEnd_t() {
        return productionEnd_t;
    }

    public void setProductionEnd_t(int productionEnd_t) {
        this.productionEnd_t = productionEnd_t;
    }

    public int getShippingStar_t() {
        return shippingStar_t;
    }

    public void setShippingStar_t(int shippingStar_t) {
        this.shippingStar_t = shippingStar_t;
    }

    public int getShippingEnd_t() {
        return shippingEnd_t;
    }

    public void setShippingEnd_t(int shippingEnd_t) {
        this.shippingEnd_t = shippingEnd_t;
    }

    public clientOrder getClientOrder() {
        return clientOrder;
    }




}
