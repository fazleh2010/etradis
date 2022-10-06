/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core.sparql;

/**
 *
 * @author elahi
 */
/**
 *
 * @author elahi
 */
public class PrepareSparqlQuery {
    
    public PrepareSparqlQuery(){
        
    }
    public  String findSparql(String property, String object) {
        String sparql = null;

        sparql = "select  ?s\n"
                + "    {\n"
                + "   " + "?s" + " " + "<" + property + ">" + "  " + "'" + object + "'" + "\n"
                + "    }";

        return sparql;
    }

    private String rdfType = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";

    public static String setSubjectWikipedia(String objectUri, String property, String rdfProperty, String objectClassUri) {
        String sparql = null;

        return sparql;

    }

    public static String setObjectWikiPedia(String subjectUrl, String property, String rdfProperty, String objectClassUri) {
        String sparql = null;

        return sparql;

    }

    public static String setSubjectWikipediaWithClassName(String entityUri, String property, String rdfProperty, String entityClass) {
        String sparql = null;
        //className //        ?uri rdf:type dbo:Country .
        if (entityUri.contains("http:")) {
            sparql = "select  ?s\n"
                    + "    {\n"
                    + "   " + "?s" + " " + "<" + property + ">" + "  " + "<" + entityUri + "> ." + "\n"
                    + "   " + "?s" + " " + "<" + rdfProperty + ">" + "  " + "<" + entityClass + "> ." + "\n"
                    + "    }";
        } else {
            sparql = "select  ?s\n"
                    + "    {\n"
                    + "   " + "?s" + " " + "<" + property + ">" + "  " + "<" + entityUri + "> ." + "\n"
                    + "   " + "?s" + " " + "<" + rdfProperty + ">" + "  " + "<" + entityClass + "> ." + "\n"
                    + "    }";

        }

        return sparql;

    }

    public static String setSubjectWikipediaWithOccupation(String entityUri, String property, String otherProperty, String otherClass) {
        String sparql = null;

        if (entityUri.contains("http:")) {
            sparql = "select  ?s\n"
                    + "    {\n"
                    + "   " + "?s" + " " + "<" + property + ">" + "  " + "<" + entityUri + "> ." + "\n"
                    + "   " + "?s" + " " + "<" + otherProperty + ">" + "  " + "<" + otherClass + "> ." + "\n"
                    + "    }";
        } else {
            sparql = "select  ?s\n"
                    + "    {\n"
                    + "   " + "?s" + " " + "<" + property + ">" + "  " + "<" + entityUri + "> ." + "\n"
                    + "   " + "?s" + " " + "<" + otherProperty + ">" + "  " + "<" + otherClass + "> ." + "\n"
                    + "    }";

        }

        return sparql;

    }

    public static String setObjectWikipediaWithClassName(String entityUri, String property, String rdfProperty, String entityClass) {
        String sparql = null;
        //className //        ?uri rdf:type dbo:Country .
        sparql = "select  ?o\n"
                + "    {\n"
                + "    " + "<" + entityUri + ">" + " " + "<" + property + ">" + "  " + "?o ." + "\n"
                + "   " + "?o" + " " + "<" + rdfProperty + ">" + "  " + "<" + entityClass + "> ." + "\n"
                + "    }";

        return sparql;

    }

    public static String setObjectWikipediaWithOccupation(String entityUri, String property, String otherProperty, String otherClass) {
        String sparql = null;

        sparql = "select  ?o\n"
                + "    {\n"
                + "    " + "<" + entityUri + ">" + " " + "<" + property + ">" + "  " + "?o ." + "\n"
                + "   " + "?o" + " " + "<" + otherProperty + ">" + "  " + "<" + otherClass + "> ." + "\n"
                + "    }";

        return sparql;

    }

    public static String setObjectWikiPediaCount(String entityUrl, String property, String variable) {
        //SELECT (COUNT(DISTINCT ?x) as ?c) WHERE {  <http://dbpedia.org/resource/Turkmenistan> <http://dbpedia.org/ontology/language> ?x . } 
        variable = "?" + variable;
        String sparql = "select " + "(COUNT(DISTINCT " + variable + ") as ?c) WHERE" + "\n"
                + "    {\n"
                + "    " + "<" + entityUrl + ">" + " " + "<" + property + ">" + "  " + variable + " ." + "\n"
                + "    }";
        return sparql;

    }

    public static String setSubjectWikiPediaCount(String objectUri, String property, String variable) {
        //SELECT (COUNT(DISTINCT ?x) as ?c) WHERE {  <http://dbpedia.org/resource/Turkmenistan> <http://dbpedia.org/ontology/language> ?x . } 
        variable = "?" + variable;
        String sparql = "select  " + "(COUNT(DISTINCT " + variable + ") as ?c) WHERE" + "\n"
                + "    {\n"
                + "   " + variable + " " + "<" + property + ">" + "  " + "<" + objectUri + "> ." + "\n"
                + "    }";
        return sparql;

    }

    public static String setBooleanWikiPedia(String domainEntityUrl, String property, String rangeEntityUrl) {
        String sparql
                = "ASK WHERE { "
                + "<" + domainEntityUrl + ">" + " " + "<" + property + ">" + " " + "<" + rangeEntityUrl + "> . "
                + "}";
        return sparql;
    }

    public String setSubjectWikiData(String entityUrl, String propertyUrl, String language) {
        return "SELECT ?subjectLabel WHERE {\n"
                + "    subject <" + propertyUrl + "> ?object.\n"
                + "   SERVICE wikibase:label {\n"
                + "     bd:serviceParam wikibase:language \"" + language + "\" .\n"
                + "   }\n"
                + "}\n"
                + "";

    }

    public static String setSparqlQueryPropertyWithSubjectFilterWikipedia(String entityUrl, String property) {
        String sparql = null;
        if (entityUrl.contains("http:")) {
            sparql = "select  ?s\n"
                    + "    {\n"
                    + "   " + "?s" + " " + "<" + property + ">" + "  " + "<" + entityUrl + ">" + "\n"
                    + "    }";
        } else {
            sparql = "select  ?s\n"
                    + "    {\n"
                    + "   " + "?s" + " " + "<" + property + ">" + "  " + entityUrl + "\n"
                    + "    }";
        }
        return sparql;

    }

    public static String setLabelWikipedia(String entityUrl, String language) {
        String sparql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "   PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "   PREFIX dbpedia: <http://dbpedia.org/resource/>\n"
                + "\n"
                + "   SELECT DISTINCT ?label \n"
                + "   WHERE {  \n"
                + "       <" + entityUrl + "> rdfs:label ?label .     \n"
                + "       filter(langMatches(lang(?label),\"" + language + "\"))         \n"
                + "   }";

        return sparql;

    }

    public static String setLabelWikiData(String entityUrl, String language) {
        String sparql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "   PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "   PREFIX dbpedia: <http://dbpedia.org/resource/>\n"
                + "\n"
                + "   SELECT DISTINCT ?label \n"
                + "   WHERE {  \n"
                + "       <" + entityUrl + "> rdfs:label ?label .     \n"
                + "       filter(langMatches(lang(?label),\"EN\"))         \n"
                + "   }";

        return sparql;

    }

    public static String setSparqlQueryForTypesWikipedia(String propertyUrl, String objectUrl) {
        String sparql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "   PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "   PREFIX dbpedia: <http://dbpedia.org/resource/>\n"
                + "\n"
                + "   SELECT DISTINCT ?label \n"
                + "   WHERE {  \n"
                + "   " + "?label" + " " + "<" + propertyUrl + ">" + " " + "<" + objectUrl + ">" + " .     \n"
                + "       filter(langMatches(lang(?label),\"EN\"))         \n"
                + "   }";

        return sparql;

    }

    public static String setObjectWikiPedia2(String entityUrl, String property) {
        return " PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "PREFIX res: <http://dbpedia.org/resource/>\n"
                + "SELECT DISTINCT ?uri \n"
                + "WHERE { \n"
                + "        res:French_Polynesia dbo:capital ?x .\n"
                + "        ?x dbo:mayor ?uri .\n"
                + "}";

    }

    public static String setObjectWikiData(String entityUrl, String propertyUrl, String language) {
        /*return "SELECT ?object ?objectLabel WHERE {\n"
                + "   "+"<"+entityUrl+">"+" "+"<"+property+">"+" ?object.\n"
                + "   SERVICE wikibase:label {\n"
                + "     bd:serviceParam wikibase:language \"en\" .\n"
                + "   }\n"
                + "}";*/

 /*return "SELECT ?objectLabel WHERE {\n"
                + "    <" + entityUrl + "> <" + propertyUrl + "> ?object.\n"
                + "   SERVICE wikibase:label {\n"
                + "     bd:serviceParam wikibase:language \""+language+"\" .\n"
                + "   }\n"
                + "}\n"
                + "";*/
        return "SELECT ?label WHERE {\n"
                + "    <" + entityUrl + "> <" + propertyUrl + "> ?object.\n"
                + "  ?object rdfs:label ?label \n"
                + "        FILTER (langMatches( lang(?label), \"" + language + "\" ) )\n"
                + "}";

    }

    public static String setObjectBen(String entityUrl, String propertyUrl, String language) {

        return "SELECT ?object WHERE {\n"
                + "    <" + entityUrl + "> <" + propertyUrl + "> ?object.\n"
                + "}";

    }

}
