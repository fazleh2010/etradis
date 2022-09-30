/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core;

import de.citec.etradis.finder.ImageFinder;
import static de.citec.etradis.core.Constants.URI_MEDIA;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
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
public class Main implements Constants {

    public static void main(String args[]) throws IOException {
        Set<String> menus = new HashSet<String>();
        //menus.add(FIND_IMAGE);
        menus.add(FIND_ENTITIES_OF_CLASS);

        if (menus.contains(FIND_IMAGE)) {
            Integer fileIndex = 0, fileLimit = 10000, urlLimit = -1;
            String str = "";
            File[] files = new File(WIKIPEDIA_DIR).listFiles();
            imageFinds(files, fileIndex, urlLimit, str, fileLimit);
        }
        if (menus.contains(FIND_ENTITIES_OF_CLASS)) {
            Integer urlLimit = -1;
            File classFile = new File(DBPEDIA_DIR + CLASS_ENTITIES_FILE);
            Set<String> entities = findEntitiesOfClass(CLASS_DIR, classFile, urlLimit);
        }
    }

    private static void imageFinds(File[] files, Integer fileIndex, Integer urlLimit, String str, Integer fileLimit) throws IOException {
        for (File file : files) {
            fileIndex = fileIndex + 1;
            String outputFile = null;
            if (file.getName().contains("wikipedia-links_lang=en.ttl.z")) {
                System.out.println(file.getName());
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
                if (!ImageFinder.getUrlResults().isEmpty()) {
                    String line = uri_dbpedia + url_wikipedia + ImageFinder.getUrlResults() + "\n";
                    content += line;
                    System.out.println(ImageFinder.getUrlResults());
                }

            }
            FileFolderUtils.stringToFile(content, outputFile);
            FileFolderUtils.stringToFile(str, WIKIPEDIA_DIR + PROCESS_FILE_LIST);
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

    public static Set<String> findEntitiesOfClass(String outputDir, File inputFile, Integer numberOfTriples) {
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

        return results;

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

}
