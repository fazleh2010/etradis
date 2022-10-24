/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.finder;

import de.citec.etradis.core.Constants;
import de.citec.etradis.core.FileFolderUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author elahi
 */
public class VedioFinder implements Constants{
   //https://upload.wikimedia.org/wikipedia/en/2/27/Adolf_Hitler_at_Berchtesgaden.ogg
    private  static Set<String> VEDIO_EXTENSIONS=new TreeSet<String>();

    private  LinkedHashSet<String> vedioLinks = new LinkedHashSet<String>();
   


    public VedioFinder(String urlString) {
        VEDIO_EXTENSIONS.add(".webm");
        VEDIO_EXTENSIONS.add(".WEBM");
        VEDIO_EXTENSIONS.add(".ogg");
        VEDIO_EXTENSIONS.add(".OGG");
        VEDIO_EXTENSIONS.add(".mp4");
        VEDIO_EXTENSIONS.add(".MP4");
        VEDIO_EXTENSIONS.add(".mov");
        VEDIO_EXTENSIONS.add(".MOV");
        VEDIO_EXTENSIONS.add(".wmv");
        VEDIO_EXTENSIONS.add(".WMV");
        VEDIO_EXTENSIONS.add(".avi");
        VEDIO_EXTENSIONS.add(".AVI");
        VEDIO_EXTENSIONS.add(".avchd");
        VEDIO_EXTENSIONS.add(".AVCHD");
        VEDIO_EXTENSIONS.add(".mkv");
        VEDIO_EXTENSIONS.add(".MKV");
        vedioLinks = new LinkedHashSet<String>();

        try {
            //is = new FileInputStream(fileName);
            urlString=urlString.replace("http:","https:");
            URL url=new URL (urlString);
            //System.out.println("urlString::"+urlString);

            InputStream is = url.openStream();
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = buf.readLine()) != null) {
                if (line.contains(UPLOAD_LINK)) {
                    line = line.replace(UPLOAD_LINK, NEWLINE + BRACKET_OPEN + UPLOAD_LINK);
                    if (isImage(line)) {
                        continue;
                    }

                    if (isVedioLink(line)) {
                        line = filterVedioLink(line);
                        line = addPrefix(StringUtils.substringBetween(line, BRACKET_OPEN, BRACKET_CLOSE));
                        vedioLinks.add(line);
                    }

                }
            }

        } catch (Exception ex) {
            Logger.getLogger(FileFolderUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Boolean isVedioLink(String line) {
        for (String extension : VEDIO_EXTENSIONS) {
            if (line.contains(extension)) {
                return true;
            }
        }

        return false;
    }

    private String filterVedioLink(String line) {
        for (String extension : VEDIO_EXTENSIONS) {
            if (line.contains(extension)) {
                line = line.replace(extension, extension + BRACKET_CLOSE + NEWLINE);
                line = line.replace(VEDIO_OGG, VEDIO_OGG + BRACKET_CLOSE + NEWLINE);
            }
        }

        return line;
    }

    private  Boolean isImage(String line) {
        if (line.contains(AUDIO_JPG) || line.contains(AUDIO_PNG)) {
            return true;
        }
        return false;
    }

    private  String addPrefix(String line) {
        return HTTPS + line;
    }

    public  LinkedHashSet<String> getVedioLinks() {
        return vedioLinks;
    }

   
    static public void main(String[] argv) throws Exception {
        File fileName = new File("src/main/resources/Test.html");
        String urlString="https://en.wikipedia.org/wiki/Adolf_Hitler";
        VedioFinder vedioFinder = new VedioFinder(urlString);

        Integer index = 0;
        for (String line : vedioFinder.getVedioLinks()) {
            index = index + 1;
            System.out.println(line);

        }

    }

}
