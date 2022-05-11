package Models;

import Controllers.ERP;
import Controllers.manufacturingOrderController;
import Readers.xmlTransformationsReader;

import java.util.*;


public class higherDeadline extends orderCriterium {

    private class exe {

        public static final int oneDay = 60;
        public static final int LINEAR_t = 2;
        public static final int ROTATE_t = 2*LINEAR_t;
        public static final int in_out_WH_t = 2;



        public static final int unloadTOproduction_delay = oneDay / oneDay;
        public static final int productionFactor = 2;
        /**
         * unload capacity per day of Section A
         * VERIFICADO !!
         */
        public static final int unload_capacity = 9;

        public static final int production_capacity = 10;
        /**
         * shipping capacity per day of Section E
         */
        public static final int shipping_capacity = 6;


    }

    class OrdersComparator implements Comparator<manufacturingOrderController> {

        public int compare(manufacturingOrderController o1, manufacturingOrderController o2) {

            if (o1.getManufacturing_order().getClientOrder().getDeliveryDate() ==
                    o2.getManufacturing_order().getClientOrder().getDeliveryDate())
                return 0;
            else if (o1.getManufacturing_order().getClientOrder().getDeliveryDate() >
                    o2.getManufacturing_order().getClientOrder().getDeliveryDate())
                return 1;
            else
                return -1;

        }
    }

    @Override
    public void ordering(ArrayList<manufacturingOrderController> MyDetailedOrder) {

        Collections.sort(MyDetailedOrder, new OrdersComparator());

    }

    public Vector<Integer> scheduler(manufacturingOrderController MyNewDetailedOrder, long current_t) {

        return estimatePlan(MyNewDetailedOrder, current_t);

    }
     /**
     * @param curr
     * @param current_t
     * @return o plano para a nova encomenda do cliente ( unload_begin/end ...)
     */
    public Vector<Integer> estimatePlan(manufacturingOrderController curr, long current_t) {


        int productionID = curr.getManufacturing_order().getProductionID();
        int unload_t, production_t, shipping_t;

        //manufacturingOrderController curr = getManufacturingOrders().get(0);
        clientOrder order = curr.getClientOrderController().getClientOrder();

        unload_t = unloadEstimation(order);
        production_t = productionEstimation(order);
        shipping_t = shippingEstimation(order);

        //System.out.println("total = unload_t + production_t + shipping_t = "
        //       + unload_t + " + " + production_t + " + " + shipping_t + " = " + (unload_t + production_t + shipping_t) + "s");

        // em função do tempo disponivel escolhe o supplier
        // se nao for suficiente olha para o espaço livre do armazem e das encomendas pendentes de modo que caibam
        // se ainda assim nao for possivel decidir entao olha para o custo das peças

        //Vector<Integer> fullPlanEstimation = fullPlanEstimation(order, unload_t, production_t, shipping_t);
        return fullPlanEstimation(order, unload_t, production_t, shipping_t, current_t);
        //createInternalOrders(fullPlanEstimation, productionID);
        //displayInternalOrder();

        //unload starting day
        //return fullPlanEstimation.get(0);
        //int shipping_begin = createShippingOrder(/*production_end,*/ shipping_t, order.getDeliveryDate(), productionID);
        //int production_begin = createProductionOrder(shipping_begin, production_t, productionID);
        //int unload_begin = createUnloadOrder(unload_t, productionID);


        //}
    }

    public int unloadEstimation(clientOrder order) {

        // Pesquisa encomendas pendentes para chegar antes
        // Logo esta encomenda so pode começar a ser descarregada após
        return order.getQty() * (exe.LINEAR_t + exe.ROTATE_t + exe.in_out_WH_t);
    }

    public int productionEstimation(clientOrder order) {

        // Assumindo que as máquinas já têm as ferramentas necessárias equipadas

        // Depende da peça; Type 3 é para as máquinas 2
        ArrayList<transformations> transformationsVec = new xmlTransformationsReader().readTransformations();
        int i = 0;
        int totalDuration = 0;
        int nTransformations = 0;
        int aux = -1;
        ;
        while (i < transformationsVec.size()) {

            if (transformationsVec.get(i).getFinal() == order.getPieceType()) {
                if (transformationsVec.get(i).getInit() == 1 || transformationsVec.get(i).getInit() == 2) {
                    totalDuration = totalDuration + transformationsVec.get(i).getDuration();
                    nTransformations++;
                    break;
                } else {

                    aux = transformationsVec.get(i).getInit();
                    totalDuration = totalDuration + transformationsVec.get(i).getDuration();
                    nTransformations++;
                    i = 0;
                }
            }

            if (transformationsVec.get(i).getFinal() == aux) {
                if (transformationsVec.get(i).getInit() == 1 || transformationsVec.get(i).getInit() == 2) {
                    totalDuration = totalDuration + transformationsVec.get(i).getDuration();
                    nTransformations++;
                    break;
                }

                aux = transformationsVec.get(i).getInit();
                totalDuration = totalDuration + transformationsVec.get(i).getDuration();
                nTransformations++;

                i = 0;
            }
            i++;

        }

        return (order.getQty() * exe.in_out_WH_t) + exe.productionFactor * (nTransformations * 2 * (exe.LINEAR_t + (4 * exe.ROTATE_t))) + totalDuration + exe.in_out_WH_t;

    }

    public int shippingEstimation(clientOrder order) {

        //int r = order.getQty() / exe.shipping_capacity;
        int r = 0;
        return r * exe.oneDay + (order.getQty() * exe.ROTATE_t) + (3 * exe.LINEAR_t);
    }

    /**
     * @param order
     * @param unload_t
     * @param production_t
     * @param shipping_t
     * @return #days added to delivery date
     */
    public Vector<Integer> fullPlanEstimation(clientOrder order, int unload_t, int production_t, int shipping_t, long current_t) {
        int unload_begin = 0, unload_end = 0;
        int production_begin = 0, production_end = 0;
        int shipping_begin = 0, shipping_end = 0;

        int deliveryDate = order.getDeliveryDate() - 1;

        //System.out.println("unload_t: " + unload_t + "production_t: " + production_t + "shipping_t:" + shipping_t );
        //2 pq é 1 dia de safe zone e outro 1 dia para o tempo minimo de entrega de uma encomenda
        while (unload_begin - exe.unloadTOproduction_delay - 1 < 0) {
            deliveryDate++;
            // *** SHIPPING
            // folga de 10 s no shipping, portanto tentar ocupar no maximo os primeiros 20s do dia de enviar

            float deadline = /*current_t + */ deliveryDate *  exe.oneDay + (exe.oneDay / 2) - 10;
            shipping_end = (int) deadline / exe.oneDay;
            if ((deadline / exe.oneDay) - (int) (deadline / exe.oneDay) >= 0.5) {
                shipping_begin = shipping_end - (shipping_t / exe.oneDay) - 2;
            } else {
                shipping_begin = shipping_end - (shipping_t / exe.oneDay) - 1;
            }

            // *** PRODUCTION

            if (shipping_end - shipping_begin == 0) {
                production_end = shipping_end;
            } else {
                production_end = shipping_end - 1; // corresponde ao shipping_begin
            }
            production_begin = production_end - (production_t / exe.oneDay);

            // *** UNLOAD
            // para garantir que o unload fica completo no dia antes de começar a produção
            unload_end = production_begin - 1;
            unload_begin = unload_end - (unload_t / exe.oneDay);

        }
        //System.out.println("UNLOAD | begin: " + unload_begin + " end: " + unload_end);
        //System.out.println("PRODUCTION | begin: " + production_begin + " end: " + production_end);
        //System.out.println("SHIPPING | begin: " + shipping_begin + " end: " + shipping_end);

        Vector<Integer> returnVEC = new Vector<>();

        returnVEC.add(unload_begin);
        returnVEC.add(unload_end);

        returnVEC.add(production_begin);
        returnVEC.add(production_end);

        returnVEC.add(shipping_begin);
        returnVEC.add(shipping_end);
        return returnVEC;

    }


}
