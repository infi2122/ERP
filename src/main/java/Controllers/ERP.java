package Controllers;

import UDP.ERP2MES;
import Models.*;
import Readers.suppliersList;
import Readers.xmlReader;
import Viewers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class ERP {

    // VARIABLES
    private long startTime = 0;
    private int countdays = 0;
    private int dayBefore = -1;
    private final boolean MethodExecuted[] = {true, true, true, true, true, true, true};

    private static class exe {
        public static final int oneDay = 60;
        public static final int internalOrders_target = 2; // #days to send orders for MES
    }

    private static class executionPeriocity {
        public static final int checkOrder_t = 5;
        public static final int placeRawMaterialOrder_t = 50;
        public static final int displayManufacturingOrders_t = 8;
        public static final int displayRawMaterialArriving_t = 30;
        public static final int displayInternalOrders_t = 60;
        public static final int sendInternalOrdersToMES = exe.oneDay;
    }


    // ******* ATTRIBUTES ********
    private long currentTime;
    private orderCriterium orderCriterium;
    private ArrayList<manufacturingOrderController> manufacturingOrders;
    private ERP_Viewer erp_viewer;
    private ArrayList<receiveOrder> receiveOrder;
    private ArrayList<productionOrder> productionOrder;
    private ArrayList<shippingOrder> shippingOrder;


    // ******* CONSTRUCTOR ********
    public ERP(orderCriterium orderCriterium, ArrayList<manufacturingOrderController> manufacturingOrderControllers, ERP_Viewer erp_viewer) {
        this.orderCriterium = orderCriterium;
        this.manufacturingOrders = manufacturingOrderControllers;
        this.erp_viewer = erp_viewer;
        this.receiveOrder = new ArrayList<>();
        this.productionOrder = new ArrayList<>();
        this.shippingOrder = new ArrayList<>();
    }

    public long getTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public orderCriterium getOrderCriterium() {
        return orderCriterium;
    }

    public void setOrderCriterium(orderCriterium orderCriterium) {
        this.orderCriterium = orderCriterium;
    }

    public ArrayList<manufacturingOrderController> getManufacturingOrders() {
        return manufacturingOrders;
    }

    public boolean addDetailedOrder(manufacturingOrderController MyNewDetailedOrder) {
        return getManufacturingOrders().add(MyNewDetailedOrder);
    }

    public ERP_Viewer getErp_viewer() {
        return erp_viewer;
    }

    public void setErp_viewer(ERP_Viewer erp_viewer) {
        this.erp_viewer = erp_viewer;
    }

    public ArrayList<receiveOrder> getReceiveOrder() {
        return receiveOrder;
    }

    public boolean addReceiveOrder(receiveOrder NewReceiveOrder) {
        return getReceiveOrder().add(NewReceiveOrder);

    }

    public ArrayList<productionOrder> getProductionOrder() {
        return productionOrder;
    }

    public boolean addProductionOrder(productionOrder NewProductionOrder) {
        return getProductionOrder().add(NewProductionOrder);
    }

    public ArrayList<shippingOrder> getShippingOrder() {
        return shippingOrder;
    }

    public boolean addShippingOrder(shippingOrder NewShippingOrder) {
        return getShippingOrder().add(NewShippingOrder);

    }


    // ******** METHODS *********

    // counts every second
    public void countTime() {
        if (startTime == 0) {
            System.out.println("Current Day: " + countdays);
            startTime = System.currentTimeMillis();

        } else {
            long time = System.currentTimeMillis();

            if (time >= getTime() * 1000 + startTime + 1000) {
                setCurrentTime((time - startTime) / 1000);

                if ((int) getTime() / exe.oneDay > countdays) {
                    countdays = (int) getTime() / exe.oneDay;
                    System.out.println("Current Day: " + countdays);
                }
                Arrays.fill(MethodExecuted, false);
            }
        }
    }

    /**
     * @return return if exists new orders
     */
    public boolean checkNewOrders() {

        boolean detectedNewOrders = false;

        if ((getTime() % executionPeriocity.checkOrder_t == 0 && !MethodExecuted[0]) || (countdays == 0 && !MethodExecuted[0])) {

            xmlReader reader = new xmlReader();
            ArrayList<clientOrder> ordersVec = reader.readOrder();

            for (clientOrder currOrder : ordersVec) {

                if (!check_if_clientOrder_have_manufacturingOrder(currOrder, getManufacturingOrders())) {
                    detectedNewOrders = true;
                    manufacturingOrderController MyNewDetailedOrder =
                            new manufacturingOrderController(new manufacturingOrder(getManufacturingOrders().size(), currOrder),
                                    new manufacturingOrder_Viewer(),
                                    new clientOrderController(currOrder, new clientOrder_Viewer()));

                    // Error testing
                    if (!addDetailedOrder(MyNewDetailedOrder))
                        System.out.println("Error adding new detailed order to manufacturingOrderController !");


                    Vector<Integer> plan = getOrderCriterium().scheduler(MyNewDetailedOrder, getTime());
                    //int rawmaterialdelivery = estimatePlan(MyNewDetailedOrder);
                    createInternalOrders(plan, MyNewDetailedOrder.getManufacturing_order().getProductionID());

                    // plan.get(0) retorna o unload_begin
                    createRawMaterialOrder(plan.get(0), MyNewDetailedOrder);


                }
            }


            getOrderCriterium().ordering(getManufacturingOrders());
            MethodExecuted[0] = true;

        }
        return detectedNewOrders;
    }

    /**
     * Todos os dias faz as encomendas necessárias aos fornecedores
     */
    public void placeRawMaterialOrder() {

        if (getTime() % executionPeriocity.placeRawMaterialOrder_t == 0 && !MethodExecuted[3]) {

            for (manufacturingOrderController curr : getManufacturingOrders()) {
                for (rawMaterialOrder currMaterial : curr.getManufacturing_order().getRawMaterialOrder()) {
                    if (currMaterial.getOrderPlaceTime() == getTime() / exe.oneDay) {
                        currMaterial.setArrivalTime(currMaterial.getSupplier().getDeliveryTime() + (int) (getTime() / exe.oneDay));
                    }
                }

            }
            MethodExecuted[3] = true;
        }
    }


    private boolean check_if_clientOrder_have_manufacturingOrder
            (clientOrder order, ArrayList<manufacturingOrderController> manufacturingOrders) {
        for (manufacturingOrderController curr : manufacturingOrders) {

            if (curr.getManufacturing_order().getClientOrder().getOrderNum() == order.getOrderNum()) {
                if (curr.getManufacturing_order().getClientOrder().getClientName().equals(order.getClientName())) {
                    return true;
                }

            }

        }
        return false;
    }


    /**
     * @param vector
     * @param productionID
     */
    public void createInternalOrders(Vector<Integer> vector, int productionID) {
        // Para o ReceiveOrder
        int i = 0;
        while (i <= vector.get(1) - vector.get(0)) {
            getReceiveOrder().add(new receiveOrder(productionID, vector.get(0) + i));
            i++;
        }
        // Para o ProductionOrder
        i = 0;
        while (i <= vector.get(3) - vector.get(2)) {
            getProductionOrder().add(new productionOrder(productionID, vector.get(2) + i));
            i++;
        }
        // Para o ShippingOrder
        i = 0;
        while (i <= vector.get(5) - vector.get(4)) {
            getShippingOrder().add(new shippingOrder(productionID, vector.get(4) + i));
            i++;
        }

    }

    // Para checkar encomendas realizadas à espera de serem entregues
    public ArrayList<rawMaterialOrder> allMaterialsOrdered() {

        ArrayList<rawMaterialOrder> arrayList = new ArrayList<>();

        for (manufacturingOrderController currOrder : getManufacturingOrders()) {
            for (rawMaterialOrder currMaterial : currOrder.getManufacturing_order().getRawMaterialOrder()) {
                arrayList.add(currMaterial);

            }
        }

        return arrayList;
    }

    /**
     * @param deadline
     * @param order
     * @return #dias para a encomenda ser feita
     */

    public int createRawMaterialOrder(int deadline, manufacturingOrderController order) {

        // Primeiro checka se tem peças em stock nos armazens
        int stock = 0;
        // Depois checka as peças que estao encomendadas, para ver se chega
        int pieces_ordered = 0;
        //Por fim encomenda o execedente

        // Encontrar o fornecedor face ao tempo
        ArrayList<supplier> suppliers = new suppliersList().availableSuppliers();
        supplier choosenSupplier = new supplier();

        for (supplier curr : suppliers) {
            if (curr.getDeliveryTime() + (int) (getTime() / 60) < deadline) {
                choosenSupplier = curr;
                break;
            }
        }
        // ao fazer o break garante-se que se escolhe sempre o fornecedor que demora mais a entregar logo o que sai mais barato

        // encomendar o nº de peças necessárias
        int qty_to_order = 0;
        if (choosenSupplier.getMinQty() > order.getManufacturing_order().getClientOrder().getQty()) {
            qty_to_order = choosenSupplier.getMinQty();
        }

        // Saber qual o tipo de peça 1 -> P6 e P8 ou 2-> Outras
        int pieceType_to_order = 0;
        if (order.getManufacturing_order().getClientOrder().getPieceType() == 6 ||
                order.getManufacturing_order().getClientOrder().getPieceType() == 8) {
            pieceType_to_order = 1;
        } else {
            pieceType_to_order = 2;
        }
        //Teoricamente deve de haver um vetor para colocar as encomendas a serem feitas no dia certo,
        // pois correm o risco de chegarem mais cedo

        //Place Order
        if (!order.getManufacturing_order().addNewRawMaterialOrder(
                new rawMaterialOrder(
                        qty_to_order,
                        order.getManufacturing_order().getClientOrder().getQty(),
                        deadline - choosenSupplier.getDeliveryTime(),
                        choosenSupplier,
                        pieceType_to_order

                )))
            System.out.println("Error creating new RawMaterial Order on class:ERP method:makeRawMaterialOrder");

        //order.displayRawMaterialOrders();

        return choosenSupplier.getDeliveryTime();
    }


    public void send2MESinteralOrders(ERP2MES erp2MES) {

        if (getTime() % executionPeriocity.sendInternalOrdersToMES == 0 && getTime() != 0 && !MethodExecuted[6]) {
            String recvString = new String();
            String prodString = new String();
            String shipString = new String();

            for (receiveOrder curr : getReceiveOrder()) {
                if (curr.getStartDate() <= (int) (getTime() / exe.oneDay) + exe.internalOrders_target) {
                    recvString = recvString.concat(Integer.toString(curr.getOrderID()));
                    recvString = recvString.concat("@");
                    recvString = recvString.concat(Integer.toString(curr.getStartDate()));
                    recvString = recvString.concat("/");
                }
            }
            recvString = recvString.concat("-");

            for (productionOrder curr : getProductionOrder()) {
                if (curr.getStartDate() <= (int) (getTime() / exe.oneDay) + exe.internalOrders_target) {
                    prodString = prodString.concat(Integer.toString(curr.getOrderID()));
                    prodString = prodString.concat("@");
                    prodString = prodString.concat(Integer.toString(curr.getStartDate()));
                    prodString = prodString.concat("/");
                }
            }
            prodString = prodString.concat("-");

            for (shippingOrder curr : getShippingOrder()) {
                if (curr.getStartDate() <= (int) (getTime() / exe.oneDay) + exe.internalOrders_target) {
                    shipString = shipString.concat(Integer.toString(curr.getOrderID()));
                    shipString = shipString.concat("@");
                    shipString = shipString.concat(Integer.toString(curr.getStartDate()));
                    shipString = shipString.concat("/");
                }
            }
            shipString = shipString.concat("-");
            String returnStr = new String();

            returnStr = returnStr.concat(recvString);
            returnStr = returnStr.concat(prodString);
            returnStr = returnStr.concat(shipString);

            System.out.println(returnStr);
            erp2MES.printInERP2MESbuffer(returnStr);
            MethodExecuted[6] = true;
            return;
        }

    }

    // ******** VIEW METHODS *********

    public void displayManufacturingOrders() {
        if (getTime() % executionPeriocity.displayManufacturingOrders_t == 0 && getTime() != 0 && !MethodExecuted[1]) {
            getErp_viewer().showManufacturingOrders(getManufacturingOrders());
            MethodExecuted[1] = true;
        }

    }

    public void displayRawMaterialArriving() {
        if (getTime() % executionPeriocity.displayRawMaterialArriving_t == 0 && !MethodExecuted[2])
            getErp_viewer().showRawMaterialArriving(getManufacturingOrders(), getTime());
        MethodExecuted[2] = true;


    }

    public void displayInternalOrder() {
        if (dayBefore != countdays && countdays != 0) {
            getErp_viewer().showInternalOrders(getReceiveOrder(), getProductionOrder(), getShippingOrder());
            dayBefore = countdays;

        }
    }

}
