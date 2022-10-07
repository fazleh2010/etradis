/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.citec.etradis.core.Constants;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import static java.lang.System.exit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class CommandLine implements Constants {

    public static Boolean execute(String searchString, String fileName, String outputFileName) throws IOException, InterruptedException {

        String command = "grep " +'"' + searchString +'"' + " " + fileName + ">>" + outputFileName;
        System.out.println("command:"+command);
        Runtime runTime = Runtime.getRuntime();
        //System.out.println("location + scriptName::" + location + scriptName);
        //String[] commands = {"perl", location + scriptName};
        //System.out.println("command::"+command);
        Process process = runTime.exec(command);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        // Read the output from the command
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        // Read any errors from the attempted command
        System.out.println("Error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.err.println(s);
        }

        if (process.waitFor() == 0) {
            System.err.println("Process terminated ");
            return true;
        } else {
            return false;
        }

    }

}
