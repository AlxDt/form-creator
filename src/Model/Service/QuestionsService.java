/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model.Service;

import Model.Core.Field;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author user
 */
public class QuestionsService {

    // Create a configuration file from a list of fields
    public static void writeFieldsToFile(String filename, List<Field> fields, String outputFilename,
            String officialOutputFilename, int lengthOriginal, boolean isTemplate) throws FileNotFoundException {
        PrintWriter newFile;

        newFile = new PrintWriter(filename);

        // The first line will always contain the full path to the output excel file
        // (and its official copy, if available)
        newFile.println(outputFilename + ((officialOutputFilename != null) ? "," + officialOutputFilename : ""));

        // The second line will always contain the original length of the form
        // (the preset forms which cannot be modified)
        newFile.println(lengthOriginal);

        // The third line will always contain the template flag
        // (whether a template was used or not)
        newFile.println(isTemplate ? 1 : 0);

        for (Field field : fields) {
            newFile.print(field.getLabel());

            List<String> multiOption = field.getMultiOption();

            if (multiOption != null) {
                newFile.print(">");

                for (int choiceIndex = 0; choiceIndex < multiOption.size(); choiceIndex++) {
                    newFile.print(multiOption.get(choiceIndex));

                    if (choiceIndex < multiOption.size() - 1) {
                        newFile.print(",");
                    }
                }
            }

            newFile.println();
        }

        newFile.close();
    }

    // Get the fields from a configuration file
    public static List<Field> readFieldsFromFile(File configFile, boolean includeAll) throws FileNotFoundException {
        List<Field> fields;

        // Read all the data from the configuration file
        try (Scanner fileScanner = new Scanner(configFile)) {
            fields = new ArrayList<>();

            // Skip the first line
            fileScanner.nextLine();

            // Get the original (non-custom) length of the form
            int originalLength = Integer.parseInt(fileScanner.nextLine());

            // Skip the third line
            fileScanner.nextLine();

            while (fileScanner.hasNextLine()) {
                String label;
                List<String> multiOption;

                String field = fileScanner.nextLine();

                label = field.split(">")[0];

                try {
                    String optionsLine = field.split(">")[1];

                    multiOption = Arrays.asList(optionsLine.split(","));
                } catch (ArrayIndexOutOfBoundsException ex) {
                    multiOption = null;
                }

                fields.add(new Field(label, multiOption));

                // If a non-custom form is to be created, include the original fields only
                originalLength--;

                if (originalLength == 0 && !includeAll) {
                    break;
                }
            }
        }

        return fields;
    }

    // Get the output file name
    public static String[] getOutputFilenames(File configFile) throws FileNotFoundException {
        String outputFilename;

        // Read all the data from the configuration file
        try (Scanner fileScanner = new Scanner(configFile)) {
            outputFilename = fileScanner.nextLine();
        }

        return outputFilename.split(",");
    }

    // Get the original length of the form (the number of preset fields which cannot
    // be modified)
    public static int getLengthOriginal(File configFile) throws FileNotFoundException {
        int lengthOriginal;

        // Read all the data from the configuration file
        try (Scanner fileScanner = new Scanner(configFile)) {
            // Skip first line
            fileScanner.nextLine();

            lengthOriginal = fileScanner.nextInt();
        }

        return lengthOriginal;
    }

    // Get the template flag of the form
    public static boolean getIsTemplate(File configFile) throws FileNotFoundException {
        int isTemplate;

        // Read all the data from the configuration file
        try (Scanner fileScanner = new Scanner(configFile)) {
            // Skip first line
            fileScanner.nextLine();

            // Skip second line
            fileScanner.nextLine();

            isTemplate = fileScanner.nextInt();
        }

        return isTemplate == 1;
    }
}
