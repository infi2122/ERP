package Readers;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import Models.clientOrder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;

public class xmlReader {

    public ArrayList<clientOrder> readOrder() {
        try {   //System.out.println(new File(".").getAbsolutePath());
            //creating a constructor of file class and parsing an XML file
            ArrayList<clientOrder> orders = new ArrayList<>();
            File file = new File("src/main/resources/File.xml");
            if (!file.exists()){
                System.out.println("Cannot Read!");
            }
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            //System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

            NodeList nodeList = doc.getElementsByTagName("clientOrder");
            // nodeList is not iterable, so we are using for loop
            for (int itr = 0; itr < nodeList.getLength(); itr++) {
                Node node = nodeList.item(itr);
                //System.out.println("\nNode Name :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    clientOrder order = new clientOrder(
                            eElement.getElementsByTagName("ClientName").item(0).getTextContent(),
                            Integer.parseInt(eElement.getElementsByTagName("OrderNumber").item(0).getTextContent()),
                            Integer.parseInt(eElement.getElementsByTagName("WorkPiece").item(0).getTextContent()),
                            Integer.parseInt(eElement.getElementsByTagName("Quantity").item(0).getTextContent()),
                            Integer.parseInt(eElement.getElementsByTagName("DueDate").item(0).getTextContent()),
                            Float.parseFloat(eElement.getElementsByTagName("latePen").item(0).getTextContent()),
                            Float.parseFloat(eElement.getElementsByTagName("earlyPen").item(0).getTextContent())
                    );
                    orders.add(order);
                    /*
                    System.out.println("Student id: "+ eElement.getElementsByTagName("id").item(0).getTextContent());
                    System.out.println("First Name: "+ eElement.getElementsByTagName("firstname").item(0).getTextContent());
                    System.out.println("Last Name: "+ eElement.getElementsByTagName("lastname").item(0).getTextContent());
                    System.out.println("Subject: "+ eElement.getElementsByTagName("subject").item(0).getTextContent());
                    System.out.println("Marks: "+ eElement.getElementsByTagName("marks").item(0).getTextContent());
                    */

                }
            }
            return orders;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}  