package eu.buildserscoffee.web;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ssqdqds {

    public static void main(String args[]) {
        try {
            File file = new File("C:\\Users\\Arnaud\\Desktop\\test.html");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("nav");

            /*for (int itr = 0; itr < nodeList.getLength(); itr++) {
                Node node = nodeList.item(itr);
                System.out.println("\nNode Name :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {


                    Element eElement = (Element) node;
                    System.out.println("Student id: " + eElement.getElementsByTagName("id").item(0).getTextContent());
                    System.out.println("First Name: " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
                    System.out.println("Last Name: " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
                    System.out.println("Subject: " + eElement.getElementsByTagName("subject").item(0).getTextContent());
                    System.out.println("Marks: " + eElement.getElementsByTagName("marks").item(0).getTextContent());
                }
            }*/
            printNodes(null, nodeList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> lib = new HashMap<>();
    public static List<String> words = new ArrayList();

    public static void printNodes(String parentVar, NodeList nodeList){
        ArrayList<String> addTo = new ArrayList<>();
        for (int itr = 0; itr < nodeList.getLength(); itr++) {
            Node node = nodeList.item(itr);
            if(node.getNodeName().equals("#text") || node.getNodeName().equals("#comment") || node.getNodeName().equals("script"))
                continue;
            String name = lower(node.getNodeName());
            int i = 1;
            while (words.contains(name)){
                i = lib.get(lower(node.getNodeName()));
                i++;
                name = node.getNodeName().toLowerCase() + i;
            }

            words.add(name);
            lib.put(lower(node.getNodeName()), i);

            System.out.println(upper(node.getNodeName()) + " " + name + " = new " + upper(node.getNodeName()) + "();");
            printAttributes(name, node);
            printNodes(name, node.getChildNodes());

            addTo.add(name);
        }
        if(addTo.size() > 0)
            System.out.println(parentVar != null? parentVar + ".add(" + String.join(", ", addTo) + ");" : "add(" + String.join(", ", addTo) + ");");
        System.out.println("");
    }

    public static String upper(String string){
        if (string.equals("a"))
            string = "anchor";
        if (string.equals("button"))
            string = "nativeButton";
        if (string.equals("img"))
            string = "image";
        if (string.equals("ul"))
            string = "UnorderedList";
        if (string.equals("p"))
            string = "Paragraph";
        if (string.equals("li"))
            string = "ListItem";
        if (string.equals("i"))
            string = "Emphasis";
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String lower(String string){
        if (string.equals("a"))
            string = "anchor";
        if (string.equals("button"))
            string = "nativeButton";
        if (string.equals("img"))
            string = "image";
        if (string.equals("ul"))
            string = "UnorderedList";
        if (string.equals("p"))
            string = "Paragraph";
        if (string.equals("li"))
            string = "ListItem";
        if (string.equals("i"))
            string = "Emphasis";
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    public static void printAttributes(String name, Node node){
        for (int itr = 0; itr < node.getAttributes().getLength(); itr++) {
            Node attr = node.getAttributes().item(itr);

            if(attr.getNodeName().equals("class"))
                System.out.println(name + ".addClassNames(\"" + String.join("\", \"", attr.getNodeValue().split(" ")) + "\");");

            if(attr.getNodeName() != "class") {
                System.out.println(name + ".getElement().setAttribute(\""+ attr.getNodeName() +"\", \"" + attr.getNodeValue() + "\");");

                //System.out.println("------------------------------------------------------------------------------------------------------------->" + attr.getNodeName());

            }
        }
    }
}
