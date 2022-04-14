package Controllers;

import Models.clientOrder;
import Viewers.clientOrder_Viewer;

public class clientOrderController {

    private Models.clientOrder clientOrder;
    private clientOrder_Viewer viewer;

    public clientOrderController(Models.clientOrder clientOrder, clientOrder_Viewer viewer) {
        this.clientOrder = clientOrder;
        this.viewer = viewer;
    }

    public clientOrder getClientOrder() {
        return clientOrder;
    }
    public void setClientOrder(clientOrder clientOrder) {
        this.clientOrder = clientOrder;
    }

    public clientOrder_Viewer getViewer() {
        return viewer;
    }
    public void setViewer(clientOrder_Viewer viewer) {
        this.viewer = viewer;
    }

}
