package Controllers;

import comsProtocols.sharedResources;
import Models.*;
import Readers.suppliersList;
import Readers.xmlReader;
import Viewers.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class ERP {

    // VARIABLES
    private long startTime = 0;
    private int countdays = 0;
    private static final int productionPrice_t = 1;

    public final int oneDay = 60;
    public final int internalOrders_target = 1; // #days to send orders for MES

    // ******* ATTRIBUTES ********
    private long currentTime;
    private sharedResources shareResources;
    private orderCriterium orderCriterium;
    private ArrayList<manufacturingOrder> manufacturingOrders;

    private ArrayList<rawMaterialOrder> rawMaterialOrders;
    private ArrayList<productionOrder> productionOrders;
    private ArrayList<shippingOrder> shippingOrders;

    private Comparators arraysOrdering = new Comparators();
    private ERP_Viewer erp_viewer;

    // ******* CONSTRUCTOR ********
    public ERP(orderCriterium orderCriterium, ArrayList<manufacturingOrder> manufacturingOrders,
               ERP_Viewer erp_viewer, sharedResources sharedResources) {
        this.orderCriterium = orderCriterium;
        this.manufacturingOrders = manufacturingOrders;
        this.erp_viewer = erp_viewer;
        this.shareResources = sharedResources;
        this.rawMaterialOrders = new ArrayList<>();
        this.productionOrders = new ArrayList<>();
        this.shippingOrders = new ArrayList<>();


    }


    public long getTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public sharedResources getShareResources() {
        return shareResources;
    }

    public orderCriterium getOrderCriterium() {
        return orderCriterium;
    }

    public void setOrderCriterium(orderCriterium orderCriterium) {
        this.orderCriterium = orderCriterium;
    }

    public ArrayList<manufacturingOrder> getManufacturingOrders() {
        return manufacturingOrders;
    }

    public boolean addmanufacturingOrder(manufacturingOrder MyNewDetailedOrder) {
        return getManufacturingOrders().add(MyNewDetailedOrder);
    }

    public ERP_Viewer getErp_viewer() {
        return erp_viewer;
    }

    public void setErp_viewer(ERP_Viewer erp_viewer) {
        this.erp_viewer = erp_viewer;
    }


    public ArrayList<rawMaterialOrder> getRawMaterialOrders() {
        return rawMaterialOrders;
    }

    public void addNewRawMaterialOrder(rawMaterialOrder newRawMaterialOrder) {
        getRawMaterialOrders().add(newRawMaterialOrder);
        arraysOrdering.rawOrdering(getRawMaterialOrders());
    }

    public ArrayList<productionOrder> getProductionOrders() {
        return productionOrders;
    }

    public void addNewProductionOrder(productionOrder newProductionOrder) {
        getProductionOrders().add(newProductionOrder);
        arraysOrdering.prodOrdering(getProductionOrders());
    }

    public ArrayList<shippingOrder> getShippingOrders() {
        return shippingOrders;
    }

    public void addNewShippingOrders(shippingOrder newShippingOrder) {
        getShippingOrders().add(newShippingOrder);
        arraysOrdering.shipOrdering(getShippingOrders());
    }

    // Inner Classes

    // ******** METHODS *********

    // counts every second
    public void countTime() {

        if (startTime == 0) {
            startTime = System.currentTimeMillis();
            shareResources.setStartTime(startTime);
            System.out.println("Current Day: " + countdays);

        } else {
            long time = System.currentTimeMillis();

            if (time >= getTime() * 1000 + startTime + 1000) {
                setCurrentTime((time - startTime) / 1000);
                if ((int) getTime() / oneDay > countdays) {
                    countdays = (int) getTime() / oneDay;
                    getErp_viewer().cleanScreen();
                    System.out.println("Current Day: " + countdays);
                }
            }
        }

    }

    /**
     * Checks if exists new orders in the stack of share resources of communications
     * If exists, add orders to the system
     */
    public void checkNewOrders() {

        xmlReader reader = new xmlReader();
        ArrayList<clientOrder> ordersVec = reader.readOrder(getShareResources().getClientOrders());
        if (ordersVec == null)
            return;

        for (clientOrder currOrder : ordersVec) {

            if (!check_if_clientOrder_have_manufacturingOrder(currOrder, getManufacturingOrders())) {

                int orderID = getManufacturingOrders().size();

                manufacturingOrder MyNewDetailedOrder = new manufacturingOrder(orderID, currOrder);

                // Error testing
                if (!addmanufacturingOrder(MyNewDetailedOrder))
                    System.out.println("Error adding new detailed order to manufacturingOrderController !");

                Vector<Integer> plan = getOrderCriterium().scheduler(MyNewDetailedOrder, getTime());

                // SET manufacturing plan on manufacturing order
                // SET expected Raw Material Arrival
                MyNewDetailedOrder.setExpectedRawMaterialArrival(plan.get(0));
                // SET expected production start
                MyNewDetailedOrder.setExpectedProdutionStart(plan.get(2));
                // SET expected shipping start
                MyNewDetailedOrder.setExpectedShippingStart(plan.get(4));

                // Associa as RawmaterialOrders com diferentes quantidades de materias primas
                // retorna essa relação
                ArrayList<rawMaterialOrderID_QtyRawMaterial> vec = createRawMaterialOrder(MyNewDetailedOrder);

                // Cria as PorductionOrders
                createProductionOrders(MyNewDetailedOrder, vec);

                // Cria as Shipping Orders
                createShippingOrders(MyNewDetailedOrder);
            }
        }

        getOrderCriterium().ordering(getManufacturingOrders());

    }

    private boolean check_if_clientOrder_have_manufacturingOrder
            (clientOrder order, ArrayList<manufacturingOrder> manufacturingOrders) {
        for (manufacturingOrder curr : manufacturingOrders) {

            if (curr.getClientOrder().getOrderNum() == order.getOrderNum()) {
                if (curr.getClientOrder().getClientName().equals(order.getClientName())) {
                    return true;
                }

            }
        }
        return false;
    }


    /**
     * Cria as rawMaterialsOrders para o ERP que serão enviadas (da forma pretendida) para o MES
     *
     * @param order manufacturingOrderController
     * @return Retorna a ligação entre rawMaterialOrders e manufacturingOrders
     */
    public ArrayList<rawMaterialOrderID_QtyRawMaterial> createRawMaterialOrder(manufacturingOrder order) {
        try {
            int deadline = order.getExpectedRawMaterialArrival();
            int manufacturingID = order.getProductionID();
            // O objetivo é retornar apenas as encomendas que interessam
            ArrayList<rawMaterialOrder> selection = new ArrayList<>(getRawMaterialOrders());

            // Elimina aquelas que são de outro tipo
            int type = order.getClientOrder().getPieceType();
            if (type == 6 || type == 8)
                type = 1;
            else
                type = 2;

            Iterator<rawMaterialOrder> iterator = selection.iterator();
            while (iterator.hasNext()) {
                rawMaterialOrder temp = iterator.next();
                if (temp.getPieceType() != type) {
                    iterator.remove();
                }
            }

            iterator = selection.iterator();
            // Elimina aquelas que têm um prazo de entrega maior que o deadline
            while (iterator.hasNext()) {
                rawMaterialOrder temp = iterator.next();
                if (temp.getArrivalTime() > deadline) {
                    iterator.remove();
                }
            }

            // Fica apenas com as rawMaterialOrders que têm peças de sobra
            iterator = selection.iterator();
            while (iterator.hasNext()) {
                rawMaterialOrder temp = iterator.next();
                int piecesUsed = 0;
                for (productionInRawMaterials curr2 : temp.getProductionInRawMaterials()) {
                    piecesUsed += curr2.getReservedQty();
                }
                if (piecesUsed == temp.getQty()) {
                    iterator.remove();
                }
            }

            // Se sobrar algo da filtragem, então vou atribuir rawMaterialOrders existentes
            // a esta nova manufacturingID, portanto vou adicionar essas atribuiçoes num vetor
            // de retorno para o production order
            int necessaryQty = order.getClientOrder().getQty();
            ArrayList<rawMaterialOrderID_QtyRawMaterial> returnVec = new ArrayList<>();
            if (selection.size() != 0) {

                for (rawMaterialOrder curr : selection) {
                    int piecesUsed = 0;
                    for (productionInRawMaterials curr2 : curr.getProductionInRawMaterials()) {
                        piecesUsed += curr2.getReservedQty();
                    }

                    int availablePieces = curr.getQty() - piecesUsed;

                    if (availablePieces >= necessaryQty) {
                        curr.addProductionInRawMaterials(manufacturingID, necessaryQty);
                        returnVec.add(new rawMaterialOrderID_QtyRawMaterial(curr.getID(), necessaryQty));
                        return returnVec;
                    } else {
                        curr.addProductionInRawMaterials(manufacturingID, availablePieces);
                        returnVec.add(new rawMaterialOrderID_QtyRawMaterial(curr.getID(), availablePieces));
                        necessaryQty = necessaryQty - availablePieces;
                    }
                }

            }
            // Se o vetor resultante desta seleção estiver vazio, ou se nao tiver peças em stock suficientes para fazer a encomenda
            // então preciso de fazer nova rawMaterialOrder

            // Para isso tenho de escolher o supplier
            ArrayList<supplier> suppliers = new suppliersList().availableSuppliers();
            // O default é o que entrega no menor tempo possível, portanto é o C
            supplier choosenSupplier = suppliers.get(2);

            for (supplier curr : suppliers) {
                if (curr.getDeliveryTime() + (int) (getTime() / 60) < deadline) {
                    choosenSupplier = curr;
                    break;
                }
            }

            int qtyToOrder = necessaryQty;
            if (choosenSupplier.getMinQty() > qtyToOrder) {
                qtyToOrder = choosenSupplier.getMinQty();
            }

            rawMaterialOrder newOrder = new rawMaterialOrder(
                    getRawMaterialOrders().size(),
                    qtyToOrder,
                    manufacturingID,
                    necessaryQty,
                    deadline - choosenSupplier.getDeliveryTime(),
                    type,
                    choosenSupplier);

            addNewRawMaterialOrder(newOrder);

            returnVec.add(new rawMaterialOrderID_QtyRawMaterial(newOrder.getID(), necessaryQty));

            return returnVec;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cria as productionOrders para o ERP que serão enviadas (da forma pretendida) para o MES
     *
     * @param order manufacturingOrderController
     * @param vec   Retorna a ligação entre cada manufacturingOrder e as diferentes rawMaterialOrders e quantidades
     */
    public void createProductionOrders(manufacturingOrder order, ArrayList<rawMaterialOrderID_QtyRawMaterial> vec) {

        productionOrder newProductionOrder = new productionOrder(
                order.getProductionID(),
                order.getClientOrder().getQty(),
                order.getClientOrder().getPieceType(),
                order.getExpectedProdutionStart(),
                vec

        );

        addNewProductionOrder(newProductionOrder);

    }

    /**
     * Cria as shippingOrders para o ERP que serão enviadas (da forma pretendida) para o MES
     *
     * @param order manufacturing order that origins shipping order
     */
    public void createShippingOrders(manufacturingOrder order) {

        shippingOrder newShippingOrder = new shippingOrder(
                order.getProductionID(),
                order.getClientOrder().getQty(),
                order.getExpectedShippingStart()
        );
        addNewShippingOrders(newShippingOrder);

    }

    public void send2MESinteralOrders() {

        String recvString = "";
        String prodString = "";
        String shipString = "";


        for (rawMaterialOrder curr : getRawMaterialOrders()) {
            if (curr.getArrivalTime() == countdays + 1) {
                recvString = recvString.concat(Integer.toString(curr.getID()));
                recvString = recvString.concat("@");
                recvString = recvString.concat(Integer.toString(curr.getPieceType()));
                recvString = recvString.concat("@");
                recvString = recvString.concat(Integer.toString(curr.getArrivalTime()));
                recvString = recvString.concat("@");
                recvString = recvString.concat(Integer.toString(curr.getQty()));
                recvString = recvString.concat("/");

            }
        }
        recvString = recvString.concat("_");

        for (productionOrder curr : getProductionOrders()) {
            if (countdays % 2 == 0) {
                if (curr.getStartProdutionDate() == countdays + internalOrders_target + 1
                        || curr.getStartProdutionDate() == countdays + internalOrders_target + 2) {
                    prodString = prodString.concat(Integer.toString(curr.getManufacturingID()));
                    prodString = prodString.concat("@");
                    prodString = prodString.concat(Integer.toString(curr.getFinalType()));
                    prodString = prodString.concat("@");
                    prodString = prodString.concat(Integer.toString(curr.getStartProdutionDate()));
                    prodString = prodString.concat("@");
                    prodString = prodString.concat(Integer.toString(curr.getQty()));
                    prodString = prodString.concat("@");
                    // Campo extra para dizer quantas ordens desse tipo tem o vetor
                    prodString = prodString.concat(Integer.toString(curr.getRawMaterialOrderID_qtyRawMaterials().size()));
                    for (rawMaterialOrderID_QtyRawMaterial curr2 : curr.getRawMaterialOrderID_qtyRawMaterials()) {
                        prodString = prodString.concat("@");
                        prodString = prodString.concat(Integer.toString(curr2.getRawMaterialOrderID()));
                        prodString = prodString.concat("@");
                        prodString = prodString.concat(Integer.toString(curr2.getQtyRawMaterial()));
                    }
                    prodString = prodString.concat("/");
                }
            }
        }

        prodString = prodString.concat("_");

        for (shippingOrder curr : getShippingOrders()) {
            if (curr.getStartShippingDate() == countdays + 1) {
                shipString = shipString.concat(Integer.toString(curr.getManufacturingID()));
                shipString = shipString.concat("@");
                shipString = shipString.concat(Integer.toString(curr.getStartShippingDate()));
                shipString = shipString.concat("@");
                shipString = shipString.concat(Integer.toString(curr.getQty()));
                shipString = shipString.concat("/");
            }
        }
        shipString = shipString.concat("_");
        String returnStr = "";

        returnStr = returnStr.concat(recvString);
        returnStr = returnStr.concat(prodString);
        returnStr = returnStr.concat(shipString);

        getShareResources().setInternalOrdersConcat(returnStr);

    }

    public void calculateCosts() {

        if (receiveFinishedOrdersStats()) {
            calculateTotalCosts();
        }

    }

    private boolean receiveFinishedOrdersStats() {

        String str = shareResources.getFinishedOrdersInfo();
        if (str.contains("empty"))
            return false;

        String[] allOrders = str.split("/", -1);
        int i = 0;
        while (i < allOrders.length - 1) {
            String tok = allOrders[i];

            String[] fields = tok.split("@", -1);
            //Irá ter 3 pos, com o ID, Production_t, SFS_t
            for (manufacturingOrder curr : getManufacturingOrders()) {
                // Para a manufacturing order, dá set aos tempos de produção
                if (curr.getProductionID() == Integer.parseInt(fields[0])) {

                    curr.setMeanProduction_t(Integer.parseInt(fields[1]));
                    curr.setMeanSFS_t(Integer.parseInt(fields[2]));

                }
            }
            i++;

        }
        return true;

    }

    private void calculateTotalCosts() {

        // Tenho de percorrer o vetor das manufacturingOrder
        for (manufacturingOrder curr : getManufacturingOrders()) {
            double totalCost = 0;
            // Só faz das encomendas que ainda não tem o custo calculado
            if (curr.getTotalCost() == 0 && curr.getMeanSFS_t() != 0 && curr.getMeanProduction_t() != 0) {

                for (rawMaterialOrder rawOrder : getRawMaterialOrders()) {
                    for (productionInRawMaterials rawOrderDetails : rawOrder.getProductionInRawMaterials()) {

                        if (rawOrderDetails.getOrderID() == curr.getProductionID()) {

                            // Production Cost: Pc =  1 € * Pt

                            double Pc = curr.getMeanProduction_t() * productionPrice_t * rawOrderDetails.getReservedQty();

                            // Depreciation Cost: Dc = Rc * SFS_t * 1% (Rc : raw Material cost)

                            int Rc = 0;
                            for (rawPiece piece : rawOrder.getSupplier().getRawPieces()) {

                                if (piece.getType() == rawOrder.getPieceType())
                                    Rc = piece.getUnitCost();
                                //System.out.println("Supplier: " + rawOrder.getSupplier().getName() + " Rc: " + Rc);
                            }

                            double Dc = Rc * ((double) (curr.getMeanSFS_t() / oneDay) + 1) * 0.01 * rawOrderDetails.getReservedQty();

                            totalCost += (Rc + Pc + Dc);

                        }
                    }
                }
//                System.out.println("Avg Cost: " + totalCost / manufacturingOrder.getClientOrder().getQty());
                curr.setTotalCost((int) totalCost);
            }

        }

    }


    // ******** VIEW METHODS *********

    public void displayRawMaterialArriving() {
        getErp_viewer().showRawMaterialArriving(getRawMaterialOrders(), countdays);
    }

    public void displayInternalOrder() {
        getErp_viewer().showInternalOrders(getRawMaterialOrders(), getProductionOrders(), getShippingOrders(), countdays);
    }

    public void displayRawMaterialOrdered() {
        getErp_viewer().showRawMaterialsOrdered(getRawMaterialOrders());
    }

    public void displayManufacturingOrdersCosts() {
        getErp_viewer().showManufacturingOrdersCosts(getManufacturingOrders(), getRawMaterialOrders());
    }
}
