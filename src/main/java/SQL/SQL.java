package SQL;

import java.sql.*;

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
    /*
    public void createSQLtables() {
        try {
            st.execute("CREATE TABLE IF NOT EXISTS erp(pk VARCHAR(20) NOT NULL,curr_time INT NOT NULL,manufacturing_orders VARCHAR(30) NOT NULL,receive_orders VARCHAR(20) NOT NULL,production_orders VARCHAR(20) NOT NULL,shipping_orders VARCHAR(20) NOT NULL,CONSTRAINT PK_erp PRIMARY KEY (pk));");

            st.execute("CREATE TABLE IF NOT EXISTS client_order(client_name VARCHAR(30) NOT NULL, order_num INT NOT NULL, piece_type INT NOT NULL, qty INT NOT NULL, delivery_date INT NOT NULL, pen_delay FLOAT NOT NULL, pen_advance FLOAT NOT NULL, CONSTRAINT PK_client_order PRIMARY KEY (client_name,order_num));");

            st.execute("CREATE TABLE IF NOT EXISTS internal_orders(order_id INT NOT NULL,start_date INT NOT NULL,CONSTRAINT PK_internal_orders PRIMARY KEY (order_id));");

            st.execute("CREATE TABLE IF NOT EXISTS receive_order() INHERITS (internal_orders);");

            st.execute("CREATE TABLE IF NOT EXISTS production_order() INHERITS (internal_orders);");

            st.execute("CREATE TABLE IF NOT EXISTS shipping_order() INHERITS (internal_orders);");

            st.execute("CREATE TABLE IF NOT EXISTS manufacturing_order(production_id INT NOT NULL,production_time INT NOT NULL,begin_date INT NOT NULL, end_date INT NOT NULL,client_order VARCHAR(30), raw_material_order VARCHAR(30) NOT NULL, CONSTRAINT PK_internal_orders PRIMARY KEY(production_id));");

            st.execute("CREATE TABLE IF NOT EXISTS raw_material_order(id INT NOT NULL, qty INT NOT NULL, reserved_qty INT NOT NULL, piece_type INT NOT NULL, time_to_place_order INT NOT NULL,arrival_time INT NOT NULL,supplier VARCHAR(30) NOT NULL, CONSTRAINT PK_raw_material_order PRIMARY KEY(id));");

            st.execute("CREATE TABLE IF NOT EXISTS supplier(name VARCHAR(30) NOT NULL, delivery_time INT NOT NULL, min_qty INT NOT NULL,raw_piece VARCHAR(20) NOT NULL, CONSTRAINT PK_supplier PRIMARY KEY (name));");

            st.execute("CREATE TABLE IF NOT EXISTS raw_piece( id INT NOT NULL, type INT NOT NULL, unit_cost INT NOT NULL, CONSTRAINT Pk_raw_piece PRIMARY KEY(id));");

            /* ********************
             *  ALTER tables created
             *  ********************
             *//*

            st.execute("ALTER TABLE erp ADD CONSTRAINT FK_erp2manufacturing_order FOREIGN KEY (manufacturing_orders) REFERENCES manufacturing_order (production_id) ON DELETE NO ACTION ON UPDATE NO ACTION;");

            st.execute("ALTER TABLE erp ADD CONSTRAINT FK_erp2receive_order FOREIGN KEY (receive_orders) REFERENCES receive_order (order_id) ON DELETE NO ACTION ON UPDATE NO ACTION;");
            st.execute("ALTER TABLE erp ADD CONSTRAINT FK_erp2production_order FOREIGN KEY (production_orders) REFERENCES production_order (order_id) ON DELETE NO ACTION ON UPDATE NO ACTION;");
            st.execute("ALTER TABLE erp ADD CONSTRAINT FK_erp2shipping_order FOREIGN KEY (shipping_orders) REFERENCES shipping_order (order_id) ON DELETE NO ACTION ON UPDATE NO ACTION;");

            st.execute("ALTER TABLE manufacturing_order ADD CONSTRAINT FK_manufacturing2client_order FOREIGN KEY (client_order) REFERENCES client_order (client_name,order_num) ON DELETE NO ACTION ON UPDATE NO ACTION;");
            st.execute("ALTER TABLE manufacturing_order ADD CONSTRAINT FK_manufacturing_order2raw_material_order FOREIGN KEY (raw_material_order) REFERENCES raw_material_order (id) ON DELETE NO ACTION ON UPDATE NO ACTION;");

            st.execute("ALTER TABLE raw_material_order ADD CONSTRAINT FK_raw_material_order2supplier FOREIGN KEY(client_order) REFERENCES supplier (name) ON DELETE NO ACTION ON UPDATE NO ACTION;");

            st.execute("ALTER TABLE supplier ADD CONSTRAINT FK_supplier2raw_piece FOREIGN KEY(raw_piece) REFERENCES raw_piece (id) ON DELETE NO ACTION ON UPDATE NO ACTION;");

        } catch (Exception e) {
            System.out.println("Sql exception: Creating tables error");
        }
    }*/


}
