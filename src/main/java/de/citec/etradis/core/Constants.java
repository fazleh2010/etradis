/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author elahi
 */
public interface Constants {

    public static String URI_MEDIA = "https://commons.wikimedia.org/";
    public static String URI_UPLOAD = "https://upload.wikimedia.org/";
    public static String URI_WIKIPEDIA="http://en.wikipedia.org/wiki/";
    public static String JPG = ".jpg";
    public static String PNG = ".png";
    public static String GIF = ".gif";
    public static String HREF = ".href";
    public static String WIKIPEDIA_DIR = "../dbpedia/wikipedia/";
    public static String PROCESS_FILE_LIST = "fileProcessList.txt";
    public static String DBPEDIA_DIR = "../dbpedia/";
    public static String CLASS_ENTITIES_SPECIFIC_FILE_ = "instance-types_lang=en_specific.ttl";
    public static String CLASS_ENTITIES_TRANSITIVE_FILE = "instance-types_lang=en_transitive.ttl";

    public static String CLASS_DIR = DBPEDIA_DIR + "classDir/";
    public static String FIND_IMAGE_FROM_WIKIPEDIA = "FIND_IMAGE";
    public static String FIND_ENTITIES_FROM_CLASS = "FIND_ENTITIES_OF_CLASS";
    public static String WIKILINK_FILE_SPLIT="FILE_SPLIT";
    public static String SELECTED_ENTITIES = "SELECTED_ENTITIES";
    public static String FIND_IMAGE_FOR_SELECTED_ENTITIES="FIND_IMAGE_SELECTED_ENTITIES";
    public static String MERGE_IMAGE_WIKILINK="MERGE_CLASS_AND_WIKILINK";
    public static String MAKE_FILE_FOR_GRAPH="MAKE_FILE_FOR_GRAPH";
    public static String FIND_IMAGE_PKLE_FILE="FIND_IMAGE_PKLE_FILE";
     public static String FIND_IMAGE_GIVEN_URI_LIST="FIND_IMAGE_GIVEN_URI_LIST";
    public static String FindImageGivenUri="FindImageGivenUri";
    public static String ConvertToJson="convertToJson";

    
    public static String RESULT_DIR = "/media/elahi/My Passport/etradis/dbpedia/results/";
    public static String INPUT_DIR = "input/";
    public static String OUTPUT_DIR = "output/";
    public static String IMAGE_DIR ="images/";
    public static String SELECTED_DIR ="selected/";
    public static Set<String> FIRST_LETTER_CHECK = new HashSet<String>(Arrays.asList("A","B","C","D","E","F","G","H","I"
                                                                              ,"J","K","L","M","N","O","P","Q","R"
                                                                              ,"S","T","U","V","W","X","Y","Z"));
    public static Set<String> FIRST_LETTER_DIGIT = new HashSet<String>(Arrays.asList("0"
                                                                              ,"1","2","3","4","5","6","7","8","9"
                                                                              ,"(","%","_"));
    
    public static String VEDIO="vedio";
    public static String IMAGE="image";
    
    public static String UPLOAD_LINK="//upload.wikimedia.org/";
    public static String BRACKET_OPEN="[";
    public static String BRACKET_CLOSE="]";
    public static String NEWLINE="\n";
    public static String VEDIO_OGG=".ogg";
    public static String AUDIO_JPG= ".jpg";
    public static String AUDIO_PNG=  ".png";
    public static String HTTPS= "https:";

}
