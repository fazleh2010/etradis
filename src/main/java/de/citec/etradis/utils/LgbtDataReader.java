/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.utils;

import java.io.File;
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import static java.lang.System.exit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.arnx.jsonic.util.ClassUtil.findClass;
import org.xml.sax.InputSource;

public class LgbtDataReader {
    private String content="";
    private File typeFile=null;
    
    public LgbtDataReader(String srcDir, String originalXML) {
        this.typeFile = new File(srcDir + "type.txt");
        this.content=prepareTriple(originalXML);
    }
    
    private String prepareTriple(String originalXML) {
        String content="";
         try {
            File xmlFile = new File(originalXML);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            Element rootElement = document.getDocumentElement();
            NodeList recordList = rootElement.getElementsByTagName("record");
            String stringTypes = findTypes(recordList);
            de.citec.etradis.core.FileFolderUtils.stringToFile(stringTypes, typeFile);
            for (int i = 0; i < recordList.getLength(); i++) {
                Element recordElement = (Element) recordList.item(i);
                String recordId = recordElement.getElementsByTagName("id").item(0).getTextContent();
                Record record = new Record(recordElement);
                //System.out.println("attribute::"+record.getAttributes());
                //System.out.println("detail attribute::"+record.getDetailAttributes());
                String uri = "<http://localhost:9999/etradis#record" + recordId + ">";
                String className = findEtradisClass(record.getAttributes().get("type"));
                String mainTriple = getBasicTriple(uri, className);
                String triple = mainTriple + getProperties(uri, record.getAttributes());
                //relationship
                content += triple;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
         return content;
    }

    public String getContent() {
        return content;
    }

    public File getTypeFile() {
        return typeFile;
    }

   
    public static Map<String, String> findDetail(Element detailElement) {
        // String xmlString = "<detail conceptID=\"2-1\" name=\"Title of magazine\" id=\"1\" basename=\"Name or Title (please rename appropriately)\">Schwuchtel - Eine Zeitung der Schwulenbewegung</detail>";
        Map<String, String> attributes = new HashMap<String, String>();
        try {
            String conceptID = detailElement.getAttribute("conceptID");
            String name = detailElement.getAttribute("name");
            String id = detailElement.getAttribute("id");
            String basename = detailElement.getAttribute("basename");
            String textContent = detailElement.getTextContent();

            /*System.out.println("conceptID: " + conceptID);
            System.out.println("name: " + name);
            System.out.println("id: " + id);
            System.out.println("basename: " + basename);
            System.out.println("textContent: " + textContent);*/
            attributes.put("conceptID", id);
            attributes.put("name", name);
            attributes.put("basename", basename);
            attributes.put("textContent", textContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attributes;
    }

    public static void main(String[] args) {
        String srcDir = "src/main/resources/";
        String testXML = srcDir + "IHB_single.xml";
        String originalXML = srcDir + "pseudonymized_IHB_data.xml";
        File typeFile = new File(srcDir + "type.txt");
        String str = "";

        try {
            File xmlFile = new File(originalXML);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            Element rootElement = document.getDocumentElement();
            NodeList recordList = rootElement.getElementsByTagName("record");
            String content = findTypes(recordList);
            de.citec.etradis.core.FileFolderUtils.stringToFile(content, typeFile);
            for (int i = 0; i < recordList.getLength(); i++) {
                Element recordElement = (Element) recordList.item(i);
                String recordId = recordElement.getElementsByTagName("id").item(0).getTextContent();
                Record record = new Record(recordElement);
                String type = record.getAttributes().get("type");
                //System.out.println("attribute::"+record.getAttributes());
                //System.out.println("detail attribute::"+record.getDetailAttributes());
                String uri = "<http://localhost:9999/etradis#record" + recordId + ">";
                String className = findEtradisClass(type);
                String mainTriple = getBasicTriple(uri, className);
                String triple = mainTriple + getProperties(uri, record.getAttributes());
                str += triple;
                //System.out.println(triple);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(str);
    }

    private static String getBasicTriple(String uri, String className) {
        return uri + " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/" + className + ">" + " . " + "\n";
    }

    private static String getProperties(String uri, Map<String, String> attibutes) {
        String content = "";
        String object = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#langString>";
        for (String attibute : attibutes.keySet()) {
            String value = attibutes.get(attibute);
            value = value.replace("\n", " ");
            if (value.contains("http")) {
                value = value.replace("https", "http");
                content += uri + " <http://localhost:9999/" + attibute + "> " + "<" + value + ">" + " . " + "\n";
            } else {
                if (attibute.contains("title")) {
                    content += uri + " "+"<"+"http://www.w3.org/2000/01/rdf-schema#label"+">"+" " + "\"" + value + "\"^^" + object + " . " + "\n";
                    content += uri + " "+"<"+"http://localhost:9999/" + attibute + "> " + "\"" + value + "\"^^" + object + " . " + "\n";
                } else {
                    content += uri + " "+"<"+"http://localhost:9999/" + attibute + "> " + "\"" + value + "\"^^" + object + " . " + "\n";
                }
            }
        }

        return content;

    }

    public static String findEtradisClass(String className) {
        className = className.toLowerCase();
        String mapped_class = "Miscellaneous";

        if (className.contains("event")) {
            mapped_class = "Event";
        } else if (className.contains("organisation") || className.contains("person")) {
            mapped_class = "Agent";
        } else if (className.contains("place")) {
            mapped_class = "Place";
        } else if (className.contains("topic")) {
            mapped_class = "TopicalConcept";
        } else if (className.contains("magazine") || className.contains("document")) {
            mapped_class = "CulturalArtifact";
        } else {
            mapped_class = "Miscellaneous";
        }

        return mapped_class;
    }

    private static String findTypes(NodeList recordList) {
        Set<String> types = new TreeSet<String>();
        for (int i = 0; i < recordList.getLength(); i++) {
            Element recordElement = (Element) recordList.item(i);
            String recordId = recordElement.getElementsByTagName("id").item(0).getTextContent();
            Record record = new Record(recordElement);
            //System.out.println("attribute::"+record.getAttributes());
            System.out.println(record.getAttributes().get("type"));
            String nodeName = record.getAttributes().get("type");
            types.add(nodeName);
            //System.out.println("detail attribute::"+record.getDetailAttributes());
            //String uri="<http://localhost:9999/etradis#record_" + recordId + ">";
            //String triple = uri+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Person>" + " . " + "\n";
        }
        String str = "";
        for (String type : types) {
            str += type + "\n";
        }
        return str;
    }
    
     public static void mainT(String[] args) {
        String xmlString = "<record visibility=\"viewable\" visnote=\"logged in users\" selected=\"no\" depth=\"0\">\n"
                + "    <!-- other XML elements here -->\n"
                + "    <id>2533</id>\n"
                + "    <type id=\"80\" conceptID=\"3-124\">Magazine</type>\n"
                + "    <citeAs>https://heurist.sfb1288.uni-bielefeld.de/heurist/?recID=2533&amp;db=ipaetzold_erste_datenbank</citeAs>\n"
                + "    <title>Schwuchtel - Eine Zeitung der Schwulenbewegung</title>\n"
                + "    <added>2023-05-01 15:54:06</added>\n"
                + "    <modified>2023-05-01 15:55:00</modified>\n"
                + "    <workgroup id=\"0\">public</workgroup>\n"
                + "    <detail conceptID=\"2-1\" name=\"Title of magazine\" id=\"1\" basename=\"Name or Title (please rename appropriately)\">Schwuchtel - Eine Zeitung der Schwulenbewegung</detail>\n"
                + "    <detail conceptID=\"2-134\" name=\"Location (place)\" id=\"134\" basename=\"Location (place)\" isRecordPointer=\"true\">2263</detail>\n"
                + "    <detail conceptID=\"3-1017\" name=\"Publisher &gt;\" id=\"1001\" basename=\"Publisher\" isRecordPointer=\"true\">2414</detail>\n"
                + "    <detail conceptID=\"2-36\" id=\"36\" name=\"Original ID\" basename=\"Original ID\">0-2533</detail>\n"
                + "</record>";
        Map<String, String> attributes = new HashMap<String, String>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));

            Element recordElement = document.getDocumentElement();
            NodeList nodeList = recordElement.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Map<String, String> detailAttributes = new HashMap<String, String>();
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String nodeName = element.getNodeName();
                    String nodeValue = element.getTextContent();
                    if (nodeName.contains("detail")) {
                        detailAttributes = findDetail(element);
                    } else {
                        attributes.put(nodeName, nodeValue);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String key : attributes.keySet()) {
            String value = attributes.get(key);
            System.out.println(key + " " + value);

        }
    }

    


}


/*// Extract information from the <record> element
                String recordId = recordElement.getElementsByTagName("id").item(0).getTextContent();
                String recordType = recordElement.getElementsByTagName("type").item(0).getTextContent();
                String recordTitle = recordElement.getElementsByTagName("title").item(0).getTextContent();
                String recordCiteAs = recordElement.getElementsByTagName("citeAs").item(0).getTextContent();
                 // Print information about the record
                System.out.println("Record ID: " + recordId);
                System.out.println("Record Type: " + recordType);
                System.out.println("Record Title: " + recordTitle);
                System.out.println("Record CiteAs: " + recordCiteAs);
                System.out.println("---------------------------------------");
                
                                //String triple = uri+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/"+className+">" + " . " + "\n";

               NodeList detailList = recordElement.getChildNodes();*/
