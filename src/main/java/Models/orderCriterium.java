package Models;

import Controllers.manufacturingOrderController;

import java.util.ArrayList;
import java.util.Vector;


public abstract class orderCriterium {

    public abstract void ordering(ArrayList<manufacturingOrderController> MyDetailedOrder);
    public abstract Vector<Integer> scheduler(manufacturingOrderController MyNewDetailedOrder, long current_t);

}
