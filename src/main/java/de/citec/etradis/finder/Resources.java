/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.finder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.citec.etradis.core.FileFolderUtils;
import de.citec.etradis.utils.Cleaner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resources {

    @JsonProperty("type")
    private String type = "";

    @JsonProperty("detail")
    private List<Resource> detail = new ArrayList<Resource>();
    
    
     public Resources() {
       
    }

    public Resources(String type, List<Resource> detail) {
        this.type = type;
        this.detail = detail;
    }

    public void setDetail(List<Resource> detail) {
        this.detail = detail;
    }

    public List<Resource> getDetail() {
        return detail;
    }
    
    public Resources(File file, Integer limit,String outputDir) {
        InputStream is;
        Set<String> ids = new TreeSet<String>();
        Integer index = 0;
        try {
            is = new FileInputStream(file);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line="";
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
                ids.add(line);
                 Resource resource=new  Resource(line);
                 this.detail.add(resource);
                if (limit == -1)
                    ; else if (index > limit) {
                    break;
                }
                index = index + 1;

            }
        } catch (Exception ex) {
            Logger.getLogger(FileFolderUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        Map<String, List<Resource>> alphabeticResources=this.findAlphabeticResources();
        for (String key : alphabeticResources.keySet()) {
            String fileName = file.getName()+"_"+key + ".json";
            List<Resource> list = alphabeticResources.get(key);
            writeObjectJson(outputDir, fileName, "baseUris", list);

        }
        
    }

    public String getType() {
        return type;
    }

  
    
    public Map<String, List<Resource>> findAlphabeticResources() {
        Map<String, List<Resource>> alphabeticResources=new TreeMap<String, List<Resource>>();
        for (Resource resource : this.detail) {
            String uri_dbpedia = resource.getUri_dbpedia();
            String firstLetter = Cleaner.findFirstWord(uri_dbpedia);
            List<Resource> list = new ArrayList<Resource>();

            if (alphabeticResources.containsKey(firstLetter)) {
                list = alphabeticResources.get(firstLetter);
                list.add(resource);
                alphabeticResources.put(firstLetter, list);
            } else {
                list.add(resource);
                alphabeticResources.put(firstLetter, list);
            }

        }
        return alphabeticResources;
    }
    
     public static void writeObjectJson(String dir, String fileName, String type, List<Resource> list) {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(dir + fileName);
        Resources resources = new Resources(type, list);

        try {
            mapper.writeValue(new File(dir + fileName), resources);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }  

}
