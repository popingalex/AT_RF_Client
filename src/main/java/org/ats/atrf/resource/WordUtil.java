package org.ats.atrf.resource;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.aqua.io.file.FileUtil;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;

public class WordUtil {

    public static class WordObject {
        public final static String PATTERN_HOLDER = "%.+%";
        private XWPFDocument       document;

        public WordObject(String path) {
            try {
                document = new XWPFDocument(FileUtil.readStream(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private XWPFTable getTableByBookmark(String bookmark) {
            for (XWPFTable table : document.getTables()) {
                for (CTBookmark b : table.getRow(0).getCell(0).getCTTc().getPArray(0).getBookmarkStartList()) {
                    if (bookmark.equals(b.getName())) {
                        return table;
                    }
                }
            }
            return null;
        }

        public void fillTable(String bookmark, Object[][] source) {
            XWPFTable table = getTableByBookmark(bookmark);
            if (null != table) {
                for (int i = 0; i < source.length; i++) {
                    XWPFTableRow row = table.createRow();
                    for (int j = 0; j < source[0].length; j++) {
                        row.getCell(j).setText(source[i][j].toString());
                    }
                }
            }
        }

        public void replaceHolder(String holder, Object value) {
            for (XWPFParagraph p : document.getParagraphs()) {
                List<XWPFRun> runs = p.getRuns();
                String content = p.getText();
                if (runs.size() > 0 && Pattern.compile(holder).matcher(content).find()) {
                    runs.get(0).setText(content.replaceAll(holder, value.toString()), 0);
                    for (int i = 1, sum = runs.size(); i < sum; i++) {
                        p.removeRun(sum - i);
                    }
                }
            }
        }

        public void save(String path) {
            try {
                document.write(new FileOutputStream(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
