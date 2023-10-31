/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.utils;

import static de.citec.etradis.core.Main.writeFile;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;

public class RegexMatcherExample {

    public static void main(String[] args) {
        String fileName = "LocationP625_Corrected_rest_Corrected_2.ttl"; // done until 5037041
        String filePrefix = fileName.replace(".ttl", "");
        //String fileName = "Cultural_P50_2.ttl"; 
        String inputFilePath = "/media/elahi/Elements/A-Projects/wikidata/" + fileName;
        String outputFilePath = "/media/elahi/Elements/A-Projects/wikidata/" + filePrefix + "_Corrected_2_2.ttl";
        String wrongFilePath = "/media/elahi/Elements/A-Projects/wikidata/" + filePrefix + "_Wrong_2_2.ttl";

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            String line = null;
            Integer total = 26183890; //21497561  //36517768
            //Integer limit=100000;
            Integer index=1;
            while ((line = reader.readLine()) != null) {
                
                if(index<5033878){
                    index=index+1;
                   continue; 
                }
                if (check(line)) {
                    
                    writeFile(outputFilePath, line + "\n");

                } else {
                    System.out.println(index+" in valid::" + line);
                     writeFile(wrongFilePath, line + "\n");

                }
                index=index+1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Boolean check(String string) {
        //"Point(0.903167 51.721525)"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .
        String[] info = string.split("> ");
        String subject = info[0], predicate = info[1], object = info[2];
        int inCount = StringUtils.countMatches(string, "<");
        int outCount = StringUtils.countMatches(string, ">");
        int endCount = StringUtils.countMatches(string, "> .");
        
       

        if (string.contains("> . .")) {
            return false;
        }
        if (!subject.contains("<http://www.wikidata.org/entity/")) {
            return false;
        }
        if (!predicate.contains("<http://www.wikidata.org/prop/direct/P625")) {
            return false;
        }
        if(!object.contains("http://www.opengis.net/ont/geosparql#wktLiteral")){
           return false; 
        }
        if (!object.contains("\"Point(")) {
            return false;
        }
        if (endCount == 1 && inCount == 3 && outCount == 3) {
            ;
        } else {
            return false;
        }

        if (regularExprPoint(string,object)) {
            return true;
        }

        return false;

    }

    public static Boolean regularExprPoint(String string,String text) {
        // The string to search for a pattern

        int bktCountIn = StringUtils.countMatches(text, "(");
        int bktCountOut = StringUtils.countMatches(text, ")");
        int quoteCount = StringUtils.countMatches(text, "\"");
        int symCount = StringUtils.countMatches(text, "^");
        int pointCount = StringUtils.countMatches(text, "Point");
        if (!string.endsWith("^^<http://www.opengis.net/ont/geosparql#wktLiteral> .")) {
           return false;
        }

        if (bktCountIn == 1 && bktCountOut == 1 && quoteCount == 2 && symCount == 2) {
            
            if (text.contains("Point(") && text.contains(")\"^^<http:")) {
                text = StringUtils.substringBetween(text, "Point(", ")\"^^<http:");
                String[] info = text.split(" ");
                if (info.length == 2) {
                    return true;
                }
            }
            

        }
        return false;

    }

    public static Boolean isFloatValue(String text) {
        Boolean flag = false;
        // Define a regular expression pattern to match floating-point numbers
        String regex = "-?\\d+\\.\\d+";

        // Compile the regular expression pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher object to find the pattern in the text
        Matcher matcher = pattern.matcher(text);

        // Find and print all matching floating-point numbers
        while (matcher.find()) {
            String matchedFloat = matcher.group();
            //System.out.println("Found floating-point number: " + matchedFloat);
            flag = true;
        }
        return flag;
    }

    /*public static void main(String[] args) {
        // The string to search for a pattern
        String line = "<http://www.wikidata.org/entity/Q80584971> <http://www.wikidata.org/prop/direct/P625> \"Point(-3.749223947525 51.736025315995)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .";
        line = "<http://www.wikidata.org/entity/Q80584971> <http://www.wikidata.org/prop/direct/P625> \"Point(-3.749223947525 51.736025315995)\"^^^<http://www.opengis.net/ont/geosparql#wktLiteral> .";
        line = "<http://www.wikidata.org/entity/Q80584971> <http://www.wikidata.org/prop/direct/P625> \"Point(-3.749223947525 51.736025315995)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> ";
        line="<http://www.wikidata.org/entity/Q8169> <http://www.wikidata.org/prop/direct/P625> \"Po \"Point(17.251086 49.5934)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .";
        line ="<http://www.wikidata.org/entity/Q25726746> <http://www.wikidata.org/prop/direct/P625> \"Point(73.880883 56.892827)\"1666666667)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .";
        line ="<http://www.wikidata.org/entity/Q161051> <http://www.wikidata.org/prop/direct/P625> \"Point(10.320277777\"Point(-1.287943 52.393512)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .";
        line="<http://www.wikidata.org/entity/Q161051> <http://www.wikidata.org/prop/direct/P625> \"Point(10.320277777\"Point(-1.287943 52.393512)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .";
        line="<http://www.wikidata.org/entity/Q19723807> <http://www.wikidata.org/prop/direct/P625> \"Point(110.86 -6.536)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> ..";
        line="<http://www.wikidata.org/entity/Q19723807> <http://www.wikidata.org/prop/direct/P625> \"Point(110.86 -6.536)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .:test";
        line="<http://www.wikidata.org/entity/Q110335466> <http://www.wikidata.org/prop/direct/P625> \"Point(5.2047 46.9934)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .";
        
        if (check(line)) {
            System.out.println("valid::" + line);
        } else {
            System.out.println("in valid::" + line);
        }
    
}*/
 /*public static Boolean regularExprPoint(String text, String regex) {
        // The string to search for a pattern
        text = text.replace("^^<http:", "\n");
        String[] info = text.split("\n");
        text = info[0];

        // Compile the regular expression pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher object to find the pattern in the text
        Matcher matcher = pattern.matcher(text);

        // Find the first match
        if (matcher.find()) {
            // The matched text
            String match = matcher.group();

            // Print the matched text
            //System.out.println("Matched: " + match + " " + text);

            // Extract captured groups (latitude and longitude)
            String latitude = matcher.group(1);
            String longitude = matcher.group(2);

            // Print the latitude and longitude
            //System.out.println("Latitude: " + latitude);
            //System.out.println("Longitude: " + longitude);
            return true;
        } else {
            return false;
        }
    }*/
}
