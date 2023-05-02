/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core.DBPediaHandler;

/**
 *
 * @author elahi
 */
public class DBpediaHelper {
    
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

    public static String sparqlSuperclasses(String uri) {
        String sparql= "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
"        PREFIX dbr: <http://dbpedia.org/resource/>\n" +
"        PREFIX dbt: <http://dbpedia.org/property/wikiPageUsesTemplate>\n" +
"        SELECT DISTINCT ?x\n" +
"        WHERE {{\n" +
"            <"+uri+"> rdf:type ?o .\n" +
"            ?o rdfs:subClassOf* ?x .\n" +
"            ?x rdfs:subClassOf owl:Thing .\n" +
"            FILTER ( STRSTARTS(STR(?o), STR(dbo:)) || STRSTARTS(str(?o), STR(dbr:)) )\n" +
"        }}";
        return sparql;
    }

}
