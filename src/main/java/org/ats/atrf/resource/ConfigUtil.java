package org.ats.atrf.resource;

import java.io.IOException;
import java.util.Properties;

import net.sf.json.JSON;

public class ConfigUtil {
    public static void loadProperties() {
        Properties properties = new Properties();
        try {
            properties.load(FileUtil.readFileReader("src/main/resource/servlet.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSON loadXML(String path) {
        CustomXMLSerializer serializer = new CustomXMLSerializer();
        return serializer.read(FileUtil.readFile(path));
    }
}
