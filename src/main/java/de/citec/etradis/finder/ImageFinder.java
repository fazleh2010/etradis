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
import java.util.TreeSet;
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

    private LinkedHashSet<String> imagesUris = new LinkedHashSet<String>();
    private LinkedHashSet<String> audioUris = new LinkedHashSet<String>();
    private LinkedHashSet<String> vedioUris = new LinkedHashSet<String>();

    public ImageFinder(String url_wikipedia) {
        findImages(url_wikipedia);
    }

    private void findImages(String url_wikipedia) {
        LinkedHashSet<String> imageList = ImageFinder.findFirstLink(URI_MEDIA, url_wikipedia);
        for (String imageUri : imageList) {
            this.findLinks(imageUri);
            /*for (String sortedImageUri : sortedImageList) {
                if (ImageFinder.ignoreLink(sortedImageUri, ".jpg") < 2) {
                    imagesUris.add(sortedImageUri);
                }
            }*/
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

    public void findLinks(String url) {
        Document document;

        try {

            document = Jsoup.connect(url).get();
            Elements links = document.select("a[href]");
            for (Element link : links) {
                if (link.toString().contains(".jpg")
                        || link.toString().contains(".png")
                        || link.toString().contains(".gif")) {
                    String linkStr = link.attr("href");
                    if (linkStr.startsWith(URI_UPLOAD) && linkStr.endsWith(".jpg")) {
                        imagesUris.add(linkStr);
                    }

                }

                /*if (link.toString().contains(".webm")
                        || link.toString().contains(".wmv")
                        || link.toString().contains(".mov")) {
                    String linkStr = link.attr("href");
                   
                        vedioUris.add(linkStr);
                }*/
            }

        } catch (Exception e) {
            //e.printStackTrace();
            return;
        }
        return;
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

    public Set<String> getImagesUris() {
        return imagesUris;
    }

    public Set<String> getAudioUris() {
        return audioUris;
    }

    public Set<String> getVedioUris() {
        return vedioUris;
    }

    /*public static void main(String[] args) {
        String uri = "https://en.wikipedia.org/wiki/Berlin";
        ImageFinder imageFinder = new ImageFinder(uri);
        System.out.println("images:" + imageFinder.getImagesUris());
        System.out.println("vedios:" + imageFinder.getVedioUris().size());
    }*/

}
