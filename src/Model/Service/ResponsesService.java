/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model.Service;

import static Model.Service.QuestionsService.readFieldsFromFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Model.Core.Field;
import java.util.LinkedHashMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author user
 */
public class ResponsesService {

    private static final String NEW_HEADER_RESPONSE_FILLER = "N/A";

    // NOTE:
    // If an exception is thrown, add a throws clause to the function
    // TODO [6]:
    // Creates an excel (.xlsx) file with the given file name from a given
    // configuration (.dlsuform) file
    public static void createForm(
            File configFile,
            String outputFilename,
            boolean includeAll) throws IOException {
        // Create and write to an excel file from the configuration settings
        List<String> headers = new ArrayList<>();

        // Get the headers of the excel file from the configuration file
        for (Field field : readFieldsFromFile(configFile, includeAll)) {
            headers.add(field.getLabel());
        }

        // Create a new workbook
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = (XSSFSheet) wb.createSheet("Responses");

            // Create the header row
            XSSFRow headerRow = sheet.createRow(0);

            // Prepare the style of the header cells (they should be bold)
            XSSFCellStyle style = wb.createCellStyle();

            XSSFFont font = wb.createFont();
            font.setBold(true);

            style.setFont(font);

            // Write the headers into their respective columns
            for (int columnIndex = 0;
                    columnIndex < headers.size();
                    columnIndex++) {
                // Write a header label
                XSSFCell headerCell
                        = (XSSFCell) headerRow.createCell(columnIndex);

                headerCell.setCellValue(headers.get(columnIndex));

                // Embolden each header label
                headerCell.setCellStyle(style);

                // Autosize this column to fit its contents
                sheet.autoSizeColumn(columnIndex);
            }

            try (FileOutputStream fileOut
                    = new FileOutputStream(outputFilename)) {
                // Write the output to a file
                wb.write(fileOut);
            }
        }
    }

    // TODO [7]:
    // Updates an excel (.xlsx) file by changing the file's fields (columns)
    // Insert blank cells to existing rows should new fields be added
    // Do not delete existing response rows!
    // Read the fields from the output file
    public static void updateForm(
            File configFile,
            File outputFile,
            boolean isCustom) throws IOException {
        // Get all new headers from the config file
        List<String> newHeaders = new ArrayList<>();

        for (Field field : readFieldsFromFile(configFile, isCustom)) {
            newHeaders.add(field.getLabel());
        }

        XSSFWorkbook workbook;

        // Get all old fields from the existing excel file
        try (FileInputStream fileIn = new FileInputStream(outputFile)) {
            workbook = new XSSFWorkbook(fileIn);

            XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

            XSSFRow headerRow = sheet.getRow(0);

            List<String> oldHeaders = new ArrayList<>();

            for (Cell header : headerRow) {
                oldHeaders.add(header.getStringCellValue());
            }

            // Get the intersection of the old and new headers
            List<String> retainedHeaders = new ArrayList<>(newHeaders);
            retainedHeaders.retainAll(oldHeaders);

            // Save all the responses of the retained rows
            LinkedHashMap<String, List<String>> retainedResponses
                    = new LinkedHashMap<>();

            for (int columnIndex = 0;
                    columnIndex < headerRow.getPhysicalNumberOfCells();
                    columnIndex++) {
                String header
                        = headerRow.getCell(columnIndex).getStringCellValue();

                if (headerRow.getCell(columnIndex).getStringCellValue().trim()
                        .isEmpty()) {
                    break;
                }

                if (retainedHeaders.contains(header)) {
                    List<String> responses = new ArrayList<>();

                    for (int rowIndex = 1;
                            rowIndex < sheet.getPhysicalNumberOfRows();
                            rowIndex++) {
                        Row responseRow = sheet.getRow(rowIndex);

                        responses.add(
                                responseRow.getCell(columnIndex)
                                        .getStringCellValue()
                        );
                    }

                    retainedResponses.put(header, responses);
                }
            }

            // Count the number of rows the original workbook has
            int numRows = sheet.getPhysicalNumberOfRows();

            // Clear the entire sheet
            workbook.removeSheetAt(0);

            // Then create another one
            sheet = workbook.createSheet("Responses");

            // Prepare the rows
            for (int rows = 0; rows < numRows; rows++) {
                sheet.createRow(rows);
            }

            // Set the first row as the header row
            headerRow = sheet.getRow(0);

            // Prepare the style of the header cells (they should be bold)
            XSSFCellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();

            font.setBold(true);
            style.setFont(font);

            // Write the new headers into their respective columns
            for (int columnIndex = 0;
                    columnIndex < newHeaders.size();
                    columnIndex++) {
                boolean isRetained;

                String header = newHeaders.get(columnIndex);

                isRetained = retainedResponses.containsKey(header);

                // Write a header label
                Cell headerCell = headerRow.createCell(columnIndex);

                headerCell.setCellValue(header);

                // Embolden each header label
                headerCell.setCellStyle(style);

                // Write all responses, or put a filler response if not
                // available
                for (int rowIndex = 1; rowIndex < numRows; rowIndex++) {
                    Row responseRow = sheet.getRow(rowIndex);
                    Cell cell = responseRow.createCell(columnIndex);

                    cell.setCellValue(isRetained
                            ? retainedResponses.get(header).get(rowIndex - 1)
                            : NEW_HEADER_RESPONSE_FILLER
                    );
                }

                // Autosize this column to fit its contents
                sheet.autoSizeColumn(columnIndex);
            }
        }

        // Write the output to a file
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            workbook.write(outputStream);

            workbook.close();
        }
    }

    // TODO [8]:
    // Add a response row to an excel (.xlsx) file
    public static void addResponse(File outputFile, List<Field> fields)
            throws IOException {
        XSSFWorkbook workbook;

        try (FileInputStream fileIn = new FileInputStream(outputFile)) {
            workbook = new XSSFWorkbook(fileIn);

            XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

            int rowCount = sheet.getLastRowNum();
            XSSFRow row = sheet.createRow(++rowCount);

            // Write cells
            for (int i = 0; i < fields.size(); i++) {
                XSSFCell cell = (XSSFCell) row.createCell(i);
                cell.setCellValue(fields.get(i).getAnswer());

                // Autosize this column to fit its contents
                sheet.autoSizeColumn(i);
            }
        }

        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            workbook.write(outputStream);

            workbook.close();
        }
    }

    // Count the number of header columns
    public static List<String> getHeaders(File outputFile) throws IOException {
        XSSFWorkbook workbook;

        List<String> headersList = new ArrayList<>();

        try (FileInputStream fileIn = new FileInputStream(outputFile)) {
            workbook = new XSSFWorkbook(fileIn);

            XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

            Row headers = sheet.getRow(0);

            for (Cell header : headers) {
                if (header.getStringCellValue().trim().isEmpty()) {
                    break;
                }

                headersList.add(header.getStringCellValue());
            }
        }

        return headersList;
    }

    // Check if the response file is square (same number of rows all throughout)
    public static boolean isSquare(File outputFile) throws IOException {
        XSSFWorkbook workbook;

        try (FileInputStream fileIn = new FileInputStream(outputFile)) {
            workbook = new XSSFWorkbook(fileIn);

            XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

            int prevColumnCount = 0;

            for (int rowIndex = 0;
                    rowIndex < sheet.getPhysicalNumberOfRows();
                    rowIndex++) {
                int columnCount = 0;

                for (int columnIndex = 0;
                        columnIndex < sheet.getRow(rowIndex)
                                .getPhysicalNumberOfCells();
                        columnIndex++) {
                    if (sheet.getRow(rowIndex).getCell(columnIndex)
                            .getStringCellValue().trim().isEmpty()) {
                        break;
                    }

                    columnCount++;
                }

                if (rowIndex > 0 && columnCount != prevColumnCount) {
                    return false;
                }

                prevColumnCount = columnCount;
            }
        }

        return true;
    }
}
