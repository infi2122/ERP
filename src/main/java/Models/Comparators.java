package Models;

import java.util.ArrayList;

import java.util.Comparator;

public class Comparators {

    class rawOrdersComparator implements Comparator<rawMaterialOrder> {

        public int compare(rawMaterialOrder o1, rawMaterialOrder o2) {

            if (o1.getArrivalTime() == o2.getArrivalTime())
                return 0;
            else if (o1.getArrivalTime() > o2.getArrivalTime())
                return 1;
            else
                return -1;

        }
    }

    public void rawOrdering(ArrayList<rawMaterialOrder> arrayList) {

        arrayList.sort(new rawOrdersComparator());

    }

    class prodOrdersComparator implements Comparator<productionOrder> {

        public int compare(productionOrder o1, productionOrder o2) {

            if (o1.getStartProdutionDate() == o2.getStartProdutionDate())
                return 0;
            else if (o1.getStartProdutionDate() > o2.getStartProdutionDate())
                return 1;
            else
                return -1;

        }
    }

    public void prodOrdering(ArrayList<productionOrder> arrayList) {

        arrayList.sort(new prodOrdersComparator());

    }

    class shipOrdersComparator implements Comparator<shippingOrder> {

        public int compare(shippingOrder o1, shippingOrder o2) {

            if (o1.getStartShippingDate() == o2.getStartShippingDate())
                return 0;
            else if (o1.getStartShippingDate() > o2.getStartShippingDate())
                return 1;
            else
                return -1;

        }
    }

    public void shipOrdering(ArrayList<shippingOrder> arrayList) {

        arrayList.sort(new shipOrdersComparator());

    }






}
