/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core.dppedia.helper;

import de.citec.etradis.core.sparql.SparqlQuery;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author elahi
 */
public class DBpediaHelper {

   

    private LinkedHashMap<String,List<String>> sparqlResults = new LinkedHashMap<String,List<String>>();
    public static String FIND_ETRADIS = "FIND_ETRADIS";
    public static String FIND_CLASS = "FIND_CLASS";
    public static String FIND_OBJECT_CONNECTION = "FIND_OBJECT_CONNECTION";
    public static String FIND_SUBJECT_CONNECTION = "FIND_SUBJECT_CONNECTION";
    

    public DBpediaHelper(SparqlQuery sparqlQuery, String uri,String type) {
        if (type.contains(FIND_ETRADIS)) {
            String sparql = sparqlEtradis(uri);
            List<String>results = sparqlQuery.runSparqlQuery(sparql);
            sparqlResults.put(sparql, results);

        }
        
        if (type.contains(FIND_CLASS)) {
            String sparql = sparqlSuperclasses(uri);
            List<String>results = sparqlQuery.runSparqlQuery(sparql);
            sparqlResults.put(sparql, results);

        }
        if (type.contains(FIND_OBJECT_CONNECTION)) {
            String sparql =this.getObject(uri);
             List<String>results = sparqlQuery.runSparqlQuery(sparql);
            sparqlResults.put(sparql, results);
        }
        if (type.contains(FIND_SUBJECT_CONNECTION)) {
            String sparql =this.getSubject(uri);
            List<String>results = sparqlQuery.runSparqlQuery(sparql);
            sparqlResults.put(sparql, results);
        }
    }

    public static String sparqlEtradis(String uri) {
        return "select DISTINCT ?o where { " + uri + " <http://localhost:9999/etradis#type> ?o }";
    }

    public static String getTripleObject(String uri) {
        return "select DISTINCT ?o where { " + uri + " <http://localhost:9999/etradis#type> ?o }";
    }
     public static String sparqlSuperclasses(String uri) {
        String sparql = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "        PREFIX dbr: <http://dbpedia.org/resource/>\n"
                + "        PREFIX dbt: <http://dbpedia.org/property/wikiPageUsesTemplate>\n"
                + "        SELECT DISTINCT ?x\n"
                + "        WHERE {{\n"
                + "            <" + uri + "> rdf:type ?o .\n"
                + "            ?o rdfs:subClassOf* ?x .\n"
                + "            ?x rdfs:subClassOf owl:Thing .\n"
                + "            FILTER ( STRSTARTS(STR(?o), STR(dbo:)) || STRSTARTS(str(?o), STR(dbr:)) )\n"
                + "        }}";

        return sparql;
    }

    public  String getObject(String uri) {
        String sparql = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "        PREFIX dbr: <http://dbpedia.org/resource/>\n"
                + "        PREFIX dbt: <http://dbpedia.org/property/wikiPageUsesTemplate>\n"
                + "        PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "        PREFIX wd: <http://www.wikidata.org/>\n"
                + "        SELECT DISTINCT ?p ?o\n"
                + "        WHERE {{\n"
                + "            <" + uri + "> ?p ?o .\n"
                + "            FILTER ( STRSTARTS(STR(?o), STR(dbo:)) || STRSTARTS(str(?o), STR(dbr:)) || isLiteral(?o) || !STRSTARTS(STR(?p), STR(dbo:wikiPageExternalLink)) || (STRSTARTS(STR(?p), STR(owl:sameAs)) && (STRSTARTS(STR(?o), STR(wd:entity))))) .\n"
                + "            FILTER ( !STRSTARTS(STR(?p), STR(dbt:)) ) .\n"
                + "        }}";
        return sparql;
    }

    public  String getSubject(String uri) {
        String sparql = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "        PREFIX dbr: <http://dbpedia.org/resource/>\n"
                + "        PREFIX dbt: <http://dbpedia.org/property/wikiPageUsesTemplate>\n"
                + "        SELECT DISTINCT ?p ?s\n"
                + "        WHERE {{\n"
                + "            ?s ?p <" + uri + "> .\n"
                + "            FILTER ( STRSTARTS(STR(?s), STR(dbo:)) || STRSTARTS(str(?s), STR(dbr:)) || isLiteral(?s) || STRSTARTS(STR(?p), STR(dbo:wikiPageExternalLink)) ) .\n"
                + "            FILTER ( !STRSTARTS(STR(?p), STR(dbt:)) ) .\n"
                + "        }}";
        return sparql;
    }
    
    public String findSuperClass(String className) {
        className = className.toLowerCase();
        String mapped_class = "Miscellaneous";

        if (className.contains("event")) {
            mapped_class = "Event";
        } else if (className.contains("agent") || className.contains("species")
                || className.contains("ethnicgroup") || className.contains("language")) {
            mapped_class = "Event";
        } else if (className.contains("place")) {
            mapped_class = "Place";
        } else if (className.contains("timeperiod")) {
            mapped_class = "TimePeriod";
        } else if (className.contains("topicalconcept")) {
            mapped_class = "TopicalConcept";
        } else if (className.contains("work")) {
            mapped_class = "CulturalArtifact";
        } else if (className.contains("work")) {
            mapped_class = "CulturalArtifact";
        } else if (className.contains("materialobject") || className.contains("meanoftransportation") 
                || className.contains("currency")|| className.contains("device") 
                || className.contains("food") || className.contains("chemicalsubstance")) {
            mapped_class = "MaterialObject";
        } else {
            mapped_class = "Miscellaneous";
        }

        return mapped_class;
    }
    
    /*
        def _map_classes(self, super_classes: List) -> str:
        """
        Helper function to reduce dbpedia owl super classes to a given set of available labels.
        :param super_classes: list of dbpedia owl super classes of a node
        :return:
        """
        # TODO: improve mapping by nlp, ml, etc...
        mapped_class = "Miscellaneous"
        for c in super_classes:
            if "event" in c.lower():
                mapped_class = "Event"
                break
            elif any(agent in c.lower() for agent in ["agent", "species", "ethnicgroup", "language"]):
                mapped_class = "Agent"
                break
            elif "place" in c.lower():
                mapped_class = "Place"
                break
            elif "timeperiod" in c.lower():
                mapped_class = "TimePeriod"
                break
            elif "topicalconcept" in c.lower():
                mapped_class = "TopicalConcept"
                break
            elif "work" in c.lower():
                mapped_class = "CulturalArtifact"
                break
            elif any(mat_obj in c.lower() for mat_obj in ["materialobject", "meanoftransportation", "currency", "device", "food", "chemicalsubstance"]):
                mapped_class = "MaterialObject"
                break
            else:
                mapped_class = "Miscellaneous"
        return mapped_class

    */

    

    /*"""
        Initialise DBPedia handler which handles crawling process of dbpedia content using sparql endpoint.
        """
        self.sparql_endpoint = "http://dbpedia.org/sparql/"
        self.max_retries = 3  # define number of retries for any sparql query
        self.sleep_time = 3  # define time in seconds to wait for retry
        # specify uris which should be handled as property for any node
        self.node_property_names = ["http://dbpedia.org/ontology/wikiPageExternalLink",
                                     "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2002/07/owl#sameAs"]
        self.nodes = {}
        self.connections = set()
     */
 /*def _get_superclasses(self, uri: str) -> List:
        """
        Helper function to retrieve super classes (one level below own:Thing) for a specific uri according to dbpedia ontology schema (label tree)
        :param uri: specify uri of dbpedia resource
        :return: List contains super classes for given entry
        """
        super_classes = []
        query = f"""
        PREFIX dbo: <http://dbpedia.org/ontology/>
        PREFIX dbr: <http://dbpedia.org/resource/>
        PREFIX dbt: <http://dbpedia.org/property/wikiPageUsesTemplate>
        SELECT DISTINCT ?x
        WHERE {{
            <{uri}> rdf:type ?o .
            ?o rdfs:subClassOf* ?x .
            ?x rdfs:subClassOf owl:Thing .
            FILTER ( STRSTARTS(STR(?o), STR(dbo:)) || STRSTARTS(str(?o), STR(dbr:)) )
        }}
        """
        try:
            results = es.run_query(query, self.sparql_endpoint)
            if results:
                for res in results:
                    super_classes.append(res.get("x").get("value"))
            else:
                super_classes = ["owl:Thing"]
        except Exception as e:
            print(e)
        return super_classes*/
 /* def _get_predicate_object(self, uri: str, node: DefaultDict, connections: Set) -> bool:
        """
        Helper function to retrieve outgoing properties and relations of a given dbpedia resource.
        :param uri: dbpedia resource identifier
        :param node: store properties
        :param connections: store connections to other resources
        :return: boolean indicates whether query succeeded
        """
        query = f"""
        PREFIX dbo: <http://dbpedia.org/ontology/>
        PREFIX dbr: <http://dbpedia.org/resource/>
        PREFIX dbt: <http://dbpedia.org/property/wikiPageUsesTemplate>
        PREFIX owl: <http://www.w3.org/2002/07/owl#>
        PREFIX wd: <http://www.wikidata.org/>
        SELECT DISTINCT ?p ?o
        WHERE {{
            <{uri}> ?p ?o .
            FILTER ( STRSTARTS(STR(?o), STR(dbo:)) || STRSTARTS(str(?o), STR(dbr:)) || isLiteral(?o) || STRSTARTS(STR(?p), STR(dbo:wikiPageExternalLink)) || (STRSTARTS(STR(?p), STR(owl:sameAs)) && (STRSTARTS(STR(?o), STR(wd:entity))))) .
            FILTER ( !STRSTARTS(STR(?p), STR(dbt:)) ) .
        }}
        """
        try:
            results = es.run_query(query, self.sparql_endpoint)
            for res in results:
                predicate_val = res.get("p").get("value")
                obj = res.get("o")
                # add as property to node or mark as connection between two nodes
                # if predicate_val in self.node_property_names or obj.get("type") != "uri":
                if predicate_val in self.node_property_names or obj.get("type") != "uri":
                    if "xml:lang" in obj:
                        if obj.get('xml:lang') == "en":
                            object_val = f"{obj.get('value')}@{obj.get('xml:lang')}"
                            node[predicate_val].append(object_val)
                    elif obj.get("datatype") == "http://www.w3.org/2001/XMLSchema#date":
                        date_prop = (predicate_val, obj.get("value"))
                        node["dates"].append(date_prop)
                    # elif predicate_val == "http://www.w3.org/1999/02/22-rdf-syntax-ns#type":
                    else:
                        node[predicate_val].append(obj.get("value"))
                else:
                    connections.add((uri, predicate_val, obj.get("value")))
            return True
        except Exception as e:
            print(e)
            return False*/

    public LinkedHashMap<String, List<String>> getSparqlResults() {
        return sparqlResults;
    }

    public List<String> getSparqlResults(String sparql) {
        if (sparqlResults.containsKey(sparql)) {
            return sparqlResults.get(sparql);
        }
        return new ArrayList<String>();
    }
}
