package Models;


public class manufacturingOrder {

    private int productionID;
    private int expectedRawMaterialArrival;
    private int expectedProdutionStart;
    private int expectedShippingStart;
    private int meanSFS_t;
    private int meanProduction_t;
    private int finalDay;
    private int totalCost;
    private clientOrder clientOrder;

    public manufacturingOrder(int productionID, clientOrder clientOrder) {
        this.productionID = productionID;
        this.clientOrder = clientOrder;
        this.meanSFS_t = 0;
        this.meanProduction_t = 0;
        this.finalDay = 0;
        this.totalCost = 0;
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

    public int getMeanSFS_t() {
        return meanSFS_t;
    }

    public void setMeanSFS_t(int meanSFS_t) {
        this.meanSFS_t = meanSFS_t;
    }

    public int getMeanProduction_t() {
        return meanProduction_t;
    }

    public void setMeanProduction_t(int meanProduction_t) {
        this.meanProduction_t = meanProduction_t;
    }

    public int getFinalDay() {
        return finalDay;
    }

    public void setFinalDay(int finalDay) {
        this.finalDay = finalDay;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public clientOrder getClientOrder() {
        return clientOrder;
    }


}
