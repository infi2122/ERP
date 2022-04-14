package Readers;

import Models.supplier;
import Models.transformations;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class xmlTransformationsReader {

    public ArrayList<transformations> readTransformations() {
        try {   //System.out.println(new File(".").getAbsolutePath());
            //creating a constructor of file class and parsing an XML file

            ArrayList<transformations> transformationsVect = new ArrayList<>();
            File file = new File("src/main/resources/transformations.xml");
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

            NodeList nodeList = doc.getElementsByTagName("transformation");
            // nodeList is not iterable, so we are using for loop
            for (int itr = 0; itr < nodeList.getLength(); itr++) {
                Node node = nodeList.item(itr);
                //System.out.println("\nNode Name :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    transformations tranformation = new transformations();

                    tranformation.setInit(Integer.parseInt(eElement.getAttribute("Init")));
                    tranformation.setFinal(Integer.parseInt(eElement.getAttribute("Final")));
                    tranformation.setTool(Integer.parseInt(eElement.getAttribute("Tool")));
                    tranformation.setDuration(Integer.parseInt(eElement.getAttribute("Duration")));

                    transformationsVect.add(tranformation);
                }
            }
            return transformationsVect;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}  