package Models;

import java.util.ArrayList;

public class supplier {

    private String name;
    private int deliveryTime;
    private int minQty;
    private ArrayList<rawPiece> rawPieces = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public int getMinQty() {
        return minQty;
    }

    public void setMinQty(int minQty) {
        this.minQty = minQty;
    }

    public ArrayList<rawPiece> getRawPieces() {
        return rawPieces;
    }

    public void addNewRawPiece(rawPiece NeWRawPiece) {
        getRawPieces().add(NeWRawPiece);
    }
}
