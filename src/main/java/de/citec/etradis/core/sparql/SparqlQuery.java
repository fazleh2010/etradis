/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core.sparql;

import de.citec.etradis.core.DBPediaHandler.DBpediaHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import static java.lang.System.exit;
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

/**
 *
 * @author elahi
 */
public class SparqlQuery {

    private static String endpoint = "https://dbpedia.org/sparql";
    private String objectOfProperty;
    public static String FIND_ANY_ANSWER = "FIND_ANY_ANSWER";
    public static String FIND_LABEL = "FIND_LABEL";
    private String sparqlQuery = null;
    public static String RETURN_TYPE_OBJECT = "objOfProp";
    public static String RETURN_TYPE_SUBJECT = "subjOfProp";
    public static String VARIABLE = "VARIABLE";
    public static String QUESTION_MARK = "?";
    private String resultSparql = null;
    private String command = null;
    private List<String> uris=new ArrayList<String>();

    private String type = null;
    private List<Binding> bindingList = new ArrayList<Binding>();
    private Boolean online = false;

    public SparqlQuery(String sparqlQuery) {
        endpoint = "https://dbpedia.org/sparql";
        this.resultSparql = executeSparqlQuery(sparqlQuery);
        this.parseResultBindingList(resultSparql);
    }

    public String executeSparqlQuery(String query) {
        String result = null, resultUnicode = null;
        Process process = null;
        try {
            resultUnicode = this.stringToUrlUnicode(query);
            this.command = "curl " + endpoint + "?query=" + resultUnicode;
            process = Runtime.getRuntime().exec(command);
        } catch (Exception ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("error in reading sparql query!" + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    public void parseResult(String xmlStr) {
        Document doc = convertStringToXMLDocument(xmlStr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            this.parseResult(builder, xmlStr);
        } catch (Exception ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("error in parsing sparql in XML!" + ex.getMessage());
            ex.printStackTrace();
        }
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

    private void parseResult(DocumentBuilder builder, String xmlStr) {
       uris=new ArrayList<String>();
        try {
            Document document = builder.parse(new InputSource(new StringReader(
                    xmlStr)));
            NodeList results = document.getElementsByTagName("results");
            for (int i = 0; i < results.getLength(); i++) {
                NodeList childList = results.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("result".equals(childNode.getNodeName())) {
                        String answer = childList.item(j).getTextContent().trim();
                        //if (endpoint.contains("dbpedia") && type.contains(FIND_ANY_ANSWER) && answer.contains("--")) {
                        //    continue;
                        //} else {
                            this.objectOfProperty = childList.item(j).getTextContent().trim();
                            uris.add(this.objectOfProperty);
                        //}
                    }
                }

            }

        } catch (SAXException ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("no result after sparql query!" + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("no result after sparql query!" + ex.getMessage());
        }
        
    }

    public String setSubjectLabelWikipedia(String entityUrl, String property, String language) {
        String sparql = null;
        if (isEntity(entityUrl)) {
            sparql = PrepareSparqlQuery.setLabelWikipedia(entityUrl, language);
            String resultSparql = executeSparqlQuery(sparql);
            this.parseResult(resultSparql);
            entityUrl = this.objectOfProperty;
            sparql = "select  ?s\n"
                    + "    {\n"
                    + "   " + "?s" + " " + "<" + property + ">" + "  " + "'" + entityUrl + "'" + "\n"
                    + "    }";
        } else {
            sparql = "select  ?s\n"
                    + "    {\n"
                    + "   " + "?s" + " " + "<" + property + ">" + "  " + entityUrl + "\n"
                    + "    }";
        }
        return sparql;
    }

    public String stringToUrlUnicode(String string) throws UnsupportedEncodingException {
        String encodedString = URLEncoder.encode(string, "UTF-8");
        return encodedString;
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

    public SparqlQuery() {

    }

    public static void setEndpoint(String endpointT) {
        endpoint = endpointT;
    }

    public List<Binding> getBindingList() {
        return bindingList;
    }

    private void parseResultBindingList(String xmlStr) {
        Document doc = convertStringToXMLDocument(xmlStr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            this.parseResult(builder, xmlStr);
        } catch (Exception ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
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
                        Integer index = 0;
                        String http = "", label = "";
                        Map<String, String> map = new TreeMap<String, String>();
                        for (String line : lines) {

                            if (index == 0) {
                                http = line.strip().trim();
                            } else if (endpoint.contains("wikidata") && index == 3) {
                                label = line.strip().trim();
                            } else if (endpoint.contains("beniculturali") && index == 1) {
                                label = line.strip().trim();
                            }

                            index = index + 1;
                        }
                        map.put(http, label);
                        Binding binding = new Binding(label, http);
                        this.bindingList.add(binding);
                    }

                }

            }
        } catch (SAXException ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("no result after sparql query!" + ex.getMessage());
            return;
        } catch (IOException ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("no result after sparql query!" + ex.getMessage());
            return;
        }
    }

    private String modifyBindingListSparql(String sparqlQuery) {
        String[] lines = sparqlQuery.strip().trim().split(System.getProperty("line.separator"));
        String str = "";
        for (String line : lines) {
            if (line.contains("FILTER")) {
                continue;
            } else {
                str += line + "\n";
            }

        }
        return str;
    }

    /*private String  modifyObjectAnswerSparql(String sparqlQuery) {
        String[] lines =  sparqlQuery.strip().trim().split(System.getProperty("line.separator"));
        String str="";
        for(String line :lines){
            if(line.contains("FILTER"))
               continue;
            else if(line.contains("rdfs:label"))
               continue;
            else
            str+=line+"\n";

        }
        return str;
    }*/
 /*
    
PREFIX dbo: <http://dbpedia.org/ontology/>
PREFIX res: <http://dbpedia.org/resource/>
SELECT DISTINCT ?uri WHERE { 
        res:French_Polynesia dbo:capital ?x .
        ?x dbo:mayor ?uri .
}
    
    
    
       sparqlQuery:::(bgp
  
  (triple ?subjOfPropx <http://dbpedia.org/ontology/capital> ?subjOfProp)
  (triple ?subjOfProp <http://dbpedia.org/ontology/mayor> ?objOfProp)
)
    "bindingType" : "Country",
    "returnType" : "Person",
     */
    private String setObjectWikiPediaComposite(String entityUrl, String sparqlOrg, QueryType queryType, String returnVariable) {
        String[] lines = sparqlOrg.split(System.getProperty("line.separator"));
        //entityUrl="http://dbpedia.org/resource/France";
        String sparql = "";
        for (String line : lines) {

            if (line.contains("bgp") && queryType.equals(QueryType.SELECT)) {
                line = "SELECT DISTINCT " + "?" + returnVariable + " WHERE { ";
            } /*else if (queryType.equals(QueryType.SELECT) && type.contains(RETURN_TYPE_SUBJECT)) {
                line = "SELECT DISTINCT " + "?" + RETURN_TYPE_SUBJECT + " WHERE { ";

            }*/ else if (line.contains("triple") || line.contains("?subjOfPropx") || line.contains("?objOfPropx")) {
                line = line.replace("triple", "");
                line = line.replace("?subjOfPropx", "<" + entityUrl + ">");
                line = line.replace("?objOfPropx", "<" + entityUrl + ">");
                line = line + " .";
            }

            sparql += line;

        }

        sparql = sparql.replace("(", "").replace(")", "") + "}";

        return sparql;
    }

    private boolean isPropertyLabel(String property) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean isEntity(String entityUrl) {
        if (entityUrl.contains("http:")) {
            return true;
        }
        return false;
    }

    public List<String> getUris() {
        return uris;
    }
    
     public static void main(String args[]){
         String sparqlSuperclass=DBpediaHelper.sparqlSuperclasses("http://dbpedia.org/resource/Hundred_Years'_War");
         System.out.println(sparqlSuperclass);
         SparqlQuery sparqlQuery=new SparqlQuery(sparqlSuperclass);
         System.out.println(sparqlQuery.getUris());
         
     }

}
