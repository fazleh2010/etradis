/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.utils;

import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.InputSource;

public class XMLReaderExample {
    
    
    public static void mainT(String[] args) {
        String xmlString = "<record visibility=\"viewable\" visnote=\"logged in users\" selected=\"no\" depth=\"0\">\n" +
                "    <!-- other XML elements here -->\n" +
                "    <id>2533</id>\n" +
                "    <type id=\"80\" conceptID=\"3-124\">Magazine</type>\n" +
                "    <citeAs>https://heurist.sfb1288.uni-bielefeld.de/heurist/?recID=2533&amp;db=ipaetzold_erste_datenbank</citeAs>\n" +
                "    <title>Schwuchtel - Eine Zeitung der Schwulenbewegung</title>\n" +
                "    <added>2023-05-01 15:54:06</added>\n" +
                "    <modified>2023-05-01 15:55:00</modified>\n" +
                "    <workgroup id=\"0\">public</workgroup>\n" +
                "    <detail conceptID=\"2-1\" name=\"Title of magazine\" id=\"1\" basename=\"Name or Title (please rename appropriately)\">Schwuchtel - Eine Zeitung der Schwulenbewegung</detail>\n" +
                "    <detail conceptID=\"2-134\" name=\"Location (place)\" id=\"134\" basename=\"Location (place)\" isRecordPointer=\"true\">2263</detail>\n" +
                "    <detail conceptID=\"3-1017\" name=\"Publisher &gt;\" id=\"1001\" basename=\"Publisher\" isRecordPointer=\"true\">2414</detail>\n" +
                "    <detail conceptID=\"2-36\" id=\"36\" name=\"Original ID\" basename=\"Original ID\">0-2533</detail>\n" +
                "</record>";
        Map<String,String>attributes=new HashMap<String,String>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));

            Element recordElement = document.getDocumentElement();
            NodeList nodeList = recordElement.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Map<String,String>detailAttributes=new HashMap<String,String>();
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String nodeName = element.getNodeName();
                    String nodeValue = element.getTextContent();
                    if(nodeName.contains("detail")){
                        detailAttributes=findDetail(element);
                    }
                    else
                    attributes.put(nodeName, nodeValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        for (String key:attributes.keySet()) {
             String value=attributes.get(key);
             System.out.println(key+" "+value);

        }
    }
    
    public static Map<String,String> findDetail(Element detailElement) {
       // String xmlString = "<detail conceptID=\"2-1\" name=\"Title of magazine\" id=\"1\" basename=\"Name or Title (please rename appropriately)\">Schwuchtel - Eine Zeitung der Schwulenbewegung</detail>";
        Map<String,String>attributes=new HashMap<String,String>();
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
            String srcDir="src/main/resources/";
            String testXML=srcDir+"IHB_single.xml";
            String originalXML=srcDir+"pseudonymized_IHB_data.xml";
        try {
            File xmlFile = new File(originalXML);
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            Element rootElement = document.getDocumentElement();
            NodeList recordList = rootElement.getElementsByTagName("record");
            for (int i = 0; i < recordList.getLength(); i++) {
                Element recordElement = (Element) recordList.item(i);
                String recordId = recordElement.getElementsByTagName("id").item(0).getTextContent();
                Record record=new Record(recordElement);
                //System.out.println("attribute::"+record.getAttributes());
                System.out.println("attribute::"+record.getAttributes().get("type"));
                //System.out.println("detail attribute::"+record.getDetailAttributes());
                //String uri="<http://localhost:9999/etradis#record_" + recordId + ">";
                //String triple = uri+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Person>" + " . " + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                
                
               NodeList detailList = recordElement.getChildNodes();*/
