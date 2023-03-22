/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import static java.lang.System.exit;

/**
 *
 * @author elahi
 */
public class CsvFile implements CsvConstants {

    private File filename = null;
    private static String resources = "src/main/resources/";
    private List<String[]> rows = new ArrayList<String[]>();

    public CsvFile(File filename) {
        this.filename = filename;

    }

    public void writeToCSV(List<String[]> csvData) {
        if (csvData.isEmpty()) {
            System.err.println("writing csv file failed!!!");
            return;
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter(this.filename))) {
            writer.writeAll(csvData);
        } catch (IOException ex) {
            System.err.println("writing csv file failed!!!" + ex.getMessage());
        }
    }

    public List<String[]> getManualRow(File qaldFile, Double limit, Integer lineLimit) {
        List<String[]> rows = new ArrayList<String[]>();

        Stack<String> stack = new Stack<String>();
        CSVReader reader;
        try {
            rows = generateLinebyLine(qaldFile, lineLimit);
        } catch (IOException ex) {
            Logger.getLogger(CsvFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rows;
    }

    public List<String[]> getRows() {
        List<String[]> rows = new ArrayList<String[]>();

        Stack<String> stack = new Stack<String>();
        CSVReader reader;
        try {

            reader = new CSVReader(new FileReader(this.filename));
            rows = reader.readAll();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CsvFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CsvFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvException ex) {
            Logger.getLogger(CsvFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rows;
    }

    private List<String[]> generateLinebyLine(File pathToCsv, Integer lineLimit) throws FileNotFoundException, IOException {
        List<String[]> rows = new ArrayList<String[]>();
        BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));
        String line = null;
        Integer index = 0;
        while ((line = csvReader.readLine()) != null) {
            try {
                line = line.replace("\"", "");

                String[] data = line.split(",");

                rows.add(data);

            } catch (Exception ex) {
                ;
            }
            index = index + 1;
            if (index > lineLimit) {
                break;
            }
            // do something with the data
        }
        csvReader.close();
        return rows;
    }

    private Map<String, String[]> generateLinebyLine(File pathToCsv, Integer lineLimit, Integer keyIndex) throws FileNotFoundException, IOException {
        Map<String, String[]> map = new TreeMap<String, String[]>();
        BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));
        String line = null;
        Integer index = 0;
        while ((line = csvReader.readLine()) != null) {
            try {
                String[] data = line.split(",");
                String key = data[keyIndex];
                map.put(key, data);

            } catch (Exception ex) {
                ;
            }
            index = index + 1;
            if (index > lineLimit) {
                break;
            }
            // do something with the data
        }
        csvReader.close();
        return map;
    }

    public File getFilename() {
        return filename;
    }

   
}
