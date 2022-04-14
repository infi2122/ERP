package Controllers;

import Models.*;
import Viewers.*;

import java.util.ArrayList;

// cada nova encomenda do cliente implica criar uma nova manufacturingOrder que sera introduzida na Arraylist de ERP

public class manufacturingOrderController {

    // ******* ATTRIBUTES ********
    private manufacturingOrder manufacturing_order;
    private manufacturingOrder_Viewer viewer;
    private clientOrderController clientOrderController;


    // ******* CONSTRUCTOR ********
    public manufacturingOrderController(manufacturingOrder manufacturingOrder,manufacturingOrder_Viewer viewer, clientOrderController clientOrderController) {
        this.manufacturing_order = manufacturingOrder;
        this.viewer = viewer;
        this.clientOrderController = clientOrderController;
    }

    public manufacturingOrder getManufacturing_order() {
        return manufacturing_order;
    }
    public void setManufacturing_order(manufacturingOrder manufacturing_order) {
        this.manufacturing_order = manufacturing_order;
    }

    public manufacturingOrder_Viewer getViewer() {
        return viewer;
    }
    public void setViewer(manufacturingOrder_Viewer viewer) {
        this.viewer = viewer;
    }

    public clientOrderController getClientOrderController() {
        return clientOrderController;
    }
    public void setClientOrderController(clientOrderController clientOrderController) {
        this.clientOrderController = clientOrderController;
    }


    // ******** METHODS *********


    // ******** VIEWER METHODS *********

    public void displaySuppliersItems(ArrayList<supplier> arrayList){
        getViewer().showSuppliersItems(arrayList);

    }

    public void displayRawMaterialOrders(){

        for (rawMaterialOrder curr : getManufacturing_order().getRawMaterialOrder()){
            getViewer().showRawMaterialOrder(curr);
        }
    }






}
