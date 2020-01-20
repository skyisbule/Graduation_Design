package com.github.skyisbule.db.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class IOUtil {

    public static boolean forceWriteFile(String url, String content){
        FileWriter writer = null;
        try {
            File file = new File(url);
            writer = new FileWriter(file);
            writer.write(content);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
