package com.easy.base.media.util;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Store {
    private static String rootDir;

    public static boolean saveFile(InputStream inputStream,String filePath,String fileName, String mimeType){
        try{
            rootDir = "upload";
        File root = new File(rootDir+filePath);
            System.out.println(rootDir);
            System.out.println(filePath);
        boolean result = root.mkdirs();
            System.out.println(result);
        Path file = Paths.get(rootDir+filePath, fileName);
        // Create and write the file
        FileOutputStream outputStream = new FileOutputStream(file.toFile());
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    public static String getFilePath(long parentId){
        return null;
    }
    public static String getFilePathForFolder(long parentId, String folderName){
        return null;
    }

}
