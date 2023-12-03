/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.utils;

import java.io.StringReader;
import static java.lang.System.exit;
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
    //private Map<String, String> detailAttributes = new HashMap<String, String>();
    //private Map<String, String> relationAttributes = new HashMap<String, String>();
    //private Map<String, String> connections = new HashMap<String, String>();

    public Record(Map<String, String> attributesT) {
        this.attributes = attributesT;
    }

    public Record(Element recordElement) {
        getRecord(recordElement);
    }

    public void getRecord(Element recordElement) {

        try {
            NodeList nodeList = recordElement.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String nodeName = element.getNodeName();
                    String nodeValue = element.getTextContent();
                    nodeName = nodeName.replace(" ", "_").strip().stripLeading().stripTrailing().trim();
                    if (nodeName.contains("detail")) {
                        findDetail(element,nodeName,nodeValue);
                    } 
                    else if (nodeName.contains("relationship")) {
                        findRelation(element,nodeName,nodeValue);
                    } 
                  
                    attributes.put(nodeName, nodeValue);
                    
                   
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void findRelation(Element relationElement, String nodeName, String nodeValue) {
        attributes.put("relationshipTermID", relationElement.getAttribute("termID"));
        attributes.put("relationshipRelatedRecordID", relationElement.getAttribute("relatedRecordID"));
        attributes.put("relationshipTerm", relationElement.getAttribute("term"));
        attributes.put("relationshipInverse", relationElement.getAttribute("inverse"));
        attributes.put("relationshipInvTermID", relationElement.getAttribute("invTermID"));
        attributes.put("relationshipInvTermConceptID", relationElement.getAttribute("invTermConceptID"));
        attributes.put(nodeValue, nodeValue);
    }

    public void findDetail(Element detailElementt, String nodeName, String nodeValue) {
        attributes.put("detailId", detailElementt.getAttribute("id"));
        attributes.put("detailConceptID", detailElementt.getAttribute("conceptID"));
        attributes.put("detailName", detailElementt.getAttribute("name"));
        attributes.put("detailBasename", detailElementt.getAttribute("basename"));
        attributes.put(nodeName, nodeValue);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    /*public Map<String, String> getDetailAttributes() {
        return detailAttributes;
    }

    public Map<String, String> getRelationAttributes() {
        return relationAttributes;
    }*/

   
    

}
