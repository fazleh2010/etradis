/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core.sparql;

import de.citec.etradis.core.FileFolderUtils;
import de.citec.etradis.core.dppedia.helper.DBpediaHelper;
import static de.citec.etradis.core.dppedia.helper.DBpediaHelper.FIND_CLASS;
import static de.citec.etradis.core.dppedia.helper.DBpediaHelper.sparqlEtradis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class CreateSuperClass {
    //private static List<String> etradisCategories=Arrays.asList(
    //           new String[]{"Event","Place", "TimePeriod", "TopicalConcept","CulturalArtifact", "MaterialObject","Miscellaneous"});
    private static List<String> etradisCategories=Arrays.asList(
               new String[]{"Event","Agent"});
    
  
  
    public String findEtradisClass(String className) {
        className = className.toLowerCase();
        String mapped_class = "Miscellaneous";

        if (className.contains("event")) {
            mapped_class = "Event";
        } else if (className.contains("agent") || className.contains("species")
                || className.contains("ethnicgroup") || className.contains("language")) {
            mapped_class = "Agent";
        } else if (className.contains("place")) {
            mapped_class = "Place";
        } else if (className.contains("timeperiod")) {
            mapped_class = "TimePeriod";
        } else if (className.contains("topicalconcept")) {
            mapped_class = "TopicalConcept";
        } else if (className.contains("work")) {
            mapped_class = "CulturalArtifact";
        } else if (className.contains("materialobject") || className.contains("meanoftransportation")
                || className.contains("currency") || className.contains("device")
                || className.contains("food") || className.contains("chemicalsubstance")) {
            mapped_class = "MaterialObject";
        } else {
            mapped_class = "Miscellaneous";
        }

        return mapped_class;
    }
    
     public static String addQuate(String string) throws IOException {
         return "\""+string+"\"";
     }
     
      public static String findSparql() throws IOException {
         return "select ?s where { ?s "+"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>"+" ?o }";
     }
   
    public void resourceToEtradisType(File inputFile, File outputFile, List<String> filers, Map<String, String> superClass) throws Exception {
        try {
            InputStream is = new FileInputStream(inputFile);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = "";
            Integer index = 0, total = 0;
            while ((line = buf.readLine()) != null) {
                String resourceUri = null, result = null, classUri = null,uri=null;
                try {
                    String[] info = line.split(" ");
                    resourceUri = info[0];
                    classUri = info[2];

                } catch (Exception ex) {
                    System.out.println("problem in turtle line!!!");
                    continue;
                }
                if (superClass.containsKey(classUri)) {
                    result = superClass.get(classUri);
                } else {
                    result = "<" + "http://localhost:9999/etradis/" + "Miscellaneous" + ">";
                }
                if(result.contains("Miscellaneous")){
                   result = "<" + "http://localhost:9999/etradis/" + "OtherClass" + ">";
                }

                String ksvLine = resourceUri + " " + "<" + "http://localhost:9999/etradis#type" + ">" + " " + result + " .";
                System.out.println(index + " " + ksvLine);
                FileFolderUtils.appendToFile(outputFile, ksvLine);
                index = index + 1;
            }
        } catch (Exception ex) {
            throw new Exception("error::" + ex.getMessage());
        }

    }
      
    public void saveSuperKlass(File inputFile, File outputFile,SparqlQuery sparqlQuery, List<String> filers ) {
        InputStream is;
        

        try {
            is = new FileInputStream(inputFile);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = "";
            Integer index=0,total=0;
            while ((line = buf.readLine()) != null) {
                String url = null;
                try {
                    String[] info = line.split(" ");
                    url = info[0].replace("<", "").replace(">", "");
                } catch (Exception ex) {
                    continue;
                }

                DBpediaHelper dbpediaHelper = new DBpediaHelper(sparqlQuery, url, FIND_CLASS);
                for (String sparql : dbpediaHelper.getSparqlResults().keySet()) {
                    List<String> result = dbpediaHelper.getSparqlResults(sparql);
                    for (String uri : result) {
                        CreateSuperClass createSuperClass = new CreateSuperClass();
                        String superClass = createSuperClass.findEtradisClass(uri);
                        superClass = superClass.replace("http://dbpedia.org/ontology/", "");
                        String ksvLine = addQuate(url) + "," + addQuate(superClass);
                        FileFolderUtils.appendToFile(outputFile, ksvLine);
                        System.out.println(" now:"+index+" "+ksvLine);
                        index=index+1;
                        break;
                    }

                }
            }
        } catch (Exception ex) {
        }

    }
    
    public void saveSuperKlassFromCsv(File inputFile, File outputFile, SparqlQuery sparqlQuery, List<String> filers) {
        InputStream is;

        try {
            is = new FileInputStream(inputFile);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = "";
            Integer index = 0, total = 0;
            while ((line = buf.readLine()) != null) {
                index = index + 1;
                String url = line;
                url = url.replace("\"", "");
                if (url.isEmpty()) {
                    continue;
                }

                DBpediaHelper dbpediaHelper = new DBpediaHelper(sparqlQuery, url, FIND_CLASS);
                for (String sparql : dbpediaHelper.getSparqlResults().keySet()) {
                    List<String> result = dbpediaHelper.getSparqlResults(sparql);
                    String superClass =null;
                    if (result.isEmpty()) {
                         superClass ="Miscellaneous";;
                    } else {
                        for (String uri : result) {
                            CreateSuperClass createSuperClass = new CreateSuperClass();
                            superClass = createSuperClass.findEtradisClass(uri);
                            superClass = superClass.replace("http://dbpedia.org/ontology/", "");
                            //index=index+1;
                            break;
                        }
                    }
                    String ksvLine = addQuate(url) + "," + addQuate(superClass);
                    FileFolderUtils.appendToFile(outputFile, ksvLine);
                    System.out.println(" now:" + index + " " + ksvLine);

                }
            }
        } catch (Exception ex) {
        }

    }
    

    public static String superClassSparql(String classUri) throws IOException {
        /*return "select ?subclass ?superclass where {\n"
                + "  ?subclass <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?superclass\n"
                + "}";*/
        return "SELECT DISTINCT ?x\n"
                + "        WHERE {\n"
                + "             <"+classUri+"> rdfs:subClassOf* ?x .\n"
                + "            ?x rdfs:subClassOf owl:Thing .\n"
                + "        }";
    }
    
    public static String findClassLocal() throws IOException {
        return "select DISTINCT ?o where { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o }";
    }
    
    public static Map<String,String> countEtradisCategoriySparqls() throws IOException {
         Map<String,String> sparqls=new TreeMap<String,String>();
        for(String category:etradisCategories){
             String sparql=countEtradisCategories(category);
             sparqls.put(category, sparql);
        }
        return sparqls;
    }
    
    public static String countEtradisCategories(String category) throws IOException {
        return "SELECT (COUNT(DISTINCT ?s) as ?c) where { ?s <http://localhost:9999/etradis#type> <http://localhost:9999/etradis/"+category+"> }";
    }
    
     public static String countEtradisCategoriesAll() throws IOException {
        return "SELECT (COUNT(DISTINCT ?s) as ?c) where { ?s <http://localhost:9999/etradis#type> ?o }";
    }
    
    


    
    public static void main(String args[]) throws IOException {
       
        //SparqlQuery sparqlQuery_1 = new SparqlQuery("http://localhost:9999/blazegraph/sparql", filers);
        
         //Testing triple store.
          //String sparqlStr=findSparql();
          //List<String> resources=sparqlQuery_1.runSparqlQuery(sparqlStr);
          //System.out.println(sparqlStr);
        
     
      
    }

    public List<String> filter(List<String> results) {
        List<String> filter=new ArrayList<String>();
        for(String result:results){
            if(result.contains("http://dbpedia.org/ontology/")){
               System.out.println(result);
               filter.add(result);
            }
            
        }
        return filter;
    }
    
      /*String sparql =DBpediaHelper.getTripleObject(classUri);
                List<String> results = sparqlQuery.runSparqlQuery(sparql);
                if (!results.isEmpty()) {
                    result = results.iterator().next();
                } else {
                    result = "<" + "http://localhost:9999/etradis/" + "NotFound" + ">";
                }*/


}
