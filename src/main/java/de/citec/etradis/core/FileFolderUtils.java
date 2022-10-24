/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.citec.etradis.utils.Cleaner;
import static de.citec.etradis.utils.Cleaner.cleanPikleFiles;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.System.exit;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author elahi
 */
public class FileFolderUtils {

    public static void stringToFile(String content, File fileName)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content);
        writer.close();

    }

    public static String fileToString(String fileName) {
        InputStream is;
        String fileAsString = null;
        try {
            is = new FileInputStream(fileName);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
            fileAsString = sb.toString();
            //System.out.println("Contents : " + fileAsString);
        } catch (Exception ex) {
            Logger.getLogger(FileFolderUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fileAsString;
    }
    
   

    public static Set<String> fileToSet(File file, Integer limit) {
        InputStream is;
        Set<String> ids = new TreeSet<String>();
        Integer index = 0;
        try {
            is = new FileInputStream(file);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while ((line = buf.readLine()) != null) {
                ids.add(line);

                if (limit == -1)
                    ; else if (index > limit) {
                    break;
                }
                index = index + 1;

            }
        } catch (Exception ex) {
            Logger.getLogger(FileFolderUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ids;
    }

    public static Map<String,String> fileToMap(File file, Integer limit) {
        InputStream is;
        Map<String,String> ids = new TreeMap<String,String>();
        Integer index = 0;
        try {
            is = new FileInputStream(file);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while ((line = buf.readLine()) != null) {
                if (line.contains(" ")) {
                    String[] info = line.split(" ");
                    //info[0] = Cleaner.cleanUrl(info[0]);
                    //info[2] = Cleaner.cleanUrl(info[2]);
                    ids.put(info[0],info[2]);

                }

                if (limit == -1)
                    ; else if (index > limit) {
                    break;
                }
                index = index + 1;

            }
        } catch (Exception ex) {
            Logger.getLogger(FileFolderUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ids;
    }
    
    public static Map<String,String> fileToMapPLus(File file, Integer limit) {
        InputStream is;
        Map<String,String> ids = new TreeMap<String,String>();
        Integer index = 0;
        try {
            is = new FileInputStream(file);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while ((line = buf.readLine()) != null) {
                //if (line.contains("+")) {
                    String[] info = line.split("\\+");
                    ids.put(info[0],info[1]);
                    System.out.println(info[0]+" "+info[1]);
                //}
            }
        } catch (Exception ex) {
            Logger.getLogger(FileFolderUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ids;
    }

    public static void appendToFile(File file, String line) throws IOException {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            out.println(line);
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static List<File> getFiles(String fileDir, String category, String extension) {
        String[] files = new File(fileDir).list();
        List<File> selectedFiles = new ArrayList<File>();
        for (String fileName : files) {
            if (fileName.contains(category) && fileName.contains(extension)) {
                selectedFiles.add(new File(fileDir + fileName));
            }

        }

        return selectedFiles;

    }

    public static Map<String, String> getUriLabelsJson(File classFile) {
        Map<String, String> map = new TreeMap<String, String>();
        Set<String> set = new TreeSet<String>();
        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(classFile));
            //line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    line = line.strip().trim();
                    if (line.contains("=")) {
                        String uri = line.split("=")[0];
                        String label = line.split("=")[1];
                        map.put(uri, label);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /*public static void deleteFiles(String inputDir, String extension) {
        File f = new File(inputDir);
        String[] pathnames = f.list();
        for (String pathname : pathnames) {
            //System.out.println("pathname::"+inputDir + File.separatorChar + pathname);
            String[] files = new File(inputDir + pathname).list();
            for (String file : files) {
                //System.out.println("file::"+file);
               

            }

        }

    }*/
    public static void listToFiles(List<String> list, String fileName) {
        String str = "";
        for (String element : list) {
            String line = element + "\n";
            str += line;

        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(str);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(FileFolderUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public static Set<String> getLinesFromPikleFile(File file, String matchString) {
        Set<String> results = new TreeSet<String>();
        String content = fileToString(file.getAbsolutePath());
        content = content.replace("http://", "\nhttp://");
        String[] lines = content.split("\n");

        for (String line : lines) {
            if (!line.contains(matchString)) {
                continue;
            }
            line = cleanPikleFiles(line);
            results.add(line);

        }

        return results;
    }

    public static Set<String> fileToSet(String fileName) throws FileNotFoundException, IOException {
        Set<String> set = new TreeSet<String>();
        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    line = line.strip().trim();
                    set.add(line);
                }

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }


}
