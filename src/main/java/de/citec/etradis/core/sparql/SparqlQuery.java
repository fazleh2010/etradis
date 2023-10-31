/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core.sparql;

import de.citec.etradis.core.FileFolderUtils;
import de.citec.etradis.core.dppedia.helper.DBpediaHelper;
import static de.citec.etradis.core.dppedia.helper.DBpediaHelper.FIND_CLASS;
import static de.citec.etradis.core.dppedia.helper.DBpediaHelper.FIND_OBJECT_CONNECTION;
import static de.citec.etradis.core.dppedia.helper.DBpediaHelper.FIND_SUBJECT_CONNECTION;
import de.citec.etradis.core.dppedia.helper.Filter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import static java.lang.System.exit;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
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
    private String sparqlQuery = null;
    private String command = null;
    private List<String> uris=new ArrayList<String>();

    private String type = null;
    private List<Binding> bindingList = new ArrayList<Binding>();
    private List<String> filters = new ArrayList<String>();
    private Boolean online = false;
    
   
    public SparqlQuery(String endpointT,List<String>filter) {
        endpoint = endpointT;
        this.filters = filter;
    }
     public List<String> runSparqlQuery(String sparqlQuery) {
        String resultSparql = executeSparqlQuery(sparqlQuery);
        System.out.println(resultSparql);
        this.parseResult(resultSparql);
        return this.uris;
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
        uris = new ArrayList<String>();
        try {
            Document document = builder.parse(new InputSource(new StringReader(
                    xmlStr)));
            NodeList results = document.getElementsByTagName("results");
            for (int i = 0; i < results.getLength(); i++) {
                NodeList childList = results.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("result".equals(childNode.getNodeName())) {
                        //if (endpoint.contains("dbpedia") && type.contains(FIND_ANY_ANSWER) && answer.contains("--")) {
                        //    continue;
                        //} else {
                        String objectOfProperty = childList.item(j).getTextContent().trim();
                        uris.add(objectOfProperty);
                        //System.out.println(objectOfProperty);
                        /*System.out.println(objectOfProperty+" "+filters);
                         String property = null;
                        String subOrObjUri = null;

                        if(objectOfProperty.contains(" ")){
                          String[] info = objectOfProperty.split(" ");
                          try{
                               property = info[1];
                          subOrObjUri = info[0];
                           if(this.filters.contains(property))
                              uris.add(subOrObjUri);
                           else if(this.filters.isEmpty()){
                               uris.add(objectOfProperty); 
                           } 
                           else
                               uris.add(objectOfProperty);
                          }catch(Exception ex){
                              System.out.println(objectOfProperty+" "+ex.getMessage());
                             continue; 
                          }*/
                         
                             /*if(objectOfProperty.contains("http://dbpedia.org/ontology/wikiPageWikiLink")){
                                uris.add(objectOfProperty); 

                             }*/
                             /*if(objectOfProperty.contains("http://www.w3.org/2000/01/rdf-schema#label")){
                                uris.add(objectOfProperty); 

                             }
                             if(objectOfProperty.contains("http://dbpedia.org/ontology/abstract")){
                                uris.add(objectOfProperty); 

                             }*/
                             
                         
                       
                        
                       
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

    public String setSubjectLabelWikipedia(String entityUrl, String property, String language,List<String>filter) {
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


    private boolean isEntity(String entityUrl) {
        if (entityUrl.contains("http:")) {
            return true;
        }
        return false;
    }

    public List<String> getUris() {
        return uris;
    }
    
     public static void main(String args[]) throws IOException{
         List<String>filers=Arrays.asList(new String[]{"http://dbpedia.org/ontology/wikiPageWikiLink"});
          String endpoint = "https://dbpedia.org/sparql";
          //String endpoint = "http://localhost:9999/blazegraph/sparql";
          String url="http://dbpedia.org/resource/Hundred_Years'_War";
          SparqlQuery sparqlQuery =new SparqlQuery(endpoint,filers);
          String sparqlL="SELECT DISTINCT ?o\n" +
"        WHERE {\n" +
"            <http://dbpedia.org/resource/Hundred_Years'_War> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o .\n" +
"        }";

          //DBpediaHelper dbpediaHelper=new DBpediaHelper(sparqlQuery,url,FIND_CLASS+FIND_OBJECT_CONNECTION+FIND_SUBJECT_CONNECTION);
           DBpediaHelper dbpediaHelper=new DBpediaHelper(sparqlQuery,url,FIND_CLASS);
           //DBpediaHelper dbpediaHelper=new DBpediaHelper(sparqlQuery,url,FIND_CLASS);

           String str="";
          for(String sparql: dbpediaHelper.getSparqlResults().keySet()){
               List<String> result=dbpediaHelper.getSparqlResults(sparql);
                //System.out.println(" sparql::"+sparql);
                System.out.println(url+" "+result);
                String line=result+"\n";
                str+=line;
          }
          //FileFolderUtils.stringToFile(str, new File("src/main/resources/publi.txt"));
          
          //Testing triple store.
          /*String endpointT="http://localhost:9999/blazegraph/sparql";
          SparqlQuery sparqlQuery=new SparqlQuery(endpointT,new ArrayList<String>());
          String sparqlStr="select ?s where { ?s ?p ?o } limit 1";
          List<String> results=sparqlQuery.runSparqlQuery(sparqlStr);
          System.out.println(results);*/
          
          

        
     }
     
     /*
     PREFIX dbo: <http://dbpedia.org/ontology/>
        PREFIX dbr: <http://dbpedia.org/resource/>
        PREFIX dbt: <http://dbpedia.org/property/wikiPageUsesTemplate>
        PREFIX owl: <http://www.w3.org/2002/07/owl#>
        PREFIX wd: <http://www.wikidata.org/>
        SELECT DISTINCT ?p ?o
        WHERE {{
            <http://dbpedia.org/resource/Hundred_Years'_War> ?p ?o .
            FILTER ( STRSTARTS(STR(?o), STR(dbo:)) || STRSTARTS(str(?o), STR(dbr:)) || isLiteral(?o) || STRSTARTS(STR(?p), STR(dbo:wikiPageExternalLink)) || (STRSTARTS(STR(?p), STR(owl:sameAs)) && (STRSTARTS(STR(?o), STR(wd:entity))))) .
            FILTER ( !STRSTARTS(STR(?p), STR(dbt:)) ) .
        }}
     */
     /*
      PREFIX dbo: <http://dbpedia.org/ontology/>
        PREFIX dbr: <http://dbpedia.org/resource/>
        PREFIX dbt: <http://dbpedia.org/property/wikiPageUsesTemplate>
        PREFIX owl: <http://www.w3.org/2002/07/owl#>
        PREFIX wd: <http://www.wikidata.org/>
        SELECT DISTINCT ?p ?o
        WHERE {{
            <http://dbpedia.org/resource/Hundred_Years'_War> ?p ?o .
            FILTER ( STRSTARTS(STR(?o), STR(dbo:)) || STRSTARTS(str(?o), STR(dbr:)) || isLiteral(?o) || STRSTARTS(STR(?p), STR(dbo:wikiPageExternalLink)) || (STRSTARTS(STR(?p), STR(owl:sameAs)) && (STRSTARTS(STR(?o), STR(wd:entity))))) .
            FILTER ( !STRSTARTS(STR(?p), STR(dbt:)) ) .
        }}
     */
     
     /*PREFIX dbo: <http://dbpedia.org/ontology/>
PREFIX dbr: <http://dbpedia.org/resource/>
        SELECT DISTINCT ?x
        WHERE {
            <http://dbpedia.org/resource/Hundred_Years'_War> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o .
            ?o <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?x .
            ?x<http://www.w3.org/2000/01/rdf-schema#subClassOf> owl:Thing .
            FILTER ( STRSTARTS(STR(?o), STR(dbo:)) || STRSTARTS(str(?o), STR(dbr:)) )
        }*/

}
