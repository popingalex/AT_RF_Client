package org.ats.atrf.resource;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
    public static class DataObject {
        Workbook workbook = null;
        Sheet    sheet    = null;
        public DataObject(String path) {

            try {
                if (path.endsWith("-xls")) {
                    workbook = new HSSFWorkbook(FileUtil.readFileStream(path));
                } else if (path.endsWith("xlsx")) {
                    workbook = new XSSFWorkbook(FileUtil.readFileStream(path));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(workbook.getNumberOfSheets());
            sheet = workbook.getSheetAt(0);
            Name name = workbook.getName("head_record");
//            sheet.get
//            System.out.println(name.getRefersToFormula());
//            System.out.println(sheet.getNumMergedRegions());
        }
        public void switchSheet(String name) {
            sheet = workbook.getSheet(name);
        }

        public void switchSheet(Integer index) {
            sheet = workbook.getSheetAt(index);
        }

        public Cell getCell(Integer row, Integer column) {
            return sheet.getRow(row).getCell(column);
        }
        public Object getName() {
            return sheet.getSheetName();
        }
    }
}
