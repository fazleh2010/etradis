/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.finder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource {

    @JsonProperty("uri_dbpedia")
    private String uri_dbpedia = null;
    @JsonProperty("url_wikipedia")
    private String url_wikipedia = null;
    @JsonProperty("mainImageUri")
    private String mainImageUri = null;
    @JsonProperty("mainVedioUri")
    private String mainVedioUri = null;
    @JsonProperty("imageUris")
    private LinkedHashSet<String> imageUris = new LinkedHashSet<String>();
    @JsonProperty("vedioUris")
    private LinkedHashSet<String> vedioUris = new LinkedHashSet<String>();
    
    private String []column=new String[4];

    public Resource() {
    }
    
    public Resource(String line) {
        this.column = line.split("\\+");
        if (this.column.length>= 2) {
            this.uri_dbpedia = this.column[1];
        }
        if (this.column.length >= 3) {
            this.url_wikipedia = this.column[2];
        }
        if (this.column.length >= 4) {
            this.setImages(this.column[3]);
        }
        if (this.column.length >= 5) {
            this.setImages(this.column[4]);
        }
    }
    

 
    public Resource(String uri_dbpedia, String url_wikipedia) {
        this.uri_dbpedia = uri_dbpedia;
        this.url_wikipedia = url_wikipedia;
        this.imageUris = new ImageFinder(url_wikipedia).getImagesUris();
        this.vedioUris = new VedioFinder(url_wikipedia).getVedioLinks();

        if (!this.imageUris.isEmpty()) {
            this.mainImageUri = this.imageUris.iterator().next();
        }
        if (!this.vedioUris.isEmpty()) {
            this.mainVedioUri = this.vedioUris.iterator().next();
        }

    }

    public void setUri_dbpedia(String uri_dbpedia) {
        this.uri_dbpedia = uri_dbpedia;
    }

    public void setUrl_wikipedia(String url_wikipedia) {
        this.url_wikipedia = url_wikipedia;
    }

    public void setMainImageUri(String mainImageUri) {
        this.mainImageUri = mainImageUri;
    }

    public void setMainVedioUri(String mainVedioUri) {
        this.mainVedioUri = mainVedioUri;
    }

    public void setImageUris(LinkedHashSet<String> imageUris) {
        this.imageUris = imageUris;
    }

    public void setVedioUris(LinkedHashSet<String> vedioUris) {
        this.vedioUris = vedioUris;
    }

   

    public String getUri_dbpedia() {
        return uri_dbpedia;
    }

    public String getUrl_wikipedia() {
        return url_wikipedia;
    }

    public String getMainImageUri() {
        return mainImageUri;
    }

    public String getMainVedioUri() {
        return mainVedioUri;
    }

    public LinkedHashSet<String> getImageUris() {
        return imageUris;
    }

    public LinkedHashSet<String> getVedioUris() {
        return vedioUris;
    }


    @Override
    public String toString() {
        return "Resource{" + "uri_dbpedia=" + uri_dbpedia + ", url_wikipedia=" + url_wikipedia + ", mainImageUri=" + mainImageUri + ", mainVedioUri=" + mainVedioUri + ", imageUris=" + imageUris + ", vedioUris=" + vedioUris + '}';
    }

    private void setImages(String string) {
        string=string.replace("[","");
        string=string.replace("]","");
        String[]info=string.split(",");
        Integer index=0;
        for(String element:info){
            element=element.stripLeading().stripTrailing().strip().trim();

            if(index==0){
                this.mainImageUri=element;
            }
            this.imageUris.add(element);
             index=index+1;
        }
    }

}
