/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core;

import de.citec.etradis.finder.ImageFinder;
import static de.citec.etradis.core.Constants.URI_MEDIA;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class Main implements Constants {

    public static void main(String args[]) throws IOException {
        String urlDir = "/media/elahi/My Passport/etradis/dbpedia/wikipedia/";
        File[] files = new File("/media/elahi/My Passport/etradis/dbpedia/wikipedia/").listFiles();
        String processFileList="fileProcessList.txt";
        String str="";
        Integer fileIndex=0,fileLimit=1000, urlLimit=-1;
        for (File file : files) {
            fileIndex=fileIndex+1;
            String outputFile = null;
            if (file.getName().contains("wikipedia-links_lang=en.ttl.a")) {
                System.out.println(file.getName());
                outputFile = file.getAbsolutePath() + file.getName().replace(".ttl", ".txt");
            }
            else 
                continue;
            Map<String, String> results = getLinks(file, urlLimit);
            String fileName=file.getName()+"\n";
            str+=fileName;
            String content = "";
            for (String uri_dbpedia : results.keySet()) {
                String url_wikipedia = results.get(uri_dbpedia);
                ImageFinder ImageFinder = new ImageFinder(url_wikipedia);
               
                //System.out.println(url_wikipedia + " " + ImageFinder.getUrlResults().size());
                if(!ImageFinder.getUrlResults().isEmpty()){
                     String line = uri_dbpedia + url_wikipedia + ImageFinder.getUrlResults() + "\n";
                content += line;
                System.out.println( ImageFinder.getUrlResults());
                }
                
            }
             FileUtils.stringToFile(content, outputFile);
             FileUtils.stringToFile(str, urlDir+processFileList);
             if(fileIndex>fileLimit)
                 break;

        }
        //test test...
        /*String url_wikipedia = "https://en.wikipedia.org/wiki/Berlin";
        ImageFinder ImageFinder = new ImageFinder(url_wikipedia);
        System.out.println(url_wikipedia + " " + ImageFinder.getUrlResults().size());
        System.out.println(url_wikipedia + " " + ImageFinder.getUrlResults());*/
    }

    
    public static Map<String,String> getLinks(File file, Integer numberOfTriples) {
        Map<String,String> results=new TreeMap<String,String>();
        BufferedReader reader;
        String line = "";
        Integer lineNumber = 0;

        try {
            reader = new BufferedReader(new FileReader(file));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                lineNumber = lineNumber + 1;
                String subject = null;
                String object = null, property = null;
                if (line != null) {
                    line = line.replace("<", "\n" + "<");
                    line = line.replace(">", ">" + "\n");
                    line = line.replace("\"", "\n" + "\"");
                    String[] lines = line.split(System.getProperty("line.separator"));

                    Integer index = 0;
                    for (String value : lines) {
                        index = index + 1;
                        if (index == 2) {
                            subject =  value;
                        } else if (index == 6) {
                            object = value;
                        }
                        else if (index == 4) {
                            property =  value;
                        }
                    }
                  
                    
                    if (lineNumber == -1)
                         ; 
                    else if (lineNumber == numberOfTriples) {
                        break;
                    }                  

                    if(subject!=null&&object!=null){
                        ;
                    }
                    else continue;
                    
                   if( subject.contains("__")||object.contains("__"))
                       continue;
                   
                   subject = subject.replace("<", "").replace(">", "");
                   object = object.replace("<", "").replace(">", "");
                   
                   results.put(subject,object);

                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;

    }


}
