/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.utils;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author elahi
 */
public class Record {

    private Map<String, String> attributes = new HashMap<String, String>();
    private Map<String, String> detailAttributes = new HashMap<String, String>();

    public Record(Map<String, String> attributesT, Map<String, String> detailAttributesT) {
        this.attributes = attributesT;
        this.detailAttributes = detailAttributesT;
    }

    public Record(Element recordElement) {
        getRecord(recordElement);
    }
   

    public void getRecord(Element recordElement) {
        /*String xmlString = "<record visibility=\"viewable\" visnote=\"logged in users\" selected=\"no\" depth=\"0\">\n"
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
        Map<String, String> attributes = new HashMap<String, String>();*/

        try {
            /*DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            Element recordElement = document.getDocumentElement();*/
            
            NodeList nodeList = recordElement.getChildNodes();
            //NodeList detailList = recordElement.getElementsByTagName("detail");


            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String nodeName = element.getNodeName();
                    String nodeValue = element.getTextContent();
                    nodeName=nodeName.replace(" ", "_").strip().stripLeading().stripTrailing().trim();
                    //NodeList detailNodeList =element.getElementsByTagName("detail");
                    if (nodeName.contains("detail")) {
                        detailAttributes = findDetail(element);
                    } else {
                        //System.out.println(nodeName+":"+nodeValue);
                        attributes.put(nodeName, nodeValue);
                    }
                }
            }
            //findDetail(detailList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*for (String key : attributes.keySet()) {
            String value = attributes.get(key);
            System.out.println(key + " " + value);

        }*/
    }

    public Map<String, String> findDetail(Element detailElementt) {
        Map<String, String> detailAttributes=new HashMap<String, String>();
        // String xmlString = "<detail conceptID=\"2-1\" name=\"Title of magazine\" id=\"1\" basename=\"Name or Title (please rename appropriately)\">Schwuchtel - Eine Zeitung der Schwulenbewegung</detail>";
        //NodeList nodeList = detailElement.getChildNodes();
        /*for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String nodeName = element.getNodeName();
                String nodeValue = element.getTextContent();
                nodeName = nodeName.replace(" ", "_").strip().stripLeading().stripTrailing().trim();
                System.out.println("detail_"+nodeName+":"+nodeValue);
                detailAttributes.put(nodeName, nodeValue);
            }
        }*/

        try {
            String conceptID = detailElementt.getAttribute("conceptID");
            String name = detailElementt.getAttribute("name");
            String id = detailElementt.getAttribute("id");
            String basename = detailElementt.getAttribute("basename");
            String textContent = detailElementt.getTextContent();
            detailAttributes.put("conceptID", conceptID);
            detailAttributes.put("name", name);
            detailAttributes.put("basename", basename);
            detailAttributes.put("textContent", textContent);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return detailAttributes;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public Map<String, String> getDetailAttributes() {
        return detailAttributes;
    }

}
