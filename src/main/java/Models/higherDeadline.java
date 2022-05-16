package Models;

import Readers.xmlTransformationsReader;

import java.util.*;


public class higherDeadline extends orderCriterium {


    public static final int oneDay = 60;
    public static final int LINEAR_t = 2;
    public static final int ROTATE_t = 2 * LINEAR_t;
    public static final int in_out_WH_t = 2;

    public static final int placeOnConveyorDelay = 1; // segundos
    public static final int productionFactor = 2; // segundos
    public static final int unloadTOproduction_delay = 1; // days
    /**
     * unload capacity per day of Section A
     */
    public static final int unload_capacity = 9;

    public static final int production_capacity = 10;
    /**
     * shipping capacity per day of Section E
     */
    public static final int shipping_capacity = 9;


    class OrdersComparator implements Comparator<manufacturingOrder> {

        public int compare(manufacturingOrder o1, manufacturingOrder o2) {

            if (o1.getClientOrder().getDeliveryDate() ==
                    o2.getClientOrder().getDeliveryDate())
                return 0;
            else if (o1.getClientOrder().getDeliveryDate() >
                    o2.getClientOrder().getDeliveryDate())
                return 1;
            else
                return -1;

        }
    }

    @Override
    public void ordering(ArrayList<manufacturingOrder> MyDetailedOrder) {

        MyDetailedOrder.sort(new OrdersComparator());

    }

    public Vector<Integer> scheduler(manufacturingOrder MyNewDetailedOrder, long current_t) {

        return estimatePlan(MyNewDetailedOrder, current_t);

    }

    /**
     * @param curr
     * @param current_t
     * @return o plano para a nova encomenda do cliente ( unload_begin/end ...)
     */
    public Vector<Integer> estimatePlan(manufacturingOrder curr, long current_t) {


        int productionID = curr.getProductionID();
        int unload_t, production_t, shipping_t;

        clientOrder order = curr.getClientOrder();

        unload_t = unloadEstimation(order);
        production_t = productionEstimation(order);
        shipping_t = shippingEstimation(order);

        // em função do tempo disponivel escolhe o supplier
        // se nao for suficiente olha para o espaço livre do armazem e das encomendas pendentes de modo que caibam
        // se ainda assim nao for possivel decidir entao olha para o custo das peças


        return fullPlanEstimation(order, unload_t, production_t, shipping_t, current_t);
    }

    public int unloadEstimation(clientOrder order) {

        // Pesquisa encomendas pendentes para chegar antes
        // Logo esta encomenda so pode começar a ser descarregada após
        return order.getQty() * (LINEAR_t + ROTATE_t + in_out_WH_t + placeOnConveyorDelay);
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

        return (order.getQty() * in_out_WH_t) +
                productionFactor * (nTransformations * 2 * (LINEAR_t + (4 * ROTATE_t))) +
                totalDuration + in_out_WH_t;

    }

    public int shippingEstimation(clientOrder order) {

        return (order.getQty() * 2 * ROTATE_t) + (3 * LINEAR_t);
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

//        int deliveryDate = order.getDeliveryDate() - 1;
//
////        System.out.println("unload_t: " + unload_t + "production_t: " + production_t + "shipping_t:" + shipping_t);
////        2 pq é 1 dia de safe zone e outro 1 dia para o tempo minimo de entrega de uma encomenda
//        while (unload_begin - unloadTOproduction_delay - 1 < 0) {
//            deliveryDate++;
//            // *** SHIPPING
//            // folga de 10 s no shipping, portanto tentar ocupar no maximo os primeiros 20s do dia de enviar
//            float deadline = /*current_t + */ deliveryDate * oneDay + (oneDay / 2) - 10;
//            shipping_end = (int) deadline / oneDay;
//            if ((deadline / oneDay) - (int) (deadline / oneDay) >= 0.5) {
//                shipping_begin = shipping_end - (shipping_t / oneDay) - 2;
//            } else {
//                shipping_begin = shipping_end - (shipping_t / oneDay) - 1;
//            }
//
//            // *** PRODUCTION
//
//            if (shipping_end - shipping_begin == 0) {
//                production_end = shipping_end;
//            } else {
//                production_end = shipping_end - 1; // corresponde ao shipping_begin
//            }
//            production_begin = production_end - (production_t / oneDay);
//
//            // *** UNLOAD
//            // para garantir que o unload fica completo no dia antes de começar a produção
//            unload_end = production_begin - 1;
//            unload_begin = unload_end - (unload_t / oneDay);
//
//        }
//        System.out.println("UNLOAD | begin: " + unload_begin + " end: " + unload_end);
//        System.out.println("PRODUCTION | begin: " + production_begin + " end: " + production_end);
//        System.out.println("SHIPPING | begin: " + shipping_begin + " end: " + shipping_end);
//
//        unload_begin = 0;
//        unload_end = 0;
//        production_begin = 0;
//        production_end = 0;
//        shipping_begin = 0;
//        shipping_end = 0;

        // **** SHIPPING ****
        int deliveryDate = order.getDeliveryDate();

        if (order.getQty() <= shipping_capacity) {
            shipping_end = deliveryDate;
            shipping_begin = shipping_end;
        } else {
            shipping_end = deliveryDate - 1;
            shipping_begin = shipping_end - order.getQty() / shipping_capacity;
        }

        // **** PRODUCTION ****
        // Supondo que a produção termina no dia anterior ao dia de entrega
        production_end = shipping_begin - 1;
        int days2production = production_t / oneDay;

        // Dar uma folga de mais um dia para produção
        production_begin = production_end - days2production + 1;

        // **** UNLOAD ****
        unload_end = production_begin - 1;
        if (order.getQty() <= unload_capacity)
            unload_begin = unload_end;
        else
            unload_begin = unload_end - order.getQty() / unload_capacity;

//        System.out.println("UNLOAD | begin: " + unload_begin + " end: " + unload_end);
//        System.out.println("PRODUCTION | begin: " + production_begin + " end: " + production_end);
//        System.out.println("SHIPPING | begin: " + shipping_begin + " end: " + shipping_end);

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
