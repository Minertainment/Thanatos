package com.minertainment.thanatos.commons.utils;

import java.io.*;

public class FileUtils {

    public static void loadDefaults(InputStream resource, File targetFile) {
        try {
            byte[] buffer = new byte[resource.available()];
            resource.read(buffer);
            OutputStream outputStream = new FileOutputStream(targetFile);
            outputStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}