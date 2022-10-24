/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.utils;

import de.citec.etradis.core.FileFolderUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QueryType;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author elahi
 */
public class SparqlQueryTest {
    private static String endpoint = "https://dbpedia.org/sparql/";
    private String objectOfProperty;
    public static String FIND_ANY_ANSWER = "FIND_ANY_ANSWER";
    private String sparqlQuery = null;
    public static String QUESTION_MARK = "?";
    private String resultSparql = null;
    private String command = null;
    private String type = null;
    private Set<String> resultList=new HashSet<String>();
    private List<String> bindingList=new ArrayList<String>();
     private String language="en";
    
    public SparqlQueryTest(String entityUrl,String property,String language) {
        String sparqlQuery=this.answerObjectProperty(property, entityUrl, language);
        String resultSparql = executeSparqlQuery(sparqlQuery);
        this.resultList=this.parseResult(resultSparql);

    }
    
    public String executeSparqlQuery(String query) {
        String result = null, resultUnicode = null;
        Process process = null;
        try {
            resultUnicode = this.stringToUrlUnicode(query);
            this.command = "curl " + endpoint + "?query=" + resultUnicode;
            process = Runtime.getRuntime().exec(command);
        } catch (Exception ex) {
            Logger.getLogger(SparqlQueryTest.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("error in unicode in sparql query!" + ex.getMessage());
            ex.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            result = builder.toString();
        } catch (IOException ex) {
            Logger.getLogger(SparqlQueryTest.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("error in reading sparql query!" + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    public Set<String> parseResult(String xmlStr) {
        Document doc = convertStringToXMLDocument(xmlStr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            return this.parseResult(builder, xmlStr);
        } catch (Exception ex) {
            Logger.getLogger(SparqlQueryTest.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("error in parsing sparql in XML!" + ex.getMessage());
            ex.printStackTrace();
        }
        return new TreeSet<String>();
    }

    private Document convertStringToXMLDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Set<String> parseResult(DocumentBuilder builder, String xmlStr) {
        Set<String>resultList=new TreeSet<String>();
        try {
            Document document = builder.parse(new InputSource(new StringReader(
                    xmlStr)));
            NodeList results = document.getElementsByTagName("results");
            for (int i = 0; i < results.getLength(); i++) {
                NodeList childList = results.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("result".equals(childNode.getNodeName())) {
                        String answer= childList.item(j).getTextContent().trim();
                          String objectOfProperty = childList.item(j).getTextContent().trim();
                          resultList.add(objectOfProperty);
                    }
                }

            }
           
        } catch (SAXException ex) {
            Logger.getLogger(SparqlQueryTest.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("no result after sparql query!" + ex.getMessage());
            return new TreeSet<String>();
        } catch (IOException ex) {
            Logger.getLogger(SparqlQueryTest.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("no result after sparql query!" + ex.getMessage());
            return new TreeSet<String>();
        }
        return resultList;

      
    }
    
    public String setSubjOfProperty( String property, String entityUrl,String language) {
        return "select  ?s\n"
                + "    {\n"
                + "   " + "?s" + " " + "<" + property + ">" + "  " + "<" + entityUrl + ">" + "\n"
                + "    }";

    }

    public String answerObjectProperty( String property, String entityUrl, String language) {
        return "select  ?s\n"
                + "    {\n"
                + "   " + "<" + entityUrl + ">" + " " + "<" + property + ">" + "  " + "?s" + "\n"
                + "    }";

    }


    public String stringToUrlUnicode(String string) throws UnsupportedEncodingException {
        String encodedString = URLEncoder.encode(string, "UTF-8");
        return encodedString;
    }

    private void parseResultBindingList(String xmlStr) {
         Document doc = convertStringToXMLDocument(xmlStr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            this.parseResult(builder, xmlStr);
        } catch (Exception ex) {
            Logger.getLogger(SparqlQueryTest.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("error in parsing sparql in XML!" + ex.getMessage());
            ex.printStackTrace();
        }

        try {
            Document document = builder.parse(new InputSource(new StringReader(
                    xmlStr)));
            NodeList results = document.getElementsByTagName("results");
            for (int i = 0; i < results.getLength(); i++) {
                NodeList childList = results.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("result".equals(childNode.getNodeName())) {
                        String[] lines = childList.item(j).getTextContent().strip().trim().split(System.getProperty("line.separator"));
                        Integer index=0;
                        String http="",label="";
                        Map<String,String> map=new TreeMap<String,String>();
                        for(String line:lines) {
                            
                           
                            if(index==0){
                              http=line.strip().trim();    
                            }
                            else if(endpoint.contains("wikidata")&&index==3){
                              label=line.strip().trim();  
                            }
                            else if(endpoint.contains("beniculturali")&&index==1){
                              label=line.strip().trim();  
                            }
                            
                            index=index+1;
                        }
                       map.put(http,label);
                       String binding=label+"+"+http;
                       this.bindingList.add(binding);
                    }
                   
                }

            }
        } catch (SAXException ex) {
            Logger.getLogger(SparqlQueryTest.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("no result after sparql query!" + ex.getMessage());
            return ;
        } catch (IOException ex) {
            Logger.getLogger(SparqlQueryTest.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("no result after sparql query!" + ex.getMessage());
            return ;
        }
    }
   
    public String getObject() {
        return this.objectOfProperty;
    }

    public String getSparqlQuery() {
        return sparqlQuery;
    }

    public String getResultSparql() {
        return resultSparql;
    }

    @Override
    public String toString() {
        return "SparqlQuery{" + "objectOfProperty=" + objectOfProperty + ", sparqlQuery=" + sparqlQuery + '}';
    }

    
    public static void setEndpoint(String endpointT) {
        endpoint=endpointT;
    }

    public Set<String> getResultList() {
        return resultList;
    }
 
    public static void main(String[] args) throws IOException {
        //String entityUrl = "http://dbpedia.org/ontology/Work";
        String property = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
        String language = "en";
        Set<String> entityList = new TreeSet<String>();
        String dir = "/media/elahi/My Passport/general/interface/question-grammar-generator/";
        String fileName = dir + "src/main/resources/className.txt";
        entityList = FileFolderUtils.fileToSet(fileName);
        for (String entityUrl : entityList) {
            entityUrl = "http://dbpedia.org/ontology/" + entityUrl;
            SparqlQueryTest sparqlQueryTest = new SparqlQueryTest(entityUrl, property, language);
             System.out.println(entityUrl);
            System.out.println(sparqlQueryTest.getResultList());
        }
        /*String testEntity = "http://dbpedia.org/ontology/Organisation";
        SparqlQueryTest sparqlQueryTest = new SparqlQueryTest(testEntity, property, language);
        System.out.println(testEntity);
        System.out.println(sparqlQueryTest.resultList);*/

    }

}
