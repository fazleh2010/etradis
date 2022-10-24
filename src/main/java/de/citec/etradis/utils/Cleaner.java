/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.utils;

import de.citec.etradis.core.FileFolderUtils;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author elahi
 */
public class Cleaner {
    
     public static String cleanUrl(String string) {
        string =string.replace(">", "").replace("<", "");
        string=string.replace("http://dbpedia.org/resource/", "").trim().strip().stripLeading().stripTrailing();
        return string;
    }
      public static String cleanUrlBracket(String string) {
        string =string.replace(">", "").replace("<", "");
        return string;
    }
     
      private static String getSearchFileName(String firstCharacter) {
        return firstCharacter + ".ttl";
    }
      
    public static String cleanPikleFiles(String rawString) {
        byte[] bytes = rawString.getBytes(StandardCharsets.US_ASCII);
        String utf8EncodedString = new String(bytes, StandardCharsets.US_ASCII);
        String[] info = utf8EncodedString.split("\\?");
        return info[0];

    }
    
     public static String findFirstWord(String uri_dbpedia) {
        uri_dbpedia = uri_dbpedia.replace("<", "").replace(">", "");
        uri_dbpedia = uri_dbpedia.replace("http://dbpedia.org/resource/", "");
        if (uri_dbpedia.length() > 1) {
            return "" + uri_dbpedia.charAt(0);
        }
        return "" + '_';
    }
    
   
    
}
