package comsProtocols;

public class shareResources {

    private long startTime;
    private String internalOrdersConcat;
    private String clientOrders;
    private String finishedOrdersInfo;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getInternalOrdersConcat() {
        return internalOrdersConcat;
    }

    public void setInternalOrdersConcat(String internalOrdersConcat) {
        this.internalOrdersConcat = internalOrdersConcat;
    }

    public String getClientOrders() {
        return clientOrders;
    }

    public void setClientOrders(String clientOrders) {
        this.clientOrders = clientOrders;
    }

    public void printInERP2MESbuffer(String str) {

        setInternalOrdersConcat(str);
        //System.out.println(getInternalOrdersConcat());
    }

    public String readInERP2MESbuffer() {

        return getInternalOrdersConcat();
    }

    public String getFinishedOrdersInfo() {
        return finishedOrdersInfo;
    }

    public void setFinishedOrdersInfo(String finishedOrdersInfo) {
        this.finishedOrdersInfo = finishedOrdersInfo;
    }
}
