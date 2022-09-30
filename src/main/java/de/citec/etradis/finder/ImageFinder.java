/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.finder;

import static de.citec.etradis.core.Constants.URI_MEDIA;
import static de.citec.etradis.core.Constants.URI_UPLOAD;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author elahi
 */
public class ImageFinder {

    private Map<String, Set<String>> urlResults = new TreeMap<String, Set<String>>();

    public ImageFinder(String url_wikipedia) {
        findImages(url_wikipedia);
    }

    private void findImages(String url_wikipedia) {
        LinkedHashSet<String> imageList = ImageFinder.findFirstLink(URI_MEDIA, url_wikipedia);
        Set<String> results = new HashSet<String>();
        for (String imageUri : imageList) {
            Set<String> sortedImageList = ImageFinder.findExactLink(imageUri);

            for (String sortedImageUri : sortedImageList) {
                if (ImageFinder.ignoreLink(sortedImageUri, ".jpg") < 2) {
                    results.add(sortedImageUri);
                }
            }
        }
        if (!results.isEmpty()) {
            this.urlResults.put(url_wikipedia, results);
        }
    }

    public static LinkedHashSet<String> findFirstLink(String URI_MEDIA, String url) {
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

    public static Set<String> findExactLink(String url) {
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
                    if (linkStr.startsWith(URI_UPLOAD) && linkStr.endsWith(".jpg")) {
                        imageList.add(linkStr);
                    }

                }
            }

        } catch (Exception e) {
            //e.printStackTrace();
            return new HashSet<String>();
        }
        return imageList;
    }

    public static Integer ignoreLink(String string, String patternStr) {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(string);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    public Map<String, Set<String>> getUrlResults() {
        return urlResults;
    }

}
