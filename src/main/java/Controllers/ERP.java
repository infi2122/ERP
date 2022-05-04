package Controllers;

import comsProtocols.shareResources;
import Models.*;
import Readers.suppliersList;
import Readers.xmlReader;
import Viewers.*;

import java.util.ArrayList;
import java.util.Vector;

public class ERP {

    // VARIABLES
    private long startTime = 0;
    private int countdays = 0;
    private int dayBefore = -1;

    public final int oneDay = 60;
    public final int internalOrders_target = 2; // #days to send orders for MES

    // ******* ATTRIBUTES ********
    private long currentTime;
    private shareResources shareResources;
    private orderCriterium orderCriterium;
    private ArrayList<manufacturingOrderController> manufacturingOrders;
    private ArrayList<rawMaterialOrder> rawMaterialOrders;
    private ArrayList<productionOrder> productionOrders;
    private ArrayList<shippingOrder> shippingOrders;


    private ERP_Viewer erp_viewer;

    // ******* CONSTRUCTOR ********
    public ERP(orderCriterium orderCriterium, ArrayList<manufacturingOrderController> manufacturingOrderControllers,
               ERP_Viewer erp_viewer, shareResources sharedResources) {
        this.orderCriterium = orderCriterium;
        this.manufacturingOrders = manufacturingOrderControllers;
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


    public ArrayList<rawMaterialOrder> getRawMaterialOrders() {
        return rawMaterialOrders;
    }

    public void addNewRawMaterialOrder(rawMaterialOrder newRawMaterialOrder) {
        getRawMaterialOrders().add(newRawMaterialOrder);
    }
    public ArrayList<productionOrder> getProductionOrders() {
        return productionOrders;
    }

    public void addNewProductionOrder(productionOrder newProductionOrder) {
        getProductionOrders().add(newProductionOrder);
    }

    public ArrayList<shippingOrder> getShippingOrders() {
        return shippingOrders;
    }

    public void addNewShippingOrders(shippingOrder newShippingOrder) {
        getShippingOrders().add(newShippingOrder);
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
                    System.out.println("Current Day: " + countdays);
                }
            }
        }

    }

    /**
     * Checks if exists new orders in the stack of share resources of communications
     * @param sharedResource
     * @return
     */
    public void checkNewOrders(String sharedResource) {
        System.out.println("CHECK NEW ORDER! TIME: " + getTime());
        xmlReader reader = new xmlReader();
        ArrayList<clientOrder> ordersVec = reader.readOrder(sharedResource);

        if (ordersVec == null)
            return;

        for (clientOrder currOrder : ordersVec) {

            if (!check_if_clientOrder_have_manufacturingOrder(currOrder, getManufacturingOrders())) {

                int orderID = getManufacturingOrders().size();

                manufacturingOrderController MyNewDetailedOrder =
                        new manufacturingOrderController(new manufacturingOrder(orderID, currOrder),
                                new manufacturingOrder_Viewer(),
                                new clientOrderController(currOrder, new clientOrder_Viewer()));

                // Error testing
                if (!addDetailedOrder(MyNewDetailedOrder))
                    System.out.println("Error adding new detailed order to manufacturingOrderController !");

                Vector<Integer> plan = getOrderCriterium().scheduler(MyNewDetailedOrder, getTime());
                System.out.println("nao é no scheduler");
                // SET manufacturing plan on manufacturing order
                // SET expected Raw Material Arrival
                MyNewDetailedOrder.getManufacturing_order().setExpectedRawMaterialArrival(plan.get(0));
                // SET expected production start
                MyNewDetailedOrder.getManufacturing_order().setExpectedProdutionStart(plan.get(2));
                // SET expected shipping start
                MyNewDetailedOrder.getManufacturing_order().setExpectedShippingStart(plan.get(4));

                // Associa as RawmaterialOrders com diferentes quantidade de materias primas
                // retorna essa relação
                ArrayList<rawMaterialOrderID_QtyRawMaterial> vec = createRawMaterialOrder(MyNewDetailedOrder);

                // Cria as PorductionOrders
                createProductionOrders(MyNewDetailedOrder,vec);

                // Cria as Shipping Orders
                createShippingOrders(MyNewDetailedOrder);

                //createInternalOrders(plan, MyNewDetailedOrder.getManufacturing_order());

            }
        }

        getOrderCriterium().ordering(getManufacturingOrders());

        return;
    }

    /**
     * Todos os dias faz as encomendas necessárias aos fornecedores
     */
//    public void placeRawMaterialOrder() {
//
//        for (rawMaterialOrder currMaterial : getRawMaterialOrders()) {
//            if (currMaterial.getOrderPlaceTime() == getTime() / oneDay) {
//                currMaterial.setArrivalTime(currMaterial.getSupplier().getDeliveryTime() + (int) (getTime() / oneDay));
//            }
//        }
//    }
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
     * @param order
     */
    public void createInternalOrders(Vector<Integer> vector, manufacturingOrder order) {
//
//        int manufacturingID = order.getProductionID();
//
//        // Pesquisa dentro de todas as rawMaterial Orders
//        for (rawMaterialOrder curr : getRawMaterialOrders()) {
//
//            for (productionInRawMaterials curr2 : curr.getProductionInRawMaterials()) {
//                // Quais sao as encomendas associadas ao manufacturing ID
//                if (curr2.getOrderID() == manufacturingID) {
//                    // Logo cria uma RECV Order dessa quantidade para o arrivalDate da encomenda total e não
//                    // Do dia estimado que é suposto chegar a encomenda ( dado por vector.get(0) )
//                    getReceiveOrder().add(
//                            new receiveOrder(manufacturingID,
//                                    curr.getOrderPlaceTime() + curr.getSupplier().getDeliveryTime(),
//                                    order.getClientOrder().getPieceType(),
//                                    curr2.getReservedQty()
//                            ));
//                }
//            }
//        }
//
//        // Para o ProductionOrder
//        getProductionOrder().add(new productionOrder(order.getProductionID(), vector.get(2)));
//
//        // Para o ShippingOrder
//        getShippingOrder().add(new shippingOrder(order.getProductionID(), vector.get(4)));



        /*int i = 0;
        // Para o ReceiveOrder
        int manufacturingID = order.getProductionID();
        while (i <= vector.get(1) - vector.get(0)) {
            // Pesquisa dentro de todas as rawMaterial Orders
            for (rawMaterialOrder curr : getRawMaterialOrders()) {

                for (idResQty curr2 : curr.getIDResQty_vec()) {
                    // Quais sao as encomendas associadas ao manufacturing ID
                    if (curr2.getOrderID() == manufacturingID) {
                        // Logo cria uma RECV Order dessa quantidade para o arrivalDate da encomenda total e não
                        // Do dia estimado que é suposto chegar a encomenda ( dado por vector.get(0) )
                        getReceiveOrder().add(
                                new receiveOrder(manufacturingID,
                                        curr.getArrivalTime() + i,
                                        order.getClientOrder().getPieceType(),
                                        curr2.getReservedQty()
                                ));
                    }
                }
            }
            i++;
        }

        // Para o ProductionOrder
        i = 0;
        while (i <= vector.get(3) - vector.get(2)) {
            getProductionOrder().add(new productionOrder(order.getProductionID(), vector.get(2) + i));
            i++;
        }
        // Para o ShippingOrder
        i = 0;
        while (i <= vector.get(5) - vector.get(4)) {
            getShippingOrder().add(new shippingOrder(order.getProductionID(), vector.get(4) + i));
            i++;
        }
*/
    }



//    private class checkStockStruct {
//
//        private int rawMaterial_id;
//        private int availablePieces;
//
//        public checkStockStruct(int rawMaterial_id, int availablePieces) {
//            this.rawMaterial_id = rawMaterial_id;
//            this.availablePieces = availablePieces;
//        }
//
//        public int getRawMaterial_id() {
//            return rawMaterial_id;
//        }
//
//        public void setRawMaterial_id(int rawMaterial_id) {
//            this.rawMaterial_id = rawMaterial_id;
//        }
//
//        public int getAvailablePieces() {
//            return availablePieces;
//        }
//
//        public void setAvailablePieces(int availablePieces) {
//            this.availablePieces = availablePieces;
//        }
//    }

//    private class checkPendingRawMaterialOrderStruct {
//
//        private int rawMaterialOrder_id;
//        private int availableQty;
//        private int deliveryDate;
//
//        public checkPendingRawMaterialOrderStruct(int rawMaterialOrder_id, int availableQty, int deliveryDate) {
//            this.rawMaterialOrder_id = rawMaterialOrder_id;
//            this.availableQty = availableQty;
//            this.deliveryDate = deliveryDate;
//        }
//
//        public int getRawMaterialOrder_id() {
//            return rawMaterialOrder_id;
//        }
//
//        public int getAvailableQty() {
//            return availableQty;
//        }
//
//        public int getDeliveryDate() {
//            return deliveryDate;
//        }
//
//    }


    /**
     * Cria as rawMaterialsOrders para o ERP que serão enviadas (da forma pretendida) para o MES
     * @param order manufacturingOrderController
     * @return Retorna a ligação entre rawMaterialOrders e manufacturingOrders
     */
    public ArrayList<rawMaterialOrderID_QtyRawMaterial> createRawMaterialOrder(manufacturingOrderController order) {

        int deadline = order.getManufacturing_order().getExpectedRawMaterialArrival();
        int manufacturingID = order.getManufacturing_order().getProductionID();
        // O objetivo é retornar apenas as encomendas que interessam
        ArrayList<rawMaterialOrder> selection = new ArrayList<>(getRawMaterialOrders());

        // Elimina aquelas que são de outro tipo
        int type = order.getClientOrderController().getClientOrder().getPieceType();
        if (type == 6 || type == 8)
            type = 1;
        else
            type = 2;

        for (rawMaterialOrder curr : selection) {
            if (curr.getPieceType() != type) {
                selection.remove(curr);
            }
            if (selection.size() == 0)
                break;
        }

        // Elimina aquelas que têm um prazo de entrega maior que o deadline
        for (rawMaterialOrder curr : selection) {
            if (curr.getArrivalTime() > deadline) {
                selection.remove(curr);
            }
            if (selection.size() == 0)
                break;
        }

        // Fica apenas com as rawMaterialOrders que têm peças de sobra
        for (rawMaterialOrder curr : selection) {
            int piecesUsed = 0;
            for (productionInRawMaterials curr2 : curr.getProductionInRawMaterials()) {
                piecesUsed += curr2.getReservedQty();
            }
            if (piecesUsed == curr.getQty()) {
                selection.remove(curr);
            }
            if (selection.size() == 0)
                break;
        }

        // Se sobrar algo da filtragem, então vou atribuir rawMaterialOrders existentes
        // a esta nova manufacturingID, portanto vou adicionar essas atribuiçoes num vetor
        // de retorno para o production order
        int necessaryQty = order.getClientOrderController().getClientOrder().getQty();
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
                    returnVec.add(new rawMaterialOrderID_QtyRawMaterial(curr.getId(), necessaryQty));
                    return returnVec;
                } else {
                    curr.addProductionInRawMaterials(manufacturingID, availablePieces);
                    returnVec.add(new rawMaterialOrderID_QtyRawMaterial(curr.getId(), availablePieces));
                    necessaryQty = necessaryQty - availablePieces;
                }
            }

        }
        // Se o vetor resultante desta seleção estiver vazio, ou se nao tiver peças em stock suficientes para fazer a encomenda
        // então preciso de fazer nova rawMaterialOrder

        // Para isso tenho de escolher o supplier
        ArrayList<supplier> suppliers = new suppliersList().availableSuppliers();
        supplier choosenSupplier = new supplier();

        for (supplier curr : suppliers) {
            if (curr.getDeliveryTime() + (int) (getTime() / 60) < deadline) {
                choosenSupplier = curr;
                break;
            }
        }
        // Se o deadline for menor que o tempo que demora, entao escolho o supplier que entrega mais rapido --> SupplierC
        if (choosenSupplier == null)
            choosenSupplier = suppliers.get(2);

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

        returnVec.add(new rawMaterialOrderID_QtyRawMaterial(newOrder.getId(), necessaryQty));

        return returnVec;

        // Primeiro checka se tem peças em stock nos armazens
//            ArrayList<checkStockStruct> stock = checkStock(order.getManufacturing_order().getClientOrder().getPieceType(),
//                    order.getManufacturing_order().getClientOrder().getQty());
//
//        /* Significa que tenho peças suficientes, logo é reservá-las  */
//        if (stock.size() != 0) {
//
//            // criar o tal recurso partilhado para enviar para o MES, de forma que o MES receba e depois no ERP é apagado
//            // Se no MES ao receber proprio dia nao tem a quantidade suficiente para reservar, entao guarda essa ordem até
//            // que consiga stampar o orderID respetivo ≥ APENAS para o stock atualizado à ‘posteriori’, quando se cria a
//            // recv order nao temos este problema
//
//            for (checkStockStruct curr : stock) {
//                stockUpdater newUpdate = new stockUpdater(order.getManufacturing_order().getProductionID(),
//                        curr.getRawMaterial_id(), curr.getAvailablePieces());
//                getStockUpdaters().add(newUpdate);
//            }
//            return 0;
//        }
//
//        // Depois checka as peças que estao encomendadas, para ver se chega
//        ArrayList<checkPendingRawMaterialOrderStruct> pieces_ordered = checkPendingRawMaterialOrder(
//                order.getManufacturing_order().getClientOrder().getPieceType(),
//                order.getManufacturing_order().getClientOrder().getQty(),
//                deadline);
//
//        int remaingPieces = order.getManufacturing_order().getClientOrder().getQty();
//        if (pieces_ordered.size() != 0) {
//            // Ao dar o add da última RECV order da RawMaterial Order, é que são definidas as sobras para stock
//            for (checkPendingRawMaterialOrderStruct curr : pieces_ordered) {
//                rawMaterialOrder temp = null;
//                for (rawMaterialOrder curr2 : getRawMaterialOrders()) {
//                    if (curr2.getId() == curr.getRawMaterialOrder_id()) {
//                        temp = curr2;
//                        break;
//                    }
//                }
//                remaingPieces = remaingPieces - curr.getAvailableQty();
//                // Se remaining == 0 então é porque se vai usar todas as peças encomendadas
//                if (remaingPieces == 0) {
//
//                    temp.getProductionInRawMaterials().add(
//                            new productionInRawMaterials(order.getManufacturing_order().getProductionID(),
//                                    order.getManufacturing_order().getClientOrder().getQty()
//                            ));
//
//                    temp.getProductionInRawMaterials().remove(1);
//                    return 0;
//                    // Tenho de atualizar a encomenda ficticius, para reduzir a quantidade em stock
//                } else if (remaingPieces < 0) {
//
//                    temp.getProductionInRawMaterials().add(
//                            new productionInRawMaterials(order.getManufacturing_order().getProductionID(),
//                                    order.getManufacturing_order().getClientOrder().getQty()
//                            ));
//
//                    int newStock = temp.getProductionInRawMaterials().get(1).getReservedQty() - order.getManufacturing_order().getClientOrder().getQty();
//                    temp.getProductionInRawMaterials().get(1).setReservedQty(newStock);
//                    return 0;
//                    // É porque ainda preciso de encomendar a quantidade extra
//                } else {
//                    temp.getProductionInRawMaterials().add(
//                            new productionInRawMaterials(order.getManufacturing_order().getProductionID(),
//                                    curr.getAvailableQty()
//                            ));
//                    temp.getProductionInRawMaterials().remove(1);
//                }
//            }
//        }
//
//        //Por fim encomenda o execedente
//        // Encontrar o fornecedor face ao tempo
//        ArrayList<supplier> suppliers = new suppliersList().availableSuppliers();
//        supplier choosenSupplier = new supplier();
//
//        for (supplier curr : suppliers) {
//            if (curr.getDeliveryTime() + (int) (getTime() / 60) < deadline) {
//                choosenSupplier = curr;
//                break;
//            }
//        }
//        // Se o deadline for menor que o tempo que demora, entao escolho o supplier que entrega mais rapido --> SupplierC
//        if (choosenSupplier == null)
//            choosenSupplier = suppliers.get(2);
//
//
//        // ao fazer o break garante-se que se escolhe sempre o fornecedor que demora mais a entregar logo o que sai mais barato
//        // encomendar o nº de peças necessárias
//        int qty_to_order = remaingPieces;
//        if (choosenSupplier.getMinQty() > order.getManufacturing_order().getClientOrder().getQty()) {
//            qty_to_order = choosenSupplier.getMinQty();
//        }
//
//        // Saber qual o tipo de peça 1 -> P6 e P8 ou 2-> Outras
//        int pieceType_to_order;
//        if (order.getManufacturing_order().getClientOrder().getPieceType() == 6 ||
//                order.getManufacturing_order().getClientOrder().getPieceType() == 8) {
//            pieceType_to_order = 1;
//        } else {
//            pieceType_to_order = 2;
//        }
//        // Teoricamente deve de haver um vetor para colocar as encomendas a serem feitas no dia certo,
//        // pois correm o risco de chegarem mais cedo
//
//        // Place Order
//        addNewRawMaterialOrder(new rawMaterialOrder(
//                getRawMaterialOrders().size(),
//                qty_to_order,
//                order.getManufacturing_order().getProductionID(),
//                order.getManufacturing_order().getClientOrder().getQty(),
//                deadline - choosenSupplier.getDeliveryTime(),
//                pieceType_to_order,
//                choosenSupplier
//        ));
//        // Place Ficticius Order (orderID = -1), apenas a simbolizar stock
//        rawMaterialOrder ficticius = getRawMaterialOrders().get(getRawMaterialOrders().size() - 1);
//        ficticius.getProductionInRawMaterials().add(
//                new productionInRawMaterials(-1,
//                        ficticius.getQty() - order.getManufacturing_order().getClientOrder().getQty())
//        );
//
//
//        //order.displayRawMaterialOrders();
//
//        return qty_to_order;
    }

    /**
     * Cria as productionOrders para o ERP que serão enviadas (da forma pretendida) para o MES
     * @param order manufacturingOrderController
     * @param vec Retorna a ligação entre cada manufacturingOrder e as diferentes rawMaterialOrders e quantidades
     */
    public void createProductionOrders(manufacturingOrderController order, ArrayList<rawMaterialOrderID_QtyRawMaterial> vec) {

        productionOrder newProductionOrder = new productionOrder(
                order.getManufacturing_order().getProductionID(),
                order.getManufacturing_order().getClientOrder().getQty(),
                order.getManufacturing_order().getClientOrder().getPieceType(),
                order.getManufacturing_order().getExpectedProdutionStart(),
                vec

        );

        addNewProductionOrder(newProductionOrder);

    }

    /**
     * Cria as shippingOrders para o ERP que serão enviadas (da forma pretendida) para o MES
     * @param order
     */
    public void createShippingOrders (manufacturingOrderController order){

        shippingOrder newShippingOrder = new shippingOrder(
                order.getManufacturing_order().getProductionID(),
                order.getManufacturing_order().getClientOrder().getQty(),
                order.getManufacturing_order().getExpectedShippingStart()
        );
        addNewShippingOrders(newShippingOrder);

    }

//    /**
//     * Se for possível usar as peças em stock então retorna o array com [rawMaterialOrder_id, availablePieces]
//     * @param type
//     * @param qty
//     * @return
//     */
//    private ArrayList<checkStockStruct> checkStock(int type, int qty) {
//
//        /*  ATENÇÃO: Só é stock se Arrival Date > curr Day  */
//
//        ArrayList<checkStockStruct> returnVec = new ArrayList<>();
//
//        if (type == 6 || type == 8)
//            type = 1;
//        else
//            type = 2;
//
//        for (rawMaterialOrder curr : getRawMaterialOrders()) {
//            // Se for igual é considerado que a encomenda chega nesse dia, portanto vai para as peças em stock
//            // porque a atualização é feita no dia seguinte, logo nesse momento as peças ja estão em stock
//
//            if (curr.getArrivalTime() <= countdays) {
//                if (curr.getPieceType() == type) {
//                    int reservedQty_per_rawMaterialOrder = 0;
//                    for (productionInRawMaterials curr2 : curr.getProductionInRawMaterials()) {
//                        reservedQty_per_rawMaterialOrder += curr2.getReservedQty();
//                    }
//                    if (reservedQty_per_rawMaterialOrder != 0) {
//                        returnVec.add(new checkStockStruct(curr.getId(), curr.getQty() - reservedQty_per_rawMaterialOrder));
//                    }
//                    if (reservedQty_per_rawMaterialOrder >= qty)
//                        break;
//                }
//            }
//        }
//        int N_available = 0;
//
//        for (checkStockStruct curr : returnVec) {
//            N_available += curr.getAvailablePieces();
//        }
//        if (N_available < qty)
//            return new ArrayList<>();
//
//        return returnVec;
//    }

    public void updateStockinSharedResources(shareResources shareResources) {
//
//        String updateString = shareResources.getStockUpdate();
//
//        for (stockUpdater curr : getStockUpdaters()) {
//
//            updateString = updateString.concat(Integer.toString(curr.getOrderID()));
//            updateString = updateString.concat("@");
//            updateString = updateString.concat(Integer.toString(curr.getPieceType()));
//            updateString = updateString.concat("@");
//            updateString = updateString.concat(Integer.toString(curr.getQty()));
//            updateString = updateString.concat("/");
//
//        }
//        shareResources.setStockUpdate(updateString);
//
//        if (shareResources.getStockUpdate() != null)
//            System.out.println("Update String: " + shareResources.getStockUpdate());
//        getStockUpdaters().removeAll(getStockUpdaters());
//
//        if (getStockUpdaters().size() != 0)
//            System.out.println("Vetor stockUpdater size: " + getStockUpdaters().size());
    }

//    private ArrayList<checkPendingRawMaterialOrderStruct> checkPendingRawMaterialOrder(int type, int qty, int deadline) {
//
//        ArrayList<checkPendingRawMaterialOrderStruct> returnVec = new ArrayList<>();
//
//        if (type == 6 || type == 8)
//            type = 1;
//        else
//            type = 2;
//        int sum = 0;
//        for (rawMaterialOrder curr : getRawMaterialOrders()) {
//            if (curr.getPieceType() == type) {
//                if (curr.getArrivalTime() <= deadline) {
//                    int npiecesInUse = 0;
//                    for (productionInRawMaterials curr2 : curr.getProductionInRawMaterials()) {
//                        if (curr2.getOrderID() != -1)
//                            npiecesInUse += curr2.getReservedQty();
//                    }
//                    int qtyAvailable = curr.getQty() - npiecesInUse;
//                    if (qtyAvailable > 0) {
//                        returnVec.add(new checkPendingRawMaterialOrderStruct(curr.getId(), qtyAvailable, curr.getArrivalTime()));
//                        sum += qtyAvailable;
//                    }
//
//                }
//            }
//            if (sum == qty)
//                break;
//        }
//        if (sum == 0)
//            return new ArrayList<>();
//
//        return returnVec;
//    }

    public void send2MESinteralOrders(shareResources erp2MES) {

        String recvString = new String();
        String prodString = new String();
        String shipString = new String();

//
//        for (receiveOrder curr : getReceiveOrder()) {
//            if (curr.getStartDate() <= (int) (getTime() / oneDay) + internalOrders_target) {
//                recvString = recvString.concat(Integer.toString(curr.getOrderID()));
//                recvString = recvString.concat("@");
//                recvString = recvString.concat(Integer.toString(curr.getStartDate()));
//                recvString = recvString.concat("@");
//                recvString = recvString.concat(Integer.toString(curr.getPieceType()));
//                recvString = recvString.concat("@");
//                recvString = recvString.concat(Integer.toString(curr.getReservedQty()));
//                recvString = recvString.concat("/");
//
//            }
//        }
//        recvString = recvString.concat("_");
//
//        for (productionOrder curr : getProductionOrder()) {
//            if (curr.getStartDate() <= (int) (getTime() / oneDay) + internalOrders_target) {
//                prodString = prodString.concat(Integer.toString(curr.getOrderID()));
//                prodString = prodString.concat("@");
//                prodString = prodString.concat(Integer.toString(curr.getStartDate()));
//                prodString = prodString.concat("/");
//            }
//        }
//        prodString = prodString.concat("_");
//
//        for (shippingOrder curr : getShippingOrder()) {
//            if (curr.getStartDate() <= (int) (getTime() / oneDay) + internalOrders_target) {
//                shipString = shipString.concat(Integer.toString(curr.getOrderID()));
//                shipString = shipString.concat("@");
//                shipString = shipString.concat(Integer.toString(curr.getStartDate()));
//                shipString = shipString.concat("/");
//            }
//        }
//        shipString = shipString.concat("_");
//        String returnStr = new String();
//
//        returnStr = returnStr.concat(recvString);
//        returnStr = returnStr.concat(prodString);
//        returnStr = returnStr.concat(shipString);
//
//        //System.out.println(returnStr);
//        erp2MES.printInERP2MESbuffer(returnStr);

        return;


    }

    // ******** VIEW METHODS *********

    public void displayManufacturingOrders() {

        getErp_viewer().showManufacturingOrders(getManufacturingOrders());


    }

    public void displayRawMaterialArriving() {

        getErp_viewer().showRawMaterialArriving(getRawMaterialOrders(), countdays);


    }

    public void displayInternalOrder() {

        getErp_viewer().showInternalOrders(getRawMaterialOrders(), getProductionOrders(), getShippingOrders());

    }

    public void displayRawMaterialOrdered() {

        //getErp_viewer().showRawMaterialsOrdered(allMaterialsOrdered());


    }

}
