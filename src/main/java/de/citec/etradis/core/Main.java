/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core;

import static de.citec.etradis.core.Constants.CLASS_DIR;
import static de.citec.etradis.core.Constants.DBPEDIA_DIR;
import de.citec.etradis.finder.ImageFinder;
import static de.citec.etradis.core.Constants.URI_MEDIA;
import de.citec.etradis.core.sparql.PrepareSparqlQuery;
import de.citec.etradis.core.sparql.SparqlQuery;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import static de.citec.etradis.core.Constants.FIND_ENTITIES_FROM_CLASS;
import de.citec.etradis.utils.Cleaner;
import de.citec.etradis.utils.CommandLine;
import static java.lang.System.exit;

/**
 *
 * @author elahi
 */
public class Main implements Constants {

    public static void main(String args[]) throws IOException {
        Set<String> menus = new HashSet<String>();
        //menus.add(FIND_IMAGE);
        //menus.add(FIND_ENTITIES_OF_CLASS);
        //menus.add(SELECTED_ENTITIES);
        //menus.add(FIND_IMAGE_SELECTED_ENTITIES);
        //menus.add(FILE_SPLIT);
        String task = FIND_IMAGE_FOR_SELECTED_ENTITIES;

        Tasks(task);
    }

    private static void Tasks(String key) throws IOException {

        switch (key) {
            case FIND_IMAGE_FROM_WIKIPEDIA: {
                Integer fileIndex = 0, fileLimit = 10000, urlLimit = -1;
                String str = "";
                File[] files = new File(WIKIPEDIA_DIR).listFiles();
                imageFinds(files, fileIndex, urlLimit, str, fileLimit);
                break;
            }
            case FIND_ENTITIES_FROM_CLASS: {
                Integer urlLimit = -1;
                List<File> files = new ArrayList<File>();
                files.add(new File(DBPEDIA_DIR + CLASS_ENTITIES_TRANSITIVE_FILE));
                //files.add(new File(DBPEDIA_DIR + CLASS_ENTITIES_SPECIFIC_FILE_));
                for (File classFile : files) {
                    findEntitiesOfClass(CLASS_DIR, classFile, urlLimit);
                }
                break;
            }
            case SELECTED_ENTITIES: {
                File[] files = new File(CLASS_DIR).listFiles();
                Set<File> sortedFiles = new TreeSet<File>();
                for (File file : files) {
                    sortedFiles.add(new File(CLASS_DIR + file.getName()));
                }
                for (File sortedFile : sortedFiles) {
                    Integer urlLimit = 50000;
                    Set<String> entities = FileFolderUtils.fileToSet(sortedFile, urlLimit);
                    for (String entity : entities) {
                        FileFolderUtils.appendToFile(new File(RESULT_DIR + INPUT_DIR + sortedFile.getName()), entity);
                    }

                }
                break;
            }
            
            case WIKILINK_FILE_SPLIT: {
                File[] files = new File(WIKIPEDIA_DIR).listFiles();
                for (File file : files) {
                    Map<String, String> results = FileFolderUtils.fileToMap(file, -1);
                    for (String uri_dbpedia : results.keySet()) {
                        String uri_wikipedia = results.get(uri_dbpedia);
                        uri_dbpedia = Cleaner.cleanUrlBracket(uri_dbpedia);
                        uri_wikipedia = Cleaner.cleanUrlBracket(uri_wikipedia);
                        String firstCharacter = findFirstWord(uri_dbpedia);
                        if (firstLetter.contains(firstCharacter)) {
                            String line = uri_dbpedia + "+" + uri_wikipedia;
                            File outputFile = new File("../dbpedia/output/" + firstCharacter + ".ttl");
                            FileFolderUtils.appendToFile(outputFile, line);
                        }
                       
                    }
                }
                System.out.println("Completed!!!!");

            }
            break;
            case MERGE_IMAGE_WIKILINK: {
                Integer urlLimit = 2, fileLimit = 2;
                Integer index = 0;
                List<File> files=Initilizer.sortedFile(CLASS_DIR);

                Map<String, Map<String, String>> wikidataMap = Initilizer.getWikipediaFiles();
                Integer fileNumber=0;
                for (File file : files) {
                    fileNumber=fileNumber+1;
                    Set<String> results = FileFolderUtils.fileToSet(file, -1);
                    for (String uri_dbpedia : results) {
                        String firstCharacter = findFirstWord(uri_dbpedia);

                        if (firstLetter.contains(firstCharacter)) {
                            String uri_dbpedia_clean = Cleaner.cleanUrl(uri_dbpedia);
                            Map<String, String> checked = wikidataMap.get(firstCharacter);

                            if (checked.containsKey(uri_dbpedia_clean)) {
                                String searchFileName = "../dbpedia/output/" + firstCharacter +"_"+"class"+ ".txt";
                                String line = uri_dbpedia + "+" + checked.get(uri_dbpedia_clean) + "+" + file.getName().replace(".txt", "");
                                FileFolderUtils.appendToFile(new File(searchFileName), line);
 
                            }

                        }
                    }
                }
                break;
            }
            case FIND_IMAGE_FOR_SELECTED_ENTITIES: {
                Integer fileNumber = 0;
                String dir = Initilizer.getResourceWikipediaLinkDir();
                List<File> files = Initilizer.sortedFile(dir);

                for (File file : files) {
                    fileNumber = fileNumber + 1;
                    Map<String, String> results = FileFolderUtils.fileToMapPLus(file, -1);
                    Integer lineIndex = 0;
                    for (String uri_dbpedia : results.keySet()) {
                        String url_wikipedia = results.get(uri_dbpedia);
                        try {
                            String line = findImage(uri_dbpedia, url_wikipedia, lineIndex);
                            if (!line.isEmpty()) {
                                lineIndex = lineIndex + 1;
                                File outputFile = new File(dir + file.getName() + "_" + "image" + ".txt");
                                FileFolderUtils.appendToFile(outputFile, line);
                                System.out.println("fileName::" + file.getName() + " current::" + fileNumber + " totalFile::" + files.size() + " lineIndex::" + lineIndex + " totalLine:" + results.size());

                            }
                        } catch (Exception ex) {
                           continue;
                        }
                    }
                }

                break;
            }

            default:
                System.out.println("no menu is found!!");
        }

    }
    
    private static String findImage(String uri_dbpedia, String url_wikipedia,Integer lineIndex) throws IOException {
        ImageFinder ImageFinder = new ImageFinder(url_wikipedia);
        String content="";

        if (!ImageFinder.getImagesUris().isEmpty()) {
            content= lineIndex+"+"+uri_dbpedia +"+"+ url_wikipedia +"+"+ ImageFinder.getImagesUris();
          
        }
        return content;
    }

    /*private static String imageFindFromFile(File file, Integer urlLimit) throws IOException {
        String content = "";

        Set<String> results = FileFolderUtils.fileToSet(file, -1);
        Integer index = 0;
        for (String uri_dbpedia : results) {
            String url_wikipedia = findWikipediaLink(uri_dbpedia, WIKIPEDIA_DIR + OUTPUT_DIR + firstCharacter + ".ttl");
            ImageFinder ImageFinder = new ImageFinder(url_wikipedia);

            if (!ImageFinder.getUrlResults().isEmpty()) {
                String line = uri_dbpedia + url_wikipedia + ImageFinder.getUrlResults() + "\n";
                content += line;
            }
            if (urlLimit == -1) {
                ;
            } else if (index > urlLimit) {
                break;
            }
            index = index + 1;

        }
        return content;
    }*/
    
    private static void imageFinds(File[] files, Integer fileIndex, Integer urlLimit, String str, Integer fileLimit) throws IOException {
        for (File file : files) {
            fileIndex = fileIndex + 1;
            String outputFile = null;
            if (file.getName().contains("wikipedia-links_lang=en.ttl.z")) {
                outputFile = file.getAbsolutePath() + file.getName().replace(".ttl", ".txt");
            } else {
                continue;
            }
            Map<String, String> results = getLinks(file, urlLimit);
            String fileName = file.getName() + "\n";
            str += fileName;
            String content = "";
            for (String uri_dbpedia : results.keySet()) {
                String url_wikipedia = results.get(uri_dbpedia);
                ImageFinder ImageFinder = new ImageFinder(url_wikipedia);

                //System.out.println(url_wikipedia + " " + ImageFinder.getUrlResults().size());
                if (!ImageFinder.getImagesUris().isEmpty()) {
                    String line = uri_dbpedia + url_wikipedia + ImageFinder.getImagesUris() + "\n";
                    content += line;
                }

            }
            FileFolderUtils.stringToFile(content, new File(outputFile));
            FileFolderUtils.stringToFile(str, new File(WIKIPEDIA_DIR + PROCESS_FILE_LIST));
            if (fileIndex > fileLimit) {
                break;
            }

        }
        //test test...
        /*String url_wikipedia = "https://en.wikipedia.org/wiki/Berlin";
        ImageFinder ImageFinder = new ImageFinder(url_wikipedia);
        System.out.println(url_wikipedia + " " + ImageFinder.getUrlResults().size());
        System.out.println(url_wikipedia + " " + ImageFinder.getUrlResults());*/
    }

    public static Map<String, String> getLinks(File file, Integer numberOfTriples) {
        Map<String, String> results = new TreeMap<String, String>();
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
                            subject = value;
                        } else if (index == 6) {
                            object = value;
                        } else if (index == 4) {
                            property = value;
                        }
                    }

                    if (lineNumber == -1)
                         ; else if (lineNumber == numberOfTriples) {
                        break;
                    }

                    if (subject != null && object != null) {
                        ;
                    } else {
                        continue;
                    }

                    if (subject.contains("__") || object.contains("__")) {
                        continue;
                    }

                    subject = subject.replace("<", "").replace(">", "");
                    object = object.replace("<", "").replace(">", "");

                    results.put(subject, object);

                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;

    }

    public static void findEntitiesOfClass(String outputDir, File inputFile, Integer numberOfTriples) {
        Set<String> results = new TreeSet<String>();
        BufferedReader reader;
        String line = "";
        Integer lineNumber = 0;

        try {
            reader = new BufferedReader(new FileReader(inputFile));
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
                            subject = value;
                        } else if (index == 6) {
                            object = value;
                        } else if (index == 4) {
                            property = value;
                        }
                    }

                    if (lineNumber == -1)
                         ; else if (lineNumber == numberOfTriples) {
                        break;
                    }

                    if (subject != null && object != null) {
                        ;
                    } else {
                        continue;
                    }

                    if (subject.contains("__") || object.contains("__")) {
                        continue;
                    }

                    object = clean(object);
                    String objectFileName = outputDir + object + ".txt";
                    File objectFile = new File(objectFileName);
                    subject = cleanCharacter(subject);
                    FileFolderUtils.appendToFile(objectFile, subject);

                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String clean(String object) {
        object = object.replace("<", "").replace(">", "");
        object = object.replace("#", "_");
        Path path = Paths.get(object);
        object = path.getFileName().toString();
        return object;
    }

    private static String cleanCharacter(String object) {
        object = object.replace("<", "").replace(">", "");
        return object;
    }

    /*if (menus.contains(FIND_IMAGE)) {
        Integer fileIndex = 0, fileLimit = 10000, urlLimit = -1;
        String str = "";
        File[] files = new File(WIKIPEDIA_DIR).listFiles();
        imageFinds(files, fileIndex, urlLimit, str, fileLimit);
        }
        if (menus.contains(FIND_ENTITIES_OF_CLASS)) {
        Integer urlLimit = -1;
        List<File> files=new ArrayList<File>();
        files.add(new File(DBPEDIA_DIR + CLASS_ENTITIES_TRANSITIVE_FILE));
        //files.add(new File(DBPEDIA_DIR + CLASS_ENTITIES_SPECIFIC_FILE_));
        for(File classFile:files){
        findEntitiesOfClass(CLASS_DIR, classFile, urlLimit);
        }
        
        }
        if (menus.contains(SELECTED_ENTITIES)) {
        File[] files = new File(CLASS_DIR).listFiles();
        Set<File> sortedFiles = new TreeSet<File>();
        for (File file : files) {
        sortedFiles.add(new File(CLASS_DIR + file.getName()));
        }
        for (File sortedFile : sortedFiles) {
        Integer urlLimit =-1;
        System.out.println(sortedFile);
        Set<String> entities =FileFolderUtils.fileToSet(sortedFile,urlLimit);
        System.out.println(entities.size());
        for(String entity:entities){
        FileFolderUtils.appendToFile(new File(RESULT_DIR+SELECTED_ENTITIES_FILE), entity);
        }
        
        }
        }
        if (menus.contains(FIND_IMAGE_SELECTED_ENTITIES)) {
        Integer fileIndex = 0, fileLimit = 10000, urlLimit = -1;
        String str = "";
        File file = new File(RESULT_DIR + SELECTED_ENTITIES_FILE);
        File outputFile = new File(RESULT_DIR + SELECTED_ENTITIES_FILE.replace(".txt", "Image.txt"));
        String content = imageFindFromFile(file, fileIndex, urlLimit, str, fileLimit);
        FileFolderUtils.stringToFile(content, outputFile);
        }*/
    //SparqlQuery sparqlQuery =new SparqlQuery(sparqlQuery);
    private static String findFirstWord(String uri_dbpedia) {
        uri_dbpedia = uri_dbpedia.replace("<", "").replace(">", "");
        uri_dbpedia = uri_dbpedia.replace("http://dbpedia.org/resource/", "");
        if (uri_dbpedia.length() > 1) {
            return "" + uri_dbpedia.charAt(0);
        }
        return "" + '_';
    }

   
    /*try {
                                if(CommandLine.execute(uri_dbpedia, searchFileName, tempFileName)){
                                    
                                }
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            }*/
;
//String line=uri_dbpedia+"+"+url_wikipedia;
//File outputFile=new File(CLASS_DIR+ file.getName()+".ttl");
//FileFolderUtils.appendToFile(outputFile, line);
    
    /*for (File sortedFile : sortedFiles) {
                    String fileName = sortedFile.getName().replace(".txt", "Image.txt");
                    File outputFile = new File(IMAGE_DIR + fileName);
                    String content = imageFindFromFile(sortedFile, urlLimit);
                    FileFolderUtils.stringToFile(content, outputFile);
                    if (fileLimit == -1) {
                        ;
                    } else if (index > fileLimit) {
                        break;
                    }
                    index = index + 1;
                }*/
}
