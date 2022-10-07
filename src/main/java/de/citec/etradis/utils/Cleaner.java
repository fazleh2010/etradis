/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.utils;

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

    
}
