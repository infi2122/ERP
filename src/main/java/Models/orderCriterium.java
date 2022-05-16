package Models;

import java.util.ArrayList;
import java.util.Vector;


public abstract class orderCriterium {

    public abstract void ordering(ArrayList<manufacturingOrder> MyDetailedOrder);
    public abstract Vector<Integer> scheduler(manufacturingOrder MyNewDetailedOrder, long current_t);

}
