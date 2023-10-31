/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core;

import org.neo4j.driver.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author elahi
 */
public class ImportRDFtoNeo4j {

        

    public static void main(String[] args) {
        // Configure Neo4j driver
        String uri = "bolt://localhost:7687";
        String username = "neo4j";
        String password = "password";
        Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));

        // Start a session
        try (Session session = driver.session()) {
            // Create indexes (if needed)
            session.run("CREATE INDEX ON :Resource(uri)");
            session.run("CREATE INDEX ON :Resource(label)");
            session.run("CREATE INDEX ON :Resource(type)");

            // Path to DBpedia RDF file
            String filePath = "/path/to/dbpedia.rdf";

            // Read and import DBpedia RDF data
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // Process each RDF line and import into Neo4j
                    String query = "CALL semantics.importRDFSnippet('" + line.replace("'", "\\'") + "', 'RDF/XML')";
                    session.run(query);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Import completed successfully.");
        }

        // Close the driver
        driver.close();
    }



}
