package Readers;

import Models.clientOrder;
import Models.supplier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class xmlSuppliersReader {

    public ArrayList<supplier> readSuppliers() {
        try {   //System.out.println(new File(".").getAbsolutePath());
            //creating a constructor of file class and parsing an XML file

            ArrayList<supplier> suppliersList = new ArrayList<>();
            File file = new File("src/main/resources/suppliersList.xml");
            if (!file.exists()) {
                System.out.println("Cannot Read!");
            }
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            //System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

            NodeList nodeList = doc.getElementsByTagName("suplier");
            // nodeList is not iterable, so we are using for loop
            for (int itr = 0; itr < nodeList.getLength(); itr++) {
                Node node = nodeList.item(itr);
                //System.out.println("\nNode Name :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    supplier supplier = new supplier();

                    supplier.setName(eElement.getAttribute("Name"));
                    supplier.setDeliveryTime(Integer.parseInt(eElement.getAttribute("DeliveryTime")));
                    supplier.setMinQty(Integer.parseInt(eElement.getAttribute("MinQty")));

                    suppliersList.add(supplier);
                }
            }
            return suppliersList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}  