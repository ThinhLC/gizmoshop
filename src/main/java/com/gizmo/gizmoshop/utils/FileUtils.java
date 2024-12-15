package com.gizmo.gizmoshop.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtils {

    public static ResponseEntity<byte[]> getFileAsResponse(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] fileContent = StreamUtils.copyToByteArray(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "text/html");
        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }
    public static void downloadFile(String fileUrl, String destinationPath) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(destinationPath);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            fileOutputStream.close();
            System.out.println("File downloaded to " + destinationPath);
        } else {
            System.out.println("Failed to download file. HTTP response code: " + responseCode);
        }
    }
}
