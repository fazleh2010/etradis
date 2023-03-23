/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
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
import de.citec.etradis.finder.Resource;
import de.citec.etradis.finder.Resources;
import de.citec.etradis.finder.VedioFinder;
import de.citec.etradis.utils.Cleaner;
import de.citec.etradis.utils.CommandLine;
import de.citec.etradis.utils.CsvFile;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.exit;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import org.apache.logging.log4j.core.util.FileUtils;

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
        String task = FIND_IMAGE_PKLE_FILE;
        //task = FindImageGivenUri;
        //task =ConvertToJson;
        task = FIND_IMAGE_GIVEN_URI_LIST;

        Tasks(task);
    }

    private static void Tasks(String key) throws IOException {

        switch (key) {
            case FIND_IMAGE_GIVEN_URI_LIST: {
                String uriFile = "src/main/resources/uri.txt";
                String outputFileName="/home/elahi/A-etardis/resources/image.csv";
                String[] header = new String[3];
                
                LinkedHashMap<String,String> uris = FileFolderUtils.getUris(uriFile);
                CSVWriter csvWriterQuestions = new CSVWriter(new FileWriter(outputFileName, true));
                //csvWriterQuestions.writeNext(new String[]{"DBpediaUri","Image","AllImages"});
                Integer limit=uris.size(); Integer index=0;
                for (String url_dbpedia : uris.keySet()) {
                    String[]row=new String[3];
                    String url_wikipedia=uris.get(url_dbpedia);
                    ImageFinder imageFinder = new ImageFinder(url_wikipedia);
                    if (!imageFinder.getImagesUris().isEmpty()) {
                        row[0]=url_dbpedia;
                        row[1]=imageFinder.getImagesUris().iterator().next();
                        row[2]=imageFinder.getImagesUris().toString();
                    }
                    else{
                        row[0]=url_dbpedia;
                        row[1]="X";
                        row[2]="X";    
                    }
                    System.out.println("limit::"+limit+" index::"+index+" "+row[0]+" "+url_wikipedia+" "+row[1]+" "+imageFinder.getImagesUris().size());
                    csvWriterQuestions.writeNext(row);
                    index=index+1;                    
                }
                csvWriterQuestions.close();
                //https://en.wikipedia.org/wiki/LGBT_movements
                break;
            }
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
                List<File> files = Initilizer.sortedFile(CLASS_DIR);
                Integer fileIndex = 0;
                for (File file : files) {
                    System.out.println(file.getName());
                    fileIndex = fileIndex + 1;

                    Integer urlLimit = 50000, lineIndex = 0;
                    /*if (!file.getName().contains(".txt")) {
                        continue;
                    }*/
                    Set<String> entities = FileFolderUtils.fileToSet(file, urlLimit);
                    for (String uri_dbpedia : entities) {
                        lineIndex = lineIndex + 1;
                        File outputFile = new File(CLASS_DIR + SELECTED_DIR + file.getName());
                        FileFolderUtils.appendToFile(outputFile, uri_dbpedia);
                        System.out.println(file.getName() + " fileNo:" + fileIndex + " totalFile:" + files.size() + " lineNo:" + lineIndex + " Limit:" + urlLimit);
                    }
                }

                /*File[] files = new File(CLASS_DIR).listFiles();
                Set<File> sortedFiles = new TreeSet<File>();
                for (File file : files) {
                    sortedFiles.add(new File(CLASS_DIR + file.getName()));
                }
                Integer fileIndex=0;
                for (File sortedFile : sortedFiles) {
                    fileIndex=fileIndex+1;
                    Integer urlLimit = 50000, lineIndex =0;
                    if(!sortedFile.getName().contains(".txt"))
                       continue;
                    Set<String> entities = FileFolderUtils.fileToSet(sortedFile, urlLimit);
                    for (String uri_dbpedia : entities) {
                         String firstLetter=findFirstWord(uri_dbpedia);
                         lineIndex = lineIndex + 1;
                        if (!FIRST_LETTER_CHECK.contains(firstLetter)) {
                            continue;
                        }
                        
                        String url_wikipedia= findWikipediaUri(uri_dbpedia);
                         
                         try {
                            String line = findImage(uri_dbpedia, url_wikipedia, lineIndex);
                            if (!line.isEmpty()) {
                                
                                File outputFile = new File(CLASS_DIR + "images/" + sortedFile.getName() + ".txt");
                                FileFolderUtils.appendToFile(outputFile,line);
                                System.out.println("currentFile::"+fileIndex+" totalFile::" + sortedFiles.size()+" fileName:" +sortedFile.getName()+ " currentLine::" + lineIndex+" totalLInes::"+urlLimit);
                                System.out.println( " line::" + line);

                            }
                        } catch (Exception ex) {
                           continue;
                        }
                        //FileFolderUtils.appendToFile(new File(RESULT_DIR + INPUT_DIR + sortedFile.getName()), entity);
                    }

                }*/
                break;
            }
            case FIND_IMAGE_FOR_SELECTED_ENTITIES: {
                List<File> sortedFiles = Initilizer.sortedFile(CLASS_DIR + SELECTED_DIR);
                Integer fileIndex = 0, total = 0;

                for (File sortedFile : sortedFiles) {
                    if (!sortedFile.getName().startsWith("B")) {
                        continue;
                    }
                    fileIndex = fileIndex + 1;
                    Integer urlLimit = -1, lineIndex = 0;
                    if (!sortedFile.getName().contains(".txt")) {
                        continue;
                    }
                    Set<String> entities = FileFolderUtils.fileToSet(sortedFile, urlLimit);
                    for (String uri_dbpedia : entities) {
                        String firstLetter = Cleaner.findFirstWord(uri_dbpedia);
                        lineIndex = lineIndex + 1;
                        if (!FIRST_LETTER_CHECK.contains(firstLetter)) {
                            continue;
                        }

                        String url_wikipedia = findWikipediaUri(uri_dbpedia);

                        try {
                            String line = findImage(uri_dbpedia, url_wikipedia, lineIndex);
                            if (!line.isEmpty()) {
                                total = total + 1;
                                File outputFile = new File(CLASS_DIR + IMAGE_DIR + sortedFile.getName());
                                FileFolderUtils.appendToFile(outputFile, line);
                                System.out.println("currentFile::" + fileIndex + " totalFile::" + sortedFiles.size() + " fileName:" + sortedFile.getName() + " currentLine::" + lineIndex + " totalLInes::" + urlLimit);
                                System.out.println(" line::" + line);

                            }
                        } catch (Exception ex) {
                            continue;
                        }
                    }

                }
                System.out.println("Completed!! total:" + total);
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
                        String firstCharacter = Cleaner.findFirstWord(uri_dbpedia);
                        if (FIRST_LETTER_CHECK.contains(firstCharacter)) {
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
                List<File> files = Initilizer.sortedFile(CLASS_DIR);

                Map<String, Map<String, String>> wikidataMap = Initilizer.getWikipediaFiles();
                Integer fileNumber = 0;
                for (File file : files) {
                    fileNumber = fileNumber + 1;
                    Set<String> results = FileFolderUtils.fileToSet(file, -1);
                    for (String uri_dbpedia : results) {
                        String firstCharacter = Cleaner.findFirstWord(uri_dbpedia);

                        if (FIRST_LETTER_CHECK.contains(firstCharacter)) {
                            String uri_dbpedia_clean = Cleaner.cleanUrl(uri_dbpedia);
                            Map<String, String> checked = wikidataMap.get(firstCharacter);

                            if (checked.containsKey(uri_dbpedia_clean)) {
                                String searchFileName = "../dbpedia/output/" + firstCharacter + "_" + "class" + ".txt";
                                String line = uri_dbpedia + "+" + checked.get(uri_dbpedia_clean) + "+" + file.getName().replace(".txt", "");
                                FileFolderUtils.appendToFile(new File(searchFileName), line);

                            }

                        }
                    }
                }
                break;
            }
            case FIND_IMAGE_PKLE_FILE: {
                String dir = "/media/elahi/My Passport/etradis/dbpedia/connections/usecases/";
                File file = new File(dir);
                File[] sortedFiles = file.listFiles();
                Integer limit = 5;
                Map<String, List<Resource>> resources = new TreeMap<String, List<Resource>>();
                for (File sortedFile : sortedFiles) {
                    resources = findImageVedioUseCases(sortedFile, limit, resources);
                }
                for (String firstLetter : resources.keySet()) {
                    List<Resource> list = resources.get(firstLetter);
                    String fileName = firstLetter + ".json";
                    System.out.println(fileName + " " + list.size());
                    Resources.writeObjectJson(dir, fileName, "usecases", list);
                }

            }
            break;
            case FindImageGivenUri: {
                String dir = "/media/elahi/My Passport/etradis/dbpedia/connections/usecases/";
                dir = "/media/elahi/My Passport/etradis/dbpedia/classDir/images/json/";
                String givenUri = "http://dbpedia.org/resource/Alec_Coryton";
                givenUri = "http://dbpedia.org/resource/Acolon";

                Resource resource = findDetailOfResource(dir, givenUri);
                if (resource != null) {
                    System.out.println(resource);
                }

            }
            break;
            case ConvertToJson: {
                String inputDir = "/media/elahi/My Passport/etradis/dbpedia/classDir/images/";

                File file = new File(inputDir);
                File[] files = file.listFiles();
                Integer limit = -1;
                for (File fileTemp : files) {
                    if (fileTemp.getName().contains(".txt")) {
                        String outputDir = fileTemp.getName().split(".txt")[0];
                        outputDir = inputDir + "json/";
                        System.out.println(outputDir);
                        Resources resources = new Resources(new File(fileTemp.getAbsolutePath()), limit, outputDir);
                    }

                }

            }
            break;
            default:
                System.out.println("no menu is found!!");
        }

    }

    private static Resource findDetailOfResource(String dir, String givenUri) throws IOException {

        File file = new File(dir);
        File[] sortedFiles = file.listFiles();
        String firstLetterGiven = Cleaner.findFirstWord(givenUri);

        for (File sortedFile : sortedFiles) {
            if (sortedFile.getName().contains(".json") && sortedFile.getName().contains(firstLetterGiven)) {
                ObjectMapper mapper = new ObjectMapper();
                Resources resources = mapper.readValue(new File(dir + sortedFile.getName()), Resources.class);
                List<Resource> resourceList = resources.getDetail();

                for (Resource resource : resourceList) {
                    if (resource.getUri_dbpedia().contains(givenUri)) {
                        return resource;
                    }

                }
            }
        }
        return null;
    }

    private static void findImageVedioUrisBig(String dir, String type) {
        List<Resource> resources = new ArrayList<Resource>();

        File file = new File(dir);
        File[] sortedFiles = file.listFiles();
        Integer fileIndex = 0, lineIndex = 0, urlLimit = 0;
        Set<String> duplicateCheck = new TreeSet<String>();
        for (File sortedFile : sortedFiles) {
            fileIndex = fileIndex + 1;
            Set<String> results = FileFolderUtils.getLinesFromPikleFile(sortedFile, "http://dbpedia.org/resource/");
            urlLimit = results.size();
            lineIndex = 0;
            for (String uri_dbpedia : results) {
                //uri_dbpedia="http://dbpedia.org/resource/Adolf_Hitler";
                String url_wikipedia = findWikipediaUri(uri_dbpedia);
                String firstLetter = Cleaner.findFirstWord(uri_dbpedia);
                try {
                    String line = "";
                    /*if(type.contains(VEDIO)){
                      line = findVedio(uri_dbpedia, url_wikipedia, lineIndex);
                    }
                    else if(type.contains(IMAGE)){
                       line = findImage(uri_dbpedia, url_wikipedia, lineIndex);  
                    }*/

                    //Resource resource = findImageVedio(uri_dbpedia, url_wikipedia, lineIndex, resource);
                    //resources.add(resource);

                    /*lineIndex = lineIndex + 1;
                    if (!line.isEmpty()) {
                        if (!duplicateCheck.contains(url_wikipedia)) {
                            File outputFile = new File(dir + firstLetter+"_"+ type+ ".txt");
                            FileFolderUtils.appendToFile(outputFile, line);
                            System.out.println("currentFile::" + fileIndex + " totalFile::" + sortedFiles.length + " fileName:" + sortedFile.getName() + " currentLine::" + lineIndex + " totalLInes::" + urlLimit);
                            System.out.println(" line::" + line);
                            duplicateCheck.add(url_wikipedia);
                        }
                        
                    }*/
                } catch (Exception ex) {
                    continue;
                }
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        File useCaseFile = new File(dir + "usecases.json");
        try {
            // Serialize Java object info JSON file.
            mapper.writeValue(useCaseFile, resources);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, List<Resource>> findImageVedioUseCases(File sortedFile, Integer limit, Map<String, List<Resource>> resources) {
        Integer fileIndex = 0, lineIndex = 0, urlLimit = 0;
        Set<String> results = FileFolderUtils.getLinesFromPikleFile(sortedFile, "http://dbpedia.org/resource/");
        urlLimit = results.size();
        lineIndex = 0;
        for (String uri_dbpedia : results) {
            lineIndex = lineIndex + 1;
            String url_wikipedia = findWikipediaUri(uri_dbpedia);
            Resource resource = new Resource(uri_dbpedia, url_wikipedia);
            String firstLetter = Cleaner.findFirstWord(uri_dbpedia);
            List<Resource> list = new ArrayList<Resource>();

            if (resources.containsKey(firstLetter)) {
                list = resources.get(firstLetter);
                list.add(resource);
                resources.put(firstLetter, list);
            } else {
                list.add(resource);
                resources.put(firstLetter, list);
            }


            /*if ((!resource.getImageUris().isEmpty()) || (!resource.getVedioUris().isEmpty())) {
                resources.add(resource);
            }*/
        }
        return resources;
    }

    private static Resource findImageVedio(String uri_dbpedia, String url_wikipedia, Integer lineIndex, Resource resource) {
        ImageFinder ImageFinder = new ImageFinder(url_wikipedia);
        VedioFinder vedioFinder = new VedioFinder(url_wikipedia);

        LinkedHashSet<String> imagesUris = ImageFinder.getImagesUris();
        LinkedHashSet<String> vedioUris = vedioFinder.getVedioLinks();

        if (!ImageFinder.getImagesUris().isEmpty()) {
            String mainImageUri = ImageFinder.getImagesUris().iterator().next();
            resource.setMainImageUri(mainImageUri);
            resource.setImageUris(imagesUris);
        }
        if (!vedioFinder.getVedioLinks().isEmpty()) {
            String mainVedioUri = vedioFinder.getVedioLinks().toString();
            resource.setMainImageUri(mainVedioUri);
            resource.setImageUris(vedioUris);
        }
        return resource;
    }

    private static String findImage(String uri_dbpedia, String url_wikipedia, Integer lineIndex) throws IOException {
        ImageFinder ImageFinder = new ImageFinder(url_wikipedia);

        String content = "", imageString = "", vedioString = "";

        if (!ImageFinder.getImagesUris().isEmpty()) {
            imageString = ImageFinder.getImagesUris().toString();
            content = lineIndex + "+" + uri_dbpedia + "+" + url_wikipedia + "+" + imageString;

        }

        return content;
    }

    private static String findVedio(String uri_dbpedia, String url_wikipedia, Integer lineIndex) throws IOException {
        VedioFinder vedioFinder = new VedioFinder(url_wikipedia);
        String content = "", vedioString = "";
        if (!vedioFinder.getVedioLinks().isEmpty()) {
            vedioString = vedioFinder.getVedioLinks().toString();
            content = lineIndex + "+" + uri_dbpedia + "+" + url_wikipedia + "+" + vedioString;
            //System.out.println(content);
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

    private static String findWikipediaUri(String entity) {
        return entity.replace("http://dbpedia.org/resource/", "http://en.wikipedia.org/wiki/");
    }

}
