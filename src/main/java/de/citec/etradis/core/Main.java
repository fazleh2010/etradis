/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author elahi
 */
public class Main {

    public static String URI_MEDIA = "https://commons.wikimedia.org/";

    public static void main(String args[]) {
        String url_wikipedia = "https://en.wikipedia.org/wiki/Berlin";
        LinkedHashSet<String> imageList = findFirstLink(URI_MEDIA, url_wikipedia);
         Set<String> results = new HashSet<String>();
        Map<String, Set<String>> urlResults = new TreeMap<String, Set<String>>();
        for (String imageUri : imageList) {
            Set<String> sortedImageList = findExactLink(imageUri);
           
            for (String sortedImageUri : sortedImageList) {
                if (ignoreLink(sortedImageUri, ".jpg") < 2) {
                    results.add(sortedImageUri);
                }
            }
        }
        urlResults.put(url_wikipedia, results);
        
        System.out.println(url_wikipedia+" "+results.size());

    }

    private static LinkedHashSet<String> findFirstLink(String URI_MEDIA, String url) {
        Document document;
        LinkedHashSet<String> imageList = new LinkedHashSet<String>();

        try {
            document = Jsoup.connect(url).get();
            Elements links = document.select("a[href]");
            for (Element link : links) {
                if (link.toString().contains(".jpg")
                        || link.toString().contains(".png")
                        || link.toString().contains(".gif")) {
                    String linkStr = link.attr("href");
                    if (linkStr.endsWith(".jpg")) {
                        imageList.add(URI_MEDIA + linkStr);
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageList;
    }

    private static Set<String> findExactLink(String url) {
        Document document;
        Set<String> imageList = new HashSet<String>();

        try {

            document = Jsoup.connect(url).get();
            Elements links = document.select("a[href]");
            for (Element link : links) {
                if (link.toString().contains(".jpg")
                        || link.toString().contains(".png")
                        || link.toString().contains(".gif")) {
                    String linkStr = link.attr("href");
                    if (linkStr.startsWith("https://upload.wikimedia.org/") && linkStr.endsWith(".jpg")) {
                        imageList.add(linkStr);
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageList;
    }
    
    private static Integer ignoreLink(String string, String patternStr) {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(string);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    

}
