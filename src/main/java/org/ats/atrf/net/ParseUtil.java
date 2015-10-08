package org.ats.atrf.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParseUtil {
    
    public static List<String> parsePS(String output, String self) {
        List<String> list = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new StringReader(output));
        try {
            for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
                if (!temp.endsWith(self)) {
                    list.add(temp.trim().split("\\s+")[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public static void main(String[] args) {
        System.out.println(Arrays.toString("  a    3   f 8".trim().split("\\s+")));
    }
}
