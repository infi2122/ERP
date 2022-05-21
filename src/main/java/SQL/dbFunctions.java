package SQL;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class dbFunctions {
    private Connection conn;
    private Statement st;
    private ResultSet rs = null;

    public dbFunctions() {
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

    public Connection getConnection() {
        return conn;
    }

    public Statement getStatement() {
        return st;
    }

    public ResultSet getResultSet() {
        return rs;
    }

    public String getStringFromRS(String parameter) {
        String s = null;
        try {
            s = rs.getString(parameter);
        } catch (Exception e) {
            System.out.println("Sql exception: RS error");
        }
        return s;
    }

    public int getIntFromRS(String parameter) {
        int i = 0;
        try {
            i = rs.getInt(parameter);
        } catch (Exception e) {
            System.out.println("Sql exception: RS error");
        }
        return i;
    }

    public double getDoubleFromRS(String parameter) {
        double d = 0;
        try {
            d = rs.getDouble(parameter);
        } catch (Exception e) {
            System.out.println("Sql exception: RS error");
        }
        return d;
    }

    public long getLongFromRS(String parameter) {
        long l = 0;
        try {
            l = rs.getLong(parameter);
        } catch (Exception e) {
            System.out.println("Sql exception: RS error");
        }
        return l;
    }

    public boolean getBoolFromRS(String parameter) {
        boolean b = false;
        try {
            b = rs.getBoolean(parameter);
        } catch (Exception e) {
            System.out.println("Sql exception: RS error");
        }
        return b;
    }


    public boolean hasRows(String table) {
        try {
            rs = st.executeQuery("SELECT * FROM " + table);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try {
            if (rs.next() == false) return false;
            else return true;
        } catch (Exception ex) {
            System.out.println("No rows");
        }
        return false;
    }

    public List<Object> getERPFields() {
        int erpID = getIntFromRS("erpID");
        int currentTime = getIntFromRS("currentTime");

        return Arrays.asList(erpID, currentTime);
    }

    public List<Object> getClientOrderFields() {
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


        return Arrays.asList(name, orderNum, pieceType, qty, deliveryDate, penDelay, penAdvance);
    }

    public List<Object> getInternalOrdersFields() {
        int startDate = getIntFromRS("startDate");
        int orderID = getIntFromRS("orderID");

        return Arrays.asList(startDate, orderID);
    }

    public List<Object> getManufacturingOrderFields() {
        int productionID = getIntFromRS("productionID");
        double productionTime = getDoubleFromRS("productionTime");
        int beginDate = getIntFromRS("beginDate");
        int endDate = getIntFromRS("endDate");

        return Arrays.asList(productionID, productionTime, beginDate, endDate);
    }

    public List<Object> getRawMaterialOrderFields() {

        int qty = getIntFromRS("qty");
        int reserved_qty = getIntFromRS("reserved_qty");
        int piece_type = getIntFromRS("piece_type");
        int timeToPlaceOrder = getIntFromRS("timeToPlaceOrder");
        int arrivalTime = getIntFromRS("arrivalTime");
        int pk_RawMaterialOrder = getIntFromRS("pk_RawMaterialOrder");

        return Arrays.asList(qty, reserved_qty, piece_type, timeToPlaceOrder);
    }

    public List<Object> getSupplierFields() {

        String name = getStringFromRS("name");
        int deliveryTime = getIntFromRS("deliveryTime");
        int minQty = getIntFromRS("minQty");

        return Arrays.asList(name, deliveryTime, minQty);
    }

    public List<Object> getRawPieceFields() {

        int type = getIntFromRS("type");
        int unitCost = getIntFromRS("unitCost");

        return Arrays.asList(type, unitCost);
    }

    public void executeQuery(String str) {
        //System.out.println(str);
        try {
            st.executeUpdate(str);
        } catch (Exception e) {
            //System.out.println("Sql exception: query error");
        }
    }


    public void insertERP(int erpID, long currentTime, int countdays) {
        String query = "INSERT INTO \"erp\" VALUES(" + erpID + "," + currentTime + ", " + countdays + ");";
        executeQuery(query);
    }

    public void insertsharedResources(long startTime, String internalOrdersConcat, String clientOrders, String finishedOrdersInfo) {
        String query = "INSERT INTO \"sharedResources\" VALUES(" + startTime + ",'" + internalOrdersConcat + "', '" + clientOrders + "', '" + finishedOrdersInfo + "');";
        executeQuery(query);
        System.out.println(query);
    }

    public void updatesharedResources(long startTime, String internalOrdersConcat, String clientOrders, String finishedOrdersInfo) {

        String query = "UPDATE \"sharedResources\" SET \"internalOrdersConcat\" =" + internalOrdersConcat + " WHERE \"startTime\" =" + startTime;
        executeQuery(query);
        query = "UPDATE \"sharedResources\" SET \"clientOrders\" =" + clientOrders + " WHERE \"startTime\" =" + startTime;
        executeQuery(query);
        query = "UPDATE \"sharedResources\" SET \"finishedOrdersInfo\" =" + finishedOrdersInfo + " WHERE \"startTime\" =" + startTime;
        executeQuery(query);
        //System.out.println(query);
    }

    public boolean checkERP(int erpID) {
        //String query = "INSERT INTO Supplier VALUES('"+name+"',"+deliveryTime+", "+minQty+");";
        //String query = "SELECT exists (select * FROM manufacturingOrder WHERE productionID = "+productionID_value+");";
        //executeQuery(query);
        String query = "SELECT * FROM \"erp\" WHERE \"erpID\" = " + erpID;
        //System.out.println(query);

        try {
            rs = st.executeQuery(query);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try {
            if (rs.next() == false) return false;
            else return true;
        } catch (Exception ex) {
            System.out.println("No values");
        }
        return false;


    }

    public void updateERP(int erpID, long currentTime, int countdays) {

        String query = "UPDATE \"erp\" SET \"currentTime\" =" + currentTime + " WHERE \"erpID\" =" + erpID;
        executeQuery(query);
        query = "UPDATE \"erp\" SET \"countdays\" =" + countdays + " WHERE \"erpID\" =" + erpID;
        executeQuery(query);
        //System.out.println(query);
    }


    public void insertClientOrder(String clientName, int orderNum, int pieceType, int qty, int deliveryDate, float penDelay,
                                  float penAdvance) {
        String query = "INSERT INTO \"ClientOrder\" VALUES('" + clientName + "'," + orderNum + "," + pieceType + "," + qty + ", " + deliveryDate + "," +
                penDelay + ", " + penAdvance + ");";
        executeQuery(query);
        //System.out.println(query);
    }

    public boolean checkmanufacturingOrderproductionID(int productionID_value) {
        //String query = "INSERT INTO Supplier VALUES('"+name+"',"+deliveryTime+", "+minQty+");";
        //String query = "SELECT exists (select * FROM manufacturingOrder WHERE productionID = "+productionID_value+");";
        //executeQuery(query);
        String query = "SELECT * FROM \"manufacturingOrder\" WHERE \"productionID\" = " + productionID_value;
        //System.out.println(query);

        try {
            rs = st.executeQuery(query);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try {
            if (rs.next() == false) return false;
            else return true;
        } catch (Exception ex) {
            System.out.println("No values");
        }
        return false;


    }

    public void insertManufacturingOrder(int productionID, String ClientName, int expectedRawMaterialArrival, int expectedProductionStart,
                                         int expectedShippingStart, int meanSFS_t, int meanProduction_t, int finalDay, int totalCost, int orderNum) {
        String query = "INSERT INTO \"manufacturingOrder\" VALUES(" + productionID + ",'" + ClientName + "'," +
                expectedRawMaterialArrival + "," + expectedProductionStart + "," + expectedShippingStart + ", " + meanSFS_t + ", " + meanProduction_t + ", " + finalDay + ", " + totalCost + ", " + orderNum + ", 1);";
        executeQuery(query);
        //System.out.println(query);
    }

    public void updateManufacturingOrder(int productionID, int expectedRawMaterialArrival, int expectedProductionStart, int expectedShippingStart, int meanSFS_t, int meanProduction_t, int finalDay, int totalCost) {

        String query = "UPDATE \"manufacturingOrder\" SET \"expectedRawMaterialArrival\"=" + expectedRawMaterialArrival + " WHERE \"productionID\"=" + productionID;
        executeQuery(query);
        query = "UPDATE \"manufacturingOrder\" SET \"expectedProductionStart\"=" + expectedProductionStart + " WHERE \"productionID\"=" + productionID;
        executeQuery(query);
        query = "UPDATE \"manufacturingOrder\" SET \"expectedShippingStart\"=" + expectedShippingStart + " WHERE \"productionID\"=" + productionID;
        executeQuery(query);
        query = "UPDATE \"manufacturingOrder\" SET \"meanSFS_t\"=" + meanSFS_t + " WHERE \"productionID\"=" + productionID;
        executeQuery(query);
        query = "UPDATE \"manufacturingOrder\" SET \"meanProduction_t\"=" + meanProduction_t + " WHERE \"productionID\"=" + productionID;
        executeQuery(query);
        query = "UPDATE \"manufacturingOrder\" SET \"finalDay\"=" + finalDay + " WHERE \"productionID\"=" + productionID;
        executeQuery(query);
        query = "UPDATE \"manufacturingOrder\" SET \"totalCost\"=" + totalCost + " WHERE \"productionID\"=" + productionID;
        executeQuery(query);
    }

    public boolean checkshippingOrdermanufacturingID(int manufacturingID_value) {
        //String query = "INSERT INTO Supplier VALUES('"+name+"',"+deliveryTime+", "+minQty+");";
        //String query = "SELECT exists (select * FROM manufacturingOrder WHERE productionID = "+productionID_value+");";
        //executeQuery(query);
        String query = "SELECT * FROM \"ShippingOrder\" WHERE \"manufacturingID\" = " + manufacturingID_value;
        //System.out.println(query);

        try {
            rs = st.executeQuery(query);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try {
            if (rs.next() == false) return false;
            else return true;
        } catch (Exception ex) {
            System.out.println("No values");
        }
        return false;


    }

    public void insertShippingOrder(int manufacturingID, int quantity, int startShippingDate) {
        String query = "INSERT INTO \"ShippingOrder\" VALUES(" + manufacturingID + "," + quantity + "," +
                startShippingDate + ", 1);";
        executeQuery(query);
        //System.out.println(query);
    }

    public void updateShippingOrder(int manufacturingID, int quantity, int startShippingDate) {

        String query = "UPDATE \"ShippingOrder\" SET \"quantity\"=" + quantity + " WHERE \"manufacturingID\"=" + manufacturingID;
        executeQuery(query);
        query = "UPDATE \"ShippingOrder\" SET \"startShippingDate\"=" + startShippingDate + " WHERE \"manufacturingID\"=" + manufacturingID;
        executeQuery(query);
    }


    public boolean checkproductionOrdermanufacturingID(int manufacturingID_value) {
        //String query = "INSERT INTO Supplier VALUES('"+name+"',"+deliveryTime+", "+minQty+");";
        //String query = "SELECT exists (select * FROM manufacturingOrder WHERE productionID = "+productionID_value+");";
        //executeQuery(query);
        String query = "SELECT * FROM \"ProductionOrder\" WHERE \"manufacturingID\" = " + manufacturingID_value;
        //System.out.println(query);

        try {
            rs = st.executeQuery(query);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try {
            if (rs.next() == false) return false;
            else return true;
        } catch (Exception ex) {
            System.out.println("No values");
        }
        return false;


    }

    public void insertProductionOrder(int manufacturingID, int quantity, int finalType, int startProdutionDate) {
        String query = "INSERT INTO \"ProductionOrder\" VALUES(" + manufacturingID + "," + quantity + "," +
                finalType + ", " + startProdutionDate + ", 1);";
        executeQuery(query);
        //System.out.println(query);
    }

    public void updateProductionOrder(int manufacturingID, int quantity, int finalType, int startProdutionDate) {

        String query = "UPDATE \"ProductionOrder\" SET \"quantity\"=" + quantity + " WHERE \"manufacturingID\"=" + manufacturingID;
        executeQuery(query);
        query = "UPDATE \"ProductionOrder\" SET \"finalType\"=" + finalType + " WHERE \"manufacturingID\"=" + manufacturingID;
        executeQuery(query);
        query = "UPDATE \"ProductionOrder\" SET \"startProdutionDat\"e=" + startProdutionDate + " WHERE \"manufacturingID\"=" + manufacturingID;
        executeQuery(query);
    }

    public boolean checkrawMaterialOrderID_QtyRawMaterial(int rawMaterialOrderID) {
        //String query = "INSERT INTO Supplier VALUES('"+name+"',"+deliveryTime+", "+minQty+");";
        //String query = "SELECT exists (select * FROM manufacturingOrder WHERE productionID = "+productionID_value+");";
        //executeQuery(query);
        String query = "SELECT * FROM \"rawMaterialOrderID_QtyRawMaterial\" WHERE \"rawMaterialOrderID\" = " + rawMaterialOrderID;
        //System.out.println(query);

        try {
            rs = st.executeQuery(query);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try {
            if (rs.next() == false) return false;
            else return true;
        } catch (Exception ex) {
            System.out.println("No values");
        }
        return false;


    }

    public void insertrawMaterialOrderID_QtyRawMaterial(int serial_rawMaterialOrderID, int rawMaterialOrderID, int qtyRawMaterial, int manufacturingID) {
        String query = "INSERT INTO \"rawMaterialOrderID_QtyRawMaterial\" VALUES(" + serial_rawMaterialOrderID + "," + rawMaterialOrderID + "," + qtyRawMaterial + "," +
                manufacturingID + ");";
        executeQuery(query);
        //System.out.println(query);
    }

    public void insertproductionInRawMaterials(int serial_productionInRawMaterials, int orderID, int reservedQty, int id) {
        String query = "INSERT INTO \"productionInRawMaterials\" VALUES(" + serial_productionInRawMaterials + "," + orderID + "," + reservedQty + "," +
                id + ");";
        executeQuery(query);
        //System.out.println(query);
    }

    public void updaterawMaterialOrderID_QtyRawMaterial(int rawMaterialOrderID, int qtyRawMaterial, int manufacturingID) {

        String query = "UPDATE \"rawMaterialOrderID_QtyRawMaterial\" SET \"qtyRawMaterial\"=" + qtyRawMaterial + " WHERE \"rawMaterialOrderID\"=" + rawMaterialOrderID;
        executeQuery(query);
        query = "UPDATE \"rawMaterialOrderID_QtyRawMaterial\" SET \"manufacturingID\"=" + manufacturingID + " WHERE \"rawMaterialOrderID\"=" + rawMaterialOrderID;
        //executeQuery(query);
    }


    public boolean checkrawRawMaterialOrder(int id) {
        //String query = "INSERT INTO Supplier VALUES('"+name+"',"+deliveryTime+", "+minQty+");";
        //String query = "SELECT exists (select * FROM manufacturingOrder WHERE productionID = "+productionID_value+");";
        //executeQuery(query);
        String query = "SELECT * FROM \"RawMaterialOrder\" WHERE \"id\" = " + id;
        System.out.println(query);

        try {
            rs = st.executeQuery(query);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try {
            if (rs.next() == false) return false;
            else return true;
        } catch (Exception ex) {
            System.out.println("No values");
        }
        return false;


    }

    public void insertRawMaterialOrder(int id, int qty, int piece_type, int timeToPlaceOrder, int arrivalTime, String name) {
        String query = "INSERT INTO \"RawMaterialOrder\" VALUES(" + id + "," + qty + "," + piece_type + "," +
                timeToPlaceOrder + ", " + arrivalTime + ", '" + name + "', 1);";
        executeQuery(query);
        //System.out.println(query);
    }

    public void updateRawMaterialOrder(int id, int qty, int piece_type, int timeToPlaceOrder, int arrivalTime) {

        String query = "UPDATE \"RawMaterialOrder\" SET \"qty\"=" + qty + " WHERE \"id\"=" + id;
        System.out.println(query);
        executeQuery(query);
        query = "UPDATE \"RawMaterialOrder\" SET \"piece_type\"=" + piece_type + " WHERE \"id\"=" + id;
        System.out.println(query);
        executeQuery(query);
        query = "UPDATE \"RawMaterialOrder\" SET \"timeToPlaceOrder\"=" + timeToPlaceOrder + " WHERE \"id\"=" + id;
        System.out.println(query);
        executeQuery(query);
        query = "UPDATE \"RawMaterialOrder\" SET \"arrivalTime\"=" + arrivalTime + " WHERE \"id\"=" + id;
        System.out.println(query);
        executeQuery(query);
    }


    public void insertSupplier(String name, int deliveryTime, int minQty) {
        String query = "INSERT INTO \"Supplier\" VALUES('" + name + "'," + deliveryTime + "," + minQty + ");";
        executeQuery(query);
    }


    public boolean checkrawPiece(int type, int unitCost) {
        //String query = "INSERT INTO Supplier VALUES('"+name+"',"+deliveryTime+", "+minQty+");";
        //String query = "SELECT exists (select * FROM manufacturingOrder WHERE productionID = "+productionID_value+");";
        //executeQuery(query);
        String query = "SELECT * FROM \"RawPiece\" WHERE \"type\" = " + type + " AND \"unitCost\" = " + unitCost;
        //System.out.println(query);

        try {
            rs = st.executeQuery(query);
        } catch (Exception ex) {
            rs = null;
            return false;
        }

        try {
            if (rs.next() == false) return false;
            else return true;
        } catch (Exception ex) {
            System.out.println("No values");
        }
        return false;


    }

    public void insertRawPiece(int type, int unitCost, String name) {
        String query = "INSERT INTO \"RawPiece\" VALUES(" + type + "," + unitCost + ",'" + name + "');";
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
            st.execute("CREATE SCHEMA infi");
            st.execute("-- Create tables section -------------------------------------------------\n" +
                    "\n" +
                    "-- Table erp\n" +
                    "\n" +
                    "CREATE TABLE \"erp\"\n" +
                    "(\n" +
                    "  \"erpID\" Integer NOT NULL,\n" +
                    "  \"currentTime\" Integer NOT NULL,\n" +
                    "  \"countdays\" Integer NOT NULL\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  autovacuum_enabled=true)\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"erp\" ADD CONSTRAINT \"PK_erp\" PRIMARY KEY (\"erpID\")\n" +
                    ";\n" +
                    "\n" +
                    "-- Table manufacturingOrder\n" +
                    "\n" +
                    "CREATE TABLE \"manufacturingOrder\"\n" +
                    "(\n" +
                    "  \"productionID\" Integer NOT NULL,\n" +
                    "  \"clientName\" Character varying,\n" +
                    "  \"expectedRawMaterialArrival\" Integer NOT NULL,\n" +
                    "  \"expectedProductionStart\" Integer NOT NULL,\n" +
                    "  \"expectedShippingStart\" Integer NOT NULL,\n" +
                    "  \"meanSFS_t\" Integer NOT NULL,\n" +
                    "  \"meanProduction_t\" Integer NOT NULL,\n" +
                    "  \"finalDay\" Integer NOT NULL,\n" +
                    "  \"totalCost\" Integer NOT NULL,\n" +
                    "  \"orderNum\" Integer,\n" +
                    "  \"erpID\" Integer\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  autovacuum_enabled=true)\n" +
                    ";\n" +
                    "\n" +
                    "CREATE INDEX \"IX_Relationship37\" ON \"manufacturingOrder\" (\"clientName\",\"orderNum\")\n" +
                    ";\n" +
                    "\n" +
                    "CREATE INDEX \"IX_Relationship42\" ON \"manufacturingOrder\" (\"erpID\")\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"manufacturingOrder\" ADD CONSTRAINT \"PK_manufacturingOrder\" PRIMARY KEY (\"productionID\")\n" +
                    ";\n" +
                    "\n" +
                    "-- Table ClientOrder\n" +
                    "\n" +
                    "CREATE TABLE \"ClientOrder\"\n" +
                    "(\n" +
                    "  \"clientName\" Character varying NOT NULL,\n" +
                    "  \"orderNum\" Integer NOT NULL,\n" +
                    "  \"pieceType\" Integer NOT NULL,\n" +
                    "  \"qty\" Integer NOT NULL,\n" +
                    "  \"deliveryDate\" Integer NOT NULL,\n" +
                    "  \"penDelay\" Double precision NOT NULL,\n" +
                    "  \"penAdvance\" Bigint NOT NULL\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  autovacuum_enabled=true)\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"ClientOrder\" ADD CONSTRAINT \"PK_ClientOrder\" PRIMARY KEY (\"clientName\",\"orderNum\")\n" +
                    ";\n" +
                    "\n" +
                    "-- Table ProductionOrder\n" +
                    "\n" +
                    "CREATE TABLE \"ProductionOrder\"\n" +
                    "(\n" +
                    "  \"manufacturingID\" Integer NOT NULL,\n" +
                    "  \"quantity\" Integer NOT NULL,\n" +
                    "  \"finalType\" Integer NOT NULL,\n" +
                    "  \"startProductionDate\" Integer NOT NULL,\n" +
                    "  \"erpID\" Integer\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  autovacuum_enabled=true)\n" +
                    ";\n" +
                    "\n" +
                    "CREATE INDEX \"IX_Relationship40\" ON \"ProductionOrder\" (\"erpID\")\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"ProductionOrder\" ADD CONSTRAINT \"PK_ProductionOrder\" PRIMARY KEY (\"manufacturingID\")\n" +
                    ";\n" +
                    "\n" +
                    "-- Table ShippingOrder\n" +
                    "\n" +
                    "CREATE TABLE \"ShippingOrder\"\n" +
                    "(\n" +
                    "  \"manufacturingID\" Integer NOT NULL,\n" +
                    "  \"quantity\" Integer NOT NULL,\n" +
                    "  \"startShippingDate\" Integer NOT NULL,\n" +
                    "  \"erpID\" Integer\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  autovacuum_enabled=true)\n" +
                    ";\n" +
                    "\n" +
                    "CREATE INDEX \"IX_Relationship41\" ON \"ShippingOrder\" (\"erpID\")\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"ShippingOrder\" ADD CONSTRAINT \"PK_ShippingOrder\" PRIMARY KEY (\"manufacturingID\")\n" +
                    ";\n" +
                    "\n" +
                    "-- Table RawMaterialOrder\n" +
                    "\n" +
                    "CREATE TABLE \"RawMaterialOrder\"\n" +
                    "(\n" +
                    "  \"id\" Integer NOT NULL,\n" +
                    "  \"qty\" Integer NOT NULL,\n" +
                    "  \"piece_type\" Integer NOT NULL,\n" +
                    "  \"timeToPlaceOrder\" Integer NOT NULL,\n" +
                    "  \"arrivalTime\" Integer NOT NULL,\n" +
                    "  \"name\" Character varying NOT NULL,\n" +
                    "  \"erpID\" Integer\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  autovacuum_enabled=true)\n" +
                    ";\n" +
                    "\n" +
                    "CREATE INDEX \"IX_Relationship43\" ON \"RawMaterialOrder\" (\"erpID\")\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"RawMaterialOrder\" ADD CONSTRAINT \"PK_RawMaterialOrder\" PRIMARY KEY (\"id\")\n" +
                    ";\n" +
                    "\n" +
                    "-- Table Supplier\n" +
                    "\n" +
                    "CREATE TABLE \"Supplier\"\n" +
                    "(\n" +
                    "  \"name\" Character varying NOT NULL,\n" +
                    "  \"deliveryTime\" Integer NOT NULL,\n" +
                    "  \"minQty\" Integer NOT NULL\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  autovacuum_enabled=true)\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"Supplier\" ADD CONSTRAINT \"PK_Supplier\" PRIMARY KEY (\"name\")\n" +
                    ";\n" +
                    "\n" +
                    "-- Table RawPiece\n" +
                    "\n" +
                    "CREATE TABLE \"RawPiece\"\n" +
                    "(\n" +
                    "  \"type\" Integer NOT NULL,\n" +
                    "  \"unitCost\" Integer NOT NULL,\n" +
                    "  \"name\" Character varying NOT NULL\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  autovacuum_enabled=true)\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"RawPiece\" ADD CONSTRAINT \"PK_RawPiece\" PRIMARY KEY (\"type\",\"unitCost\")\n" +
                    ";\n" +
                    "\n" +
                    "-- Table rawMaterialOrderID_QtyRawMaterial\n" +
                    "\n" +
                    "CREATE TABLE \"rawMaterialOrderID_QtyRawMaterial\"\n" +
                    "(\n" +
                    "  \"serial_rawMaterialOrderID\" Serial NOT NULL,\n" +
                    "  \"rawMaterialOrderID\" Integer NOT NULL,\n" +
                    "  \"qtyRawMaterial\" Integer NOT NULL,\n" +
                    "  \"manufacturingID\" Integer NOT NULL\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  autovacuum_enabled=true)\n" +
                    ";\n" +
                    "\n" +
                    "CREATE INDEX \"IX_Relationship34\" ON \"rawMaterialOrderID_QtyRawMaterial\" (\"manufacturingID\")\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"rawMaterialOrderID_QtyRawMaterial\" ADD CONSTRAINT \"PK_rawMaterialOrderID_QtyRawMaterial\" PRIMARY KEY (\"serial_rawMaterialOrderID\")\n" +
                    ";\n" +
                    "\n" +
                    "-- Table productionInRawMaterials\n" +
                    "\n" +
                    "CREATE TABLE \"productionInRawMaterials\"\n" +
                    "(\n" +
                    "  \"serial_productionInRawMaterials\" Serial NOT NULL,\n" +
                    "  \"orderID\" Integer NOT NULL,\n" +
                    "  \"reservedQty\" Integer NOT NULL,\n" +
                    "  \"id\" Integer\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  autovacuum_enabled=true)\n" +
                    ";\n" +
                    "\n" +
                    "CREATE INDEX \"IX_Relationship39\" ON \"productionInRawMaterials\" (\"id\")\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"productionInRawMaterials\" ADD CONSTRAINT \"PK_productionInRawMaterials\" PRIMARY KEY (\"serial_productionInRawMaterials\")\n" +
                    ";\n" +
                    "\n" +
                    "-- Create foreign keys (relationships) section -------------------------------------------------\n" +
                    "\n" +
                    "ALTER TABLE \"RawPiece\"\n" +
                    "  ADD CONSTRAINT \"Relationship29\"\n" +
                    "    FOREIGN KEY (\"name\")\n" +
                    "    REFERENCES \"Supplier\" (\"name\")\n" +
                    "      ON DELETE NO ACTION\n" +
                    "      ON UPDATE NO ACTION\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"RawMaterialOrder\"\n" +
                    "  ADD CONSTRAINT \"Relationship32\"\n" +
                    "    FOREIGN KEY (\"name\")\n" +
                    "    REFERENCES \"Supplier\" (\"name\")\n" +
                    "      ON DELETE NO ACTION\n" +
                    "      ON UPDATE NO ACTION\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"rawMaterialOrderID_QtyRawMaterial\"\n" +
                    "  ADD CONSTRAINT \"Relationship34\"\n" +
                    "    FOREIGN KEY (\"manufacturingID\")\n" +
                    "    REFERENCES \"ProductionOrder\" (\"manufacturingID\")\n" +
                    "      ON DELETE NO ACTION\n" +
                    "      ON UPDATE NO ACTION\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"manufacturingOrder\"\n" +
                    "  ADD CONSTRAINT \"Relationship37\"\n" +
                    "    FOREIGN KEY (\"clientName\", \"orderNum\")\n" +
                    "    REFERENCES \"ClientOrder\" (\"clientName\", \"orderNum\")\n" +
                    "      ON DELETE NO ACTION\n" +
                    "      ON UPDATE NO ACTION\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"productionInRawMaterials\"\n" +
                    "  ADD CONSTRAINT \"Relationship39\"\n" +
                    "    FOREIGN KEY (\"id\")\n" +
                    "    REFERENCES \"RawMaterialOrder\" (\"id\")\n" +
                    "      ON DELETE NO ACTION\n" +
                    "      ON UPDATE NO ACTION\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"ProductionOrder\"\n" +
                    "  ADD CONSTRAINT \"Relationship40\"\n" +
                    "    FOREIGN KEY (\"erpID\")\n" +
                    "    REFERENCES \"erp\" (\"erpID\")\n" +
                    "      ON DELETE NO ACTION\n" +
                    "      ON UPDATE NO ACTION\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"ShippingOrder\"\n" +
                    "  ADD CONSTRAINT \"Relationship41\"\n" +
                    "    FOREIGN KEY (\"erpID\")\n" +
                    "    REFERENCES \"erp\" (\"erpID\")\n" +
                    "      ON DELETE NO ACTION\n" +
                    "      ON UPDATE NO ACTION\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"manufacturingOrder\"\n" +
                    "  ADD CONSTRAINT \"Relationship42\"\n" +
                    "    FOREIGN KEY (\"erpID\")\n" +
                    "    REFERENCES \"erp\" (\"erpID\")\n" +
                    "      ON DELETE NO ACTION\n" +
                    "      ON UPDATE NO ACTION\n" +
                    ";\n" +
                    "\n" +
                    "ALTER TABLE \"RawMaterialOrder\"\n" +
                    "  ADD CONSTRAINT \"Relationship43\"\n" +
                    "    FOREIGN KEY (\"erpID\")\n" +
                    "    REFERENCES \"erp\" (\"erpID\")\n" +
                    "      ON DELETE NO ACTION\n" +
                    "      ON UPDATE NO ACTION\n" +
                    ";\n" +
                    "\n");
                /*st.execute("CREATE TABLE IF NOT EXISTS erp(erpID INT NOT NULL,currentTime INT NOT NULL,CONSTRAINT PK_erp PRIMARY KEY (erpID));");
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
*/
        } catch (Exception e) {
            System.out.println("Sql exception: Creating tables error");
        }
    }

    public void dropSQLtables() {
        try {
            st.execute("DROP SCHEMA infi CASCADE");
        } catch (Exception e) {
            System.out.println("Sql exception: Dropping tables error");
        }
    }

}
