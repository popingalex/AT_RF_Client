package org.ats.atrf.resource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.aqua.io.file.FileUtil;

public interface IConstants {
    public static String     LINE_SEPARATOR     = System.getProperty("line.separator");

    public static DateFormat DATE_FORMAT_JSON   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static DateFormat DATE_FORMAT_LONG   = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
    public static DateFormat DATE_FORMAT_SHORT  = new SimpleDateFormat("yyyyMMdd HHmmss");
    public static String     DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
    
    public static AProperties PROPERTIES = new AProperties();
    
    public static class AProperties extends Properties {
        private static final long serialVersionUID = 1L;

        public AProperties() {
            super();
            try {
                load(FileUtil.readStream("config.properties"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        public Integer getInt(String key) {
            return Integer.parseInt(getProperty(key));
        }
    }
}
