package comsProtocols;

public class ERP_to_MES {

    private static tcpClient client;

    public static tcpClient getClient() {
        return client;
    }

    public static void setClient(tcpClient client) {
        ERP_to_MES.client = client;
    }

    public void openConnection(int port, String ip) {
        setClient(new tcpClient());
        getClient().startConnection(ip, port);
    }

    public void stopConnection() {
        getClient().stopConnection();
    }

    public String receivingOrdersStats() {

        String response = new String();
        try {
            response = getClient().sendMessage("orderStats");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;

    }


}
