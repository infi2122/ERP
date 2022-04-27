package Readers;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import Models.clientOrder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.StringReader;
import org.xml.sax.InputSource;

import java.io.File;
import java.util.ArrayList;

public class xmlReader {

    public ArrayList<clientOrder> readOrder(String xml) {

        try {

            //creating a constructor of file class and parsing an XML file
            if(xml == null){
                return null;
            }

            Document doc = convertStringToXMLDocument(xml);
            ArrayList<clientOrder> orders = new ArrayList<>();

            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();

            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            String clientName = new String();
            // nodeList is not iterable, so we are using for loop
            for (int itr = 0; itr < nodeList.getLength(); itr++) {

                Node node = nodeList.item(itr);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;

                    if (node.getNodeName().equals("Client")) {
                        clientName = ((Element) node).getAttribute("NameId");
                    } else if (node.getNodeName().equals("Order")) {

                        clientOrder order = new clientOrder(
                                clientName,
                                Integer.parseInt(((Element) node).getAttribute("Number")),
                                Integer.parseInt(String.valueOf(((Element) node).getAttribute("WorkPiece").charAt(1))),
                                Integer.parseInt(((Element) node).getAttribute("Quantity")),
                                Integer.parseInt(((Element) node).getAttribute("DueDate")),
                                Float.parseFloat(((Element) node).getAttribute("LatePen")),
                                Float.parseFloat(((Element) node).getAttribute("EarlyPen"))

                        );
                        orders.add(order);
                    }
                }
            }

            return orders;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Document convertStringToXMLDocument(String xmlString)
    {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString.replaceAll("\n|\r",""))));
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

}  