package Models;

public class shippingOrder {

    private int manufacturingID;
    private int quantity;
    private int startShippingDate;

    public shippingOrder(int manufacturingID, int quantity, int startShippingDate) {
        this.manufacturingID = manufacturingID;
        this.quantity = quantity;
        this.startShippingDate = startShippingDate;
    }

    public int getManufacturingID() {
        return manufacturingID;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getStartShippingDate() {
        return startShippingDate;
    }
}
