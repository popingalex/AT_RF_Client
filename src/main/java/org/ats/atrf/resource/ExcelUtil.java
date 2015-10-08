package org.ats.atrf.resource;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.aqua.io.file.FileUtil;

public class ExcelUtil {
    public static class SheetObject {
        private Workbook workbook = null;
        private Sheet    sheet    = null;
        public SheetObject(String path) {

            try {
                if (path.endsWith("-xls")) {
                    workbook = new HSSFWorkbook(FileUtil.readStream(path));
                } else if (path.endsWith("xlsx")) {
                    workbook = new XSSFWorkbook(FileUtil.readStream(path));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void switchSheet(String name) {
            sheet = workbook.getSheet(name);
        }

        public void switchSheet(Integer index) {
            sheet = workbook.getSheetAt(index);
        }

        public Cell getCell(Integer row, Integer column) {
            Row r = sheet.getRow(row);
            return r == null ? null : r.getCell(column);
        }
        public Object getName() {
            return sheet.getSheetName();
        }
    }
}
