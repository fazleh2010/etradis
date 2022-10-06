/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core;

/**
 *
 * @author elahi
 */
public interface Constants {

    public static String URI_MEDIA = "https://commons.wikimedia.org/";
    public static String URI_UPLOAD = "https://upload.wikimedia.org/";
    public static String JPG = ".jpg";
    public static String PNG = ".png";
    public static String GIF = ".gif";
    public static String HREF = ".href";
    public static String WIKIPEDIA_DIR = "/media/elahi/My Passport/etradis/dbpedia/wikipedia/";
    public static String PROCESS_FILE_LIST = "fileProcessList.txt";
    public static String DBPEDIA_DIR = "/media/elahi/My Passport/etradis/dbpedia/";
    public static String CLASS_ENTITIES_SPECIFIC_FILE_ = "instance-types_lang=en_specific.ttl";
    public static String CLASS_ENTITIES_TRANSITIVE_FILE = "instance-types_lang=en_transitive.ttl";

    public static String CLASS_DIR = DBPEDIA_DIR + "classDir/";
    public static String FIND_IMAGE_FROM_WIKIPEDIA = "FIND_IMAGE";
    public static String FIND_ENTITIES_FROM_CLASS = "FIND_ENTITIES_OF_CLASS";
    public static String SELECTED_ENTITIES = "SELECTED_ENTITIES";
    public static String FIND_IMAGE_FOR_SELECTED_ENTITIES="FIND_IMAGE_SELECTED_ENTITIES";
    public static String RESULT_DIR = "/media/elahi/My Passport/etradis/dbpedia/results/";
    public static String IMAGE_DIR =RESULT_DIR+"images/";
}
