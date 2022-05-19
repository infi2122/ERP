package SQL;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class SQL {
    private Connection conn;
    private Statement st;
    private ResultSet rs = null;

    public SQL() {
        String url = "jdbc:postgresql://db.fe.up.pt/meec1a0303";
        String user = "meec1a0303";
        String password = "stockAlote20";
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
            st = conn.createStatement();
            st.execute("SET search_path TO infi");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getConnection() {return conn;}
    public Statement getStatement() {return st;}
    public ResultSet getResultSet(){ return rs;}
    public String getStringFromRS(String parameter){
        String s = null;
        try {
            s = rs.getString(parameter);
        }catch (Exception e) {
            System.out.println("Sql exception: RS error");
        }
        return s;
    }
    public int getIntFromRS(String parameter){
        int i = 0;
        try {
            i = rs.getInt(parameter);
        }catch (Exception e) {
            System.out.println("Sql exception: RS error");
        }
        return i;
    }

    public double getDoubleFromRS(String parameter){
        double d = 0;
        try {
            d = rs.getDouble(parameter);
        }catch (Exception e) {
            System.out.println("Sql exception: RS error");
        }
        return d;
    }

    public long getLongFromRS(String parameter){
        long l = 0;
        try {
            l = rs.getLong(parameter);
        }catch (Exception e) {
            System.out.println("Sql exception: RS error");
        }
        return l;
    }

    public boolean getBoolFromRS(String parameter){
        boolean b = false;
        try {
            b = rs.getBoolean(parameter);
        }catch (Exception e) {
            System.out.println("Sql exception: RS error");
        }
        return b;
    }


    public boolean hasRows(String table){
        try {
            rs = st.executeQuery("SELECT * FROM "+table);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try{
            if(rs.next()==false) return false;
            else return true;
        }catch(Exception ex){
            System.out.println("No rows");
        }
        return false;
    }
    public List<Object> getERPFields(){
        int erpID = getIntFromRS("erpID");
        int currentTime = getIntFromRS("currentTime");

        return Arrays.asList(erpID,currentTime);
    }
    public List<Object> getClientOrderFields(){
        /*try {
            rs = st.executeQuery("SELECT * FROM warehouse");
            rs.next();
        }catch(Exception e){
            System.out.println("SQL exception: warehouse fields error");
        }*/

        String name = getStringFromRS("ClientName");
        int orderNum = getIntFromRS("orderNum");
        int pieceType = getIntFromRS("pieceType");
        int qty = getIntFromRS("qty");
        int deliveryDate = getIntFromRS("deliveryDate");
        double penDelay = getDoubleFromRS("penDelay");
        long penAdvance = getLongFromRS("penAdvance");


        return Arrays.asList(name,orderNum,pieceType,qty,deliveryDate,penDelay,penAdvance);
    }
    public List<Object> getInternalOrdersFields(){
        int startDate = getIntFromRS("startDate");
        int orderID = getIntFromRS("orderID");

        return Arrays.asList(startDate, orderID);
    }
    public List<Object> getManufacturingOrderFields(){
        int productionID = getIntFromRS("productionID");
        double productionTime = getDoubleFromRS("productionTime");
        int beginDate = getIntFromRS("beginDate");
        int endDate = getIntFromRS("endDate");

        return Arrays.asList(productionID,productionTime,beginDate,endDate);
    }
    public List<Object> getRawMaterialOrderFields(){

        int qty = getIntFromRS("qty");
        int reserved_qty = getIntFromRS("reserved_qty");
        int piece_type = getIntFromRS("piece_type");
        int timeToPlaceOrder = getIntFromRS("timeToPlaceOrder");
        int arrivalTime = getIntFromRS("arrivalTime");
        int pk_RawMaterialOrder = getIntFromRS("pk_RawMaterialOrder");

        return Arrays.asList(qty,reserved_qty,piece_type,timeToPlaceOrder);
    }

    public List<Object> getSupplierFields(){

        String name = getStringFromRS("name");
        int deliveryTime = getIntFromRS("deliveryTime");
        int minQty = getIntFromRS("minQty");

        return Arrays.asList(name,deliveryTime,minQty);
    }

    public List<Object> getRawPieceFields(){

        int type = getIntFromRS("type");
        int unitCost = getIntFromRS("unitCost");

        return Arrays.asList(type, unitCost);
    }

    public void executeQuery(String str){
        //System.out.println(str);
        try {
            st.executeUpdate(str);
        } catch (Exception e) {
            //System.out.println("Sql exception: query error");
        }
    }


    public void insertERP(int erpID, long currentTime, int countdays) {
        String query = "INSERT INTO \"erp\" VALUES(" + erpID + "," + currentTime + ", "+countdays+");";
        executeQuery(query);
    }

    public boolean checkERP(int erpID){
        //String query = "INSERT INTO Supplier VALUES('"+name+"',"+deliveryTime+", "+minQty+");";
        //String query = "SELECT exists (select * FROM manufacturingOrder WHERE productionID = "+productionID_value+");";
        //executeQuery(query);
        String query = "SELECT * FROM \"erp\" WHERE \"erpID\" = "+erpID;
        //System.out.println(query);

        try {
            rs = st.executeQuery(query);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try{
            if(rs.next()==false) return false;
            else return true;
        }catch(Exception ex){
            System.out.println("No values");
        }
        return false;


    }

    public void updateERP(int erpID, long currentTime, int countdays){

        String query = "UPDATE \"erp\" SET \"currentTime\" ="+currentTime+" WHERE \"erpID\" ="+erpID;
        executeQuery(query);
        query = "UPDATE \"erp\" SET \"countdays\" ="+countdays+" WHERE \"erpID\" ="+erpID;
        executeQuery(query);
        //System.out.println(query);
    }


    public void insertClientOrder(String clientName, int orderNum, int pieceType, int qty, int deliveryDate, float penDelay,
                                  float penAdvance){
        String query = "INSERT INTO \"ClientOrder\" VALUES('"+clientName+"',"+orderNum+","+pieceType+","+qty+", "+deliveryDate+"," +
                penDelay+", "+penAdvance+");";
        executeQuery(query);
        System.out.println(query);
    }

    public boolean checkmanufacturingOrderproductionID(int productionID_value){
        //String query = "INSERT INTO Supplier VALUES('"+name+"',"+deliveryTime+", "+minQty+");";
        //String query = "SELECT exists (select * FROM manufacturingOrder WHERE productionID = "+productionID_value+");";
        //executeQuery(query);
        String query = "SELECT * FROM \"manufacturingOrder\" WHERE \"productionID\" = "+productionID_value;
        //System.out.println(query);

        try {
            rs = st.executeQuery(query);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try{
            if(rs.next()==false) return false;
            else return true;
        }catch(Exception ex){
            System.out.println("No values");
        }
        return false;


    }
    public void insertManufacturingOrder(int productionID, String ClientName, int expectedRawMaterialArrival, int expectedProductionStart, int expectedShippingStart, int meanSFS_t, int meanProduction_t, int totalCost){
        String query = "INSERT INTO \"manufacturingOrder\" VALUES("+productionID+",'"+ ClientName+"'," +
                expectedRawMaterialArrival+","+expectedProductionStart+"," + expectedShippingStart + ", "+meanSFS_t+", "+meanProduction_t+", "+totalCost+");";
        executeQuery(query);
        //System.out.println(query);
    }

    public void updateManufacturingOrder(int productionID, int expectedRawMaterialArrival, int expectedProductionStart, int expectedShippingStart, int meanSFS_t, int meanProduction_t, int totalCost){

        String query = "UPDATE \"manufacturingOrder\" SET \"expectedRawMaterialArrival\"="+expectedRawMaterialArrival+" WHERE \"productionID\"="+productionID;
        executeQuery(query);
        query = "UPDATE \"manufacturingOrder\" SET \"expectedProductionStart\"="+expectedProductionStart+" WHERE \"productionID\"="+productionID;
        executeQuery(query);
        query = "UPDATE \"manufacturingOrder\" SET \"expectedShippingStart\"="+expectedShippingStart+" WHERE \"productionID\"="+productionID;
        executeQuery(query);
        query = "UPDATE \"manufacturingOrder\" SET \"meanSFS_t\"="+meanSFS_t+" WHERE \"productionID\"="+productionID;
        executeQuery(query);
        query = "UPDATE \"manufacturingOrder\" SET \"meanProduction_t\"="+meanProduction_t+" WHERE \"productionID\"="+productionID;
        executeQuery(query);
        query = "UPDATE \"manufacturingOrder\" SET \"totalCost\"="+totalCost+" WHERE \"productionID\"="+productionID;
        executeQuery(query);
    }

    public boolean checkshippingOrdermanufacturingID(int manufacturingID_value){
        //String query = "INSERT INTO Supplier VALUES('"+name+"',"+deliveryTime+", "+minQty+");";
        //String query = "SELECT exists (select * FROM manufacturingOrder WHERE productionID = "+productionID_value+");";
        //executeQuery(query);
        String query = "SELECT * FROM \"ShippingOrder\" WHERE \"manufacturingID\" = "+manufacturingID_value;
        //System.out.println(query);

        try {
            rs = st.executeQuery(query);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try{
            if(rs.next()==false) return false;
            else return true;
        }catch(Exception ex){
            System.out.println("No values");
        }
        return false;


    }

    public void insertShippingOrder(int manufacturingID, int quantity, int startShippingDate){
        String query = "INSERT INTO \"ShippingOrder\" VALUES("+manufacturingID+","+ quantity+"," +
                startShippingDate+");";
        executeQuery(query);
        //System.out.println(query);
    }

    public void updateShippingOrder(int manufacturingID, int quantity, int startShippingDate){

        String query = "UPDATE \"ShippingOrder\" SET \"quantity\"="+quantity+" WHERE \"manufacturingID\"="+manufacturingID;
        executeQuery(query);
        query = "UPDATE \"ShippingOrder\" SET \"startShippingDate\"="+startShippingDate+" WHERE \"manufacturingID\"="+manufacturingID;
        executeQuery(query);
    }


    public boolean checkproductionOrdermanufacturingID(int manufacturingID_value){
        //String query = "INSERT INTO Supplier VALUES('"+name+"',"+deliveryTime+", "+minQty+");";
        //String query = "SELECT exists (select * FROM manufacturingOrder WHERE productionID = "+productionID_value+");";
        //executeQuery(query);
        String query = "SELECT * FROM \"ProductionOrder\" WHERE \"manufacturingID\" = "+manufacturingID_value;
        //System.out.println(query);

        try {
            rs = st.executeQuery(query);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try{
            if(rs.next()==false) return false;
            else return true;
        }catch(Exception ex){
            System.out.println("No values");
        }
        return false;


    }

    public void insertProductionOrder(int manufacturingID, int quantity, int finalType, int startProdutionDate){
        String query = "INSERT INTO \"ProductionOrder\" VALUES("+manufacturingID+","+ quantity+"," +
                finalType+", "+startProdutionDate+");";
        executeQuery(query);
        //System.out.println(query);
    }

    public void updateProductionOrder(int manufacturingID, int quantity, int finalType, int startProdutionDate){

        String query = "UPDATE \"ProductionOrder\" SET \"quantity\"="+quantity+" WHERE \"manufacturingID\"="+manufacturingID;
        executeQuery(query);
        query = "UPDATE \"ProductionOrder\" SET \"finalType\"="+finalType+" WHERE \"manufacturingID\"="+manufacturingID;
        executeQuery(query);
        query = "UPDATE \"ProductionOrder\" SET \"startProdutionDat\"e="+startProdutionDate+" WHERE \"manufacturingID\"="+manufacturingID;
        executeQuery(query);
    }

    public boolean checkrawMaterialOrderID_QtyRawMaterial(int rawMaterialOrderID){
        //String query = "INSERT INTO Supplier VALUES('"+name+"',"+deliveryTime+", "+minQty+");";
        //String query = "SELECT exists (select * FROM manufacturingOrder WHERE productionID = "+productionID_value+");";
        //executeQuery(query);
        String query = "SELECT * FROM \"rawMaterialOrderID_QtyRawMaterial\" WHERE \"rawMaterialOrderID\" = "+rawMaterialOrderID;
        //System.out.println(query);

        try {
            rs = st.executeQuery(query);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try{
            if(rs.next()==false) return false;
            else return true;
        }catch(Exception ex){
            System.out.println("No values");
        }
        return false;


    }

    public void insertrawMaterialOrderID_QtyRawMaterial(int serial_rawMaterialOrderID, int rawMaterialOrderID, int qtyRawMaterial, int manufacturingID){
        String query = "INSERT INTO \"rawMaterialOrderID_QtyRawMaterial\" VALUES("+serial_rawMaterialOrderID+","+rawMaterialOrderID+","+ qtyRawMaterial+"," +
                manufacturingID+");";
        executeQuery(query);
        //System.out.println(query);
    }

    public void updaterawMaterialOrderID_QtyRawMaterial(int rawMaterialOrderID, int qtyRawMaterial, int manufacturingID){

        String query = "UPDATE \"rawMaterialOrderID_QtyRawMaterial\" SET \"qtyRawMaterial\"="+qtyRawMaterial+" WHERE \"rawMaterialOrderID\"="+rawMaterialOrderID;
        executeQuery(query);
        query = "UPDATE \"rawMaterialOrderID_QtyRawMaterial\" SET \"manufacturingID\"="+manufacturingID+" WHERE \"rawMaterialOrderID\"="+rawMaterialOrderID;
        //executeQuery(query);
    }


    public boolean checkRawMaterialOrder(int id){
        //String query = "INSERT INTO Supplier VALUES('"+name+"',"+deliveryTime+", "+minQty+");";
        //String query = "SELECT exists (select * FROM manufacturingOrder WHERE productionID = "+productionID_value+");";
        //executeQuery(query);
        String query = "SELECT * FROM \"RawMaterialOrder\" WHERE \"id\" = "+id;
        System.out.println(query);

        try {
            rs = st.executeQuery(query);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try{
            if(rs.next()==false) return false;
            else return true;
        }catch(Exception ex){
            System.out.println("No values");
        }
        return false;


    }
    public void insertRawMaterialOrder(int id, int qty, int piece_type, int timeToPlaceOrder, int arrivalTime, String name){
        String query = "INSERT INTO \"RawMaterialOrder\" VALUES("+id+","+qty+","+ piece_type+"," +
                timeToPlaceOrder+", "+arrivalTime+", '"+name+"');";
        executeQuery(query);
        System.out.println(query);
    }

    public void updateRawMaterialOrder(int id, int qty, int piece_type, int timeToPlaceOrder, int arrivalTime){

        String query = "UPDATE \"RawMaterialOrder\" SET \"qty\"="+qty+" WHERE \"id\"="+id;
        System.out.println(query);
        executeQuery(query);
        query = "UPDATE \"RawMaterialOrder\" SET \"piece_type\"="+piece_type+" WHERE \"id\"="+id;
        System.out.println(query);
        executeQuery(query);
        query = "UPDATE \"RawMaterialOrder\" SET \"timeToPlaceOrder\"="+timeToPlaceOrder+" WHERE \"id\"="+id;
        System.out.println(query);
        executeQuery(query);
        query = "UPDATE \"RawMaterialOrder\" SET \"arrivalTime\"="+arrivalTime+" WHERE \"id\"="+id;
        System.out.println(query);
        executeQuery(query);
    }


    public void insertSupplier(String name, int deliveryTime, int minQty){
        String query = "INSERT INTO \"Supplier\" VALUES('"+name+"',"+deliveryTime+","+minQty+");";
        executeQuery(query);
    }




    public boolean checkRawPiece(int type, int unitCost){
        //String query = "INSERT INTO Supplier VALUES('"+name+"',"+deliveryTime+", "+minQty+");";
        //String query = "SELECT exists (select * FROM manufacturingOrder WHERE productionID = "+productionID_value+");";
        //executeQuery(query);
        String query = "SELECT * FROM \"RawPiece\" WHERE \"type\" = "+type+" AND \"unitCost\" = "+unitCost;
        //System.out.println(query);

        try {
            rs = st.executeQuery(query);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try{
            if(rs.next()==false) return false;
            else return true;
        }catch(Exception ex){
            System.out.println("No values");
        }
        return false;


    }

    public void insertRawPiece(int type, int unitCost, String name){
        String query = "INSERT INTO \"RawPiece\" VALUES("+type+","+unitCost+",'"+ name+"');";
        executeQuery(query);
        //System.out.println(query);
    }

    /*public void updateRawPiece(int rawMaterialOrderID, int qtyRawMaterial, int manufacturingID){

        String query = "UPDATE \"RawPiece\" SET qtyRawMaterial="+qtyRawMaterial+" WHERE rawMaterialOrderID="+rawMaterialOrderID;
        executeQuery(query);
        query = "UPDATE \"rawMaterialOrderID_QtyRawMaterial\" SET manufacturingID="+manufacturingID+" WHERE rawMaterialOrderID="+rawMaterialOrderID;
        executeQuery(query);
    }*/



    public void createSQLtables() {
        try {
            st.execute("CREATE TABLE IF NOT EXISTS erp(erpID INT NOT NULL,currentTime INT NOT NULL,CONSTRAINT PK_erp PRIMARY KEY (erpID));");
            st.execute("CREATE TABLE IF NOT EXISTS manufacturingOrder(productionID INT NOT NULL,productionTime Double precision NOT NULL,beginDate INT NOT NULL,endDate INT NOT NULL, clientName VARCHAR(20) NOT NULL,CONSTRAINT PK_manufacturingOrder PRIMARY KEY (productionID));");
            st.execute("CREATE TABLE IF NOT EXISTS ClientOrder(clientName VARCHAR(20) NOT NULL,orderNum INT NOT NULL,pieceType INT NOT NULL,qty INT NOT NULL,deliveryDate INT NOT NULL," +
                    "penDelay Double precision NOT NULL,penAdvance Bigint NOT NULL,productionID INT NOT NULL, CONSTRAINT PK_ClientOrder PRIMARY KEY (clientName));");

            st.execute("CREATE TABLE IF NOT EXISTS internalOrders(startDate INT NOT NULL,orderID INT NOT NULL, CONSTRAINT PK_internalOrders PRIMARY KEY (orderID));");
            st.execute("CREATE TABLE IF NOT EXISTS ReceiveOrder()INHERITS (internalOrders);");
            st.execute("CREATE TABLE IF NOT EXISTS ProductionOrder()INHERITS (internalOrders);");
            st.execute("CREATE TABLE IF NOT EXISTS ShippingOrder()INHERITS (internalOrders);");

            st.execute("CREATE TABLE IF NOT EXISTS RawMaterialOrder(qty INT NOT NULL,reserved_qty INT NOT NULL,piece_type INT NOT NULL,timeToPlaceOrder INT NOT NULL,arrivalTime INT NOT NULL,pk_RawMaterialOrder INT NOT NULL,productionID INT NOT NULL,name VARCHAR(20) NOT NULL, CONSTRAINT PK_RawMaterialOrder PRIMARY KEY (pk_RawMaterialOrder));");

            st.execute("CREATE TABLE IF NOT EXISTS Supplier(name VARCHAR(20) NOT NULL,deliveryTime INT NOT NULL,minQty INT NOT NULL, CONSTRAINT PK_Supplier PRIMARY KEY (name));");

            st.execute("CREATE TABLE IF NOT EXISTS RawPiece(type INT NOT NULL,unitCost INT NOT NULL,name VARCHAR(20) NOT NULL, CONSTRAINT PK_RawPiece PRIMARY KEY (type,unitCost));");

            st.execute("ALTER TABLE manufacturingOrder ADD CONSTRAINT Relationship8 FOREIGN KEY (clientName) REFERENCES ClientOrder (clientName) ON DELETE NO ACTION ON UPDATE NO ACTION;");
            st.execute("ALTER TABLE RawMaterialOrder ADD CONSTRAINT Relationship10 FOREIGN KEY (productionID) REFERENCES manufacturingOrder (productionID) ON DELETE NO ACTION ON UPDATE NO ACTION;");
            st.execute("ALTER TABLE ClientOrder ADD CONSTRAINT Relationship20 FOREIGN KEY (productionID) REFERENCES manufacturingOrder (productionID) ON DELETE NO ACTION ON UPDATE NO ACTION;");

            st.execute("ALTER TABLE ReceiveOrder ADD CONSTRAINT Relationship25 FOREIGN KEY (erpID) REFERENCES erp (erpID) ON DELETE NO ACTION ON UPDATE NO ACTION;");
            st.execute("ALTER TABLE ProductionOrder ADD CONSTRAINT Relationship26 FOREIGN KEY (erpID) REFERENCES erp (erpID) ON DELETE NO ACTION ON UPDATE NO ACTION;");
            st.execute("ALTER TABLE ShippingOrder ADD CONSTRAINT Relationship27 FOREIGN KEY (erpID) REFERENCES erp (erpID) ON DELETE NO ACTION ON UPDATE NO ACTION;");

            st.execute("ALTER TABLE manufacturingOrder ADD CONSTRAINT Relationship28 FOREIGN KEY (erpID) REFERENCES erp (erpID) ON DELETE NO ACTION ON UPDATE NO ACTION;");

            st.execute("ALTER TABLE RawPiece ADD CONSTRAINT Relationship29 FOREIGN KEY (name) REFERENCES Supplier (name) ON DELETE NO ACTION ON UPDATE NO ACTION;");

            st.execute("ALTER TABLE RawMaterialOrder ADD CONSTRAINT Relationship32 FOREIGN KEY (name) REFERENCES Supplier (name) ON DELETE NO ACTION ON UPDATE NO ACTION;");

        } catch (Exception e) {
            System.out.println("Sql exception: Creating tables error");
        }
    }

    public void dropSQLtables(){
        try {
            st.execute("DROP TABLE IF EXISTS erp CASCADE");
            st.execute("DROP TABLE IF EXISTS manufacturingOrder CASCADE");
            st.execute("DROP TABLE IF EXISTS ClientOrder CASCADE");
            st.execute("DROP TABLE IF EXISTS internalOrders CASCADE");
            st.execute("DROP TABLE IF EXISTS ReceiveOrder CASCADE");
            st.execute("DROP TABLE IF EXISTS ProductionOrder CASCADE");
            st.execute("DROP TABLE IF EXISTS ShippingOrder CASCADE");
            st.execute("DROP TABLE IF EXISTS RawMaterialOrder CASCADE");
            st.execute("DROP TABLE IF EXISTS Supplier CASCADE");
            st.execute("DROP TABLE IF EXISTS RawPiece CASCADE");
        }catch (Exception e) {
            System.out.println("Sql exception: Dropping tables error");
        }
    }

}
