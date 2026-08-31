package com.OrangeLibrary.framework.common.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads Excel (.xlsx) test data files from the classpath and returns rows
 * as Object[][], ready for direct use with TestNG's @DataProvider.
 *
 * Expects the first row to be a header row (column names) - header row is
 * skipped and only data rows are returned.
 *
 * Usage:
 *   @DataProvider(name = "loginData")
 *   public Object[][] loginData() {
 *       return ExcelDataReader.readSheet("testdata/logindata.xlsx", "Sheet1");
 *   }
 */
public class ExcelDataReader {

    private static final Logger logger= LogManager.getLogger(ExcelDataReader.class);

    private ExcelDataReader()
    {

    }

    /**
     * Reads all data rows (excluding the header row) from the given sheet
     * in the given Excel file, returning each row as a String[] within an Object[][].
     *
     * @param resourcePath classpath-relative path to the .xlsx file (e.g., "testdata/logindata.xlsx")
     * @param sheetName    name of the sheet to read
     * @return Object[][] where each inner array is one row of String values
     */
    public static Object[][] readSheet(String resourcePath, String sheetName)
    {
        logger.debug("Reading Excel test data from: {} (sheet: {})", resourcePath, sheetName);
        try(InputStream inputStream=ExcelDataReader.class.getClassLoader().getResourceAsStream(resourcePath)){
            if (inputStream==null)
            {
                logger.error("Excel test data file not found on classpath: {}", resourcePath);
                throw new RuntimeException("Excel test data file not found on classpath: " + resourcePath + ". " +
                        "Verify the file exists under src/test/resources");
            }

            Workbook workbook=new XSSFWorkbook(inputStream);
            Sheet sheet=workbook.getSheet(sheetName);
            if (sheet==null)
            {
                logger.error("Sheet '{}' not found in Excel file '{}'", sheetName, resourcePath);
                throw new RuntimeException("Sheet " + sheetName + "not found in file " + resourcePath);
            }

            List<String[]> rows=new ArrayList<>();
            int rowCount=sheet.getPhysicalNumberOfRows();

            //Start at row 1 to skip the header row (row 0)
            for (int rowIndex=1;rowIndex<rowCount;rowIndex++)
            {
                Row row= sheet.getRow(rowIndex);
                if (row==null)
                {
                    continue;
                }

                int columnCount=row.getLastCellNum();
                String[] rowData=new String[columnCount];

                for (int colIndex=0;colIndex<columnCount;colIndex++)
                {
                    Cell cell=row.getCell(colIndex);
                    rowData[colIndex]=getCellValueAsString(cell);
                }

                rows.add(rowData);
            }

            workbook.close();
            logger.debug("Successfully read {} data row(s) from sheet '{}'", rows.size(), sheetName);
            return rows.toArray(new Object[0][]);
        } catch (IOException e) {
            logger.error("Failed to read Excel test data file '{}': {}", resourcePath, e.getMessage());
            throw new RuntimeException("Failed to read Excel test data file: " + resourcePath + ".", e);
        }
    }

    /**
     * Safely converts a cell's value to String regardless of its underlying cell type
     * (string, numeric, boolean), avoiding POI's type-specific getters throwing on mismatch.
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell==null)
        {
            return "";
        }

        switch (cell.getCellType()){
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                double numericValue= cell.getNumericCellValue();
                if (numericValue==Math.floor(numericValue))
                {
                    return String.valueOf((long) numericValue);
                }
                return String.valueOf(numericValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                return "";
            default:
                return cell.toString().trim();
        }
    }

}
