package UDP;

public class ERP2MES {

    private String internalOrdersConcat;

    public String getInternalOrdersConcat() {
        return internalOrdersConcat;
    }

    public void setInternalOrdersConcat(String internalOrdersConcat) {
        this.internalOrdersConcat = internalOrdersConcat;
    }

    public void printInERP2MESbuffer(String str) {

        setInternalOrdersConcat(str);
        System.out.println(getInternalOrdersConcat());
    }

    public String readInERP2MESbuffer() {

        return getInternalOrdersConcat();
    }


}
