/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author elahi
 */
public class FileFolderUtils {

    public static Set<String> getUrisFromFile(String fileName) throws FileNotFoundException, IOException {
        Set<String> set = new TreeSet<String>();
        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(fileName));
            while ((line = reader.readLine()) != null) {
                line = line.strip().stripLeading().stripTrailing().trim();
                line=line.replace("\"", "");
                set.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }

}
