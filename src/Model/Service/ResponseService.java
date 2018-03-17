/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model.Service;

import Model.Core.Field;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.RowIdLifetime;
import java.util.ArrayList;

import Model.Core.Response;
import static Model.Service.FieldService.readFieldsFromFile;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author user
 */
public class ResponseService {

	
	// NOTE:
	// If an exception is thrown, add a throws clause to the function

	// TODO [6]:
	// Creates an excel (.xlsx) file with the given file name from a given configuration (.arwq) file
	public static void createForm(File configFile, String fileNameOutput) {
       		// Create and write to an excel file from the configuration settings
		try {
			// Get the filename of the excel file from the configuration file
			String filename = configFile.getName().split("\\.")[0];
                        ArrayList<String> headers = new ArrayList<>();
			// Get the headers of the excel file from the configuration file
                        for (Field field : readFieldsFromFile(configFile))
                            headers.add(field.getLabel());
               
                        // Create a new workbook
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = (XSSFSheet) wb.createSheet("Members");
                        
			// Create the header row
			XSSFRow headerRow = sheet.createRow((short) 0);

			// Prepare the style of the header cells (they should be bold)
			XSSFCellStyle style = wb.createCellStyle();

			XSSFFont font = wb.createFont();
			font.setBold(true);

			style.setFont(font);

			// Write the headers into their respective columns
			for (int i = 0; i < headers.size(); i++) {
				// Write a header label
				XSSFCell headerCell = (XSSFCell) headerRow.createCell(i);
				headerCell.setCellValue(headers.get(i));

				// Embolden each header label
				headerCell.setCellStyle(style);

				// Autosize this column to fit its contents
				sheet.autoSizeColumn(i);
			}

			// Write the output to a file
			FileOutputStream fileOut = new FileOutputStream(filename + ".xlsx");
			wb.write(fileOut);

			fileOut.close();
			wb.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// TODO [7]:
	// Updates an excel (.xlsx) file by changing the file's fields (columns)
	// Insert blank cells to existing rows should new fields be added
	// Do not delete existing response rows!
	// Read the fields from the output file
	public static void updateForm(File outputFile) {
            // Create and write to an excel file from the configuration settings
		try {
			// Get the filename of the excel file from the configuration file
			String filename = outputFile.getName().split("\\.")[0];

			FileInputStream fileIn = new FileInputStream(outputFile);

			XSSFWorkbook wb = new XSSFWorkbook(fileIn);
			XSSFSheet sheet = (XSSFSheet) wb.getSheetAt(0);                  
			XSSFRow headerRow = sheet.getRow(0);

			// Prepare the style of the header cells (they should be bold)
			XSSFCellStyle style = wb.createCellStyle();

			XSSFFont font = wb.createFont();
			font.setBold(true);

			style.setFont(font);
                        ArrayList<String> headers = new ArrayList<>();
			// Get the headers of the excel file from the configuration file
                        for (Field field : readFieldsFromFile(new File(filename + ".txt")))
                            headers.add(field.getLabel());
                        
			// Write the headers into their respective columns
			for (int i = 0; i < headers.size(); i++) {
				// Write a header label
				XSSFCell headerCell = (XSSFCell) headerRow.createCell(i);

				headerCell.setCellValue(headers.get(i));

				// Embolden each header label
				headerCell.setCellStyle(style);

				// Autosize this column to fit its contents
				sheet.autoSizeColumn(i);
			}

			// Write the output to a file
			FileOutputStream fileOut = new FileOutputStream(outputFile);
			wb.write(fileOut);

			fileOut.close();
			wb.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	// TODO [8]:
	// Add a response row to an excel (.xlsx) file
	public static void addResponse(File outputFile, Response response) {
            // TODO Class with static function that returns the excel sheet to be written on
		boolean status = true;

		try {
			FileInputStream fileIn = new FileInputStream(outputFile);

			XSSFWorkbook workbook = new XSSFWorkbook(fileIn);
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			int rowCount = sheet.getLastRowNum();

			XSSFRow row = sheet.createRow(++rowCount);

			ArrayList<Field> FieldList = (ArrayList) response.getFields();
			// write cells
			for (int i = 0; i < FieldList.size(); i++) {
				XSSFCell cell = (XSSFCell) row.createCell(i);
				cell.setCellValue(FieldList.get(i).getAnswer());
                                
				// Autosize this column to fit its contents
				sheet.autoSizeColumn(i);
			}

			fileIn.close();

			FileOutputStream outputStream = new FileOutputStream(outputFile);
			workbook.write(outputStream);

			workbook.close();
			outputStream.close();
		} catch (IOException ex) {
			// The file provided does not exist or is of a different type!
			ex.printStackTrace();
		}            
	}
}
