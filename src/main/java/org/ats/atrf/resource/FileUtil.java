package org.ats.atrf.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class FileUtil {
    public static void makeFile(String path, String content) {
        makeFile(new File(path), content);
    }

    public static void makeFile(File file, String content) {
        makeFile(file, content, true);
    }

    public static void makeFile(String path, String content, Boolean overwrite) {
        makeFile(new File(path), content, overwrite);
    }

    public static void makeFile(File file, String content, Boolean overwrite) {
        try {
            if (file.exists()) {
                if (overwrite) {
                    file.delete();
                    file.createNewFile();
                } else {
                    return;
                }
            }
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String path) {
        StringBuffer buffer = new StringBuffer();
        try {
            Reader reader = readFileReader(path);
            if (reader == null) {
                return null;
            }
            BufferedReader bufferedReader = new BufferedReader(readFileReader(path));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line).append(IConstants.LINE_SEPARATOR);
            }
            bufferedReader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(path + " not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static Reader readFileReader(String path) {
        try {
            return new InputStreamReader(readFileStream(path), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream readFileStream(String path) {
        File file = new File(path);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(path + " not found");
        }
        return null;
    }
}
