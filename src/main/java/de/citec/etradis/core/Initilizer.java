/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author elahi
 */
public class Initilizer {
    
    public static String getResourceWikipediaLinkDir(){
        return "../dbpedia/output/";
    }

    public static Map<String, Map<String, String>> getWikipediaFiles() {
        Map<String, Map<String, String>> wikidataMap = new TreeMap<String, Map<String, String>>();
        File wikiFiles = new File("../dbpedia/output/");
        for (File file : wikiFiles.listFiles()) {
            String key = file.getName().replace(".ttl", "");
            String searchFileName = "../dbpedia/output/" + file.getName();
            Map<String, String> resources = FileFolderUtils.fileToMap(new File(searchFileName), -1);
            System.out.println(searchFileName);
            wikidataMap.put(key, resources);
        }
        return wikidataMap;

    }

    public static List<File> sortedFile(String dir) {
        Set<String> files = new TreeSet<String>();
        List<File> sortedFileList = new ArrayList<File>();
        File file = new File(dir);
        for (String fileName : file.list()) {
            files.add(fileName);
        }
        for (String fileName : files) {
            sortedFileList.add(new File(dir+fileName));
        }
        return sortedFileList;
    }

}
