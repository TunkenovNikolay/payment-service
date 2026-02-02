package org.example.xpayment_adapter_app;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    private static final Map<String, String> CONTENT_TYPES = new HashMap<>();

    static {
        CONTENT_TYPES.put("html", "text/html");
        CONTENT_TYPES.put("css", "text/css");
        CONTENT_TYPES.put("js", "application/javascript");
        CONTENT_TYPES.put("json", "application/json");
        CONTENT_TYPES.put("png", "image/png");
        CONTENT_TYPES.put("jpg", "image/jpeg");
        CONTENT_TYPES.put("jpeg", "image/jpeg");
        CONTENT_TYPES.put("gif", "image/gif");
        CONTENT_TYPES.put("ico", "image/x-icon");
        CONTENT_TYPES.put("txt", "text/plain");
        CONTENT_TYPES.put("pdf", "application/pdf");
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started at http://localhost:8080");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new
                    OutputStreamWriter(clientSocket.getOutputStream()));

            String inFileName = getFileNameFromRequest(in);

            if (!isNullOrBlank(inFileName)) {
                String directory = "simple-http-server/static";
                Path filePath = Paths.get(directory, inFileName).normalize().toAbsolutePath();
                Path staticDirPath = Paths.get(directory).toAbsolutePath();

                if (!filePath.startsWith(staticDirPath)) {
                    return;
                }

                if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                    byte[] fileContent;

                    try (BufferedInputStream ignored = new BufferedInputStream(Files.newInputStream(filePath))) {
                        fileContent = Files.readAllBytes(filePath);
                    }

                    String contentType = getContentType(inFileName);
                    long fileSize = Files.size(filePath);

                    out.write("HTTP/1.1 200 OK\r\n");
                    out.write("Content-Type: " + contentType + "; charset=UTF-8\r\n");
                    out.write("Content-Length: " + fileSize + "\r\n");
                    out.write("\r\n");
                    out.write(new String(fileContent, StandardCharsets.UTF_8));
                } else {
                    String htmlResponse = "<html><body><h1>" + "404 File Not Found:" + "</h1><p>" + inFileName + "</p></body></html>";
                    out.write("HTTP/1.1 404 File Not Found:\r\n");
                    out.write("Content-Type: " + "Content-Type: text/html\r\n");
                    out.write("Content-Length: " + htmlResponse.getBytes().length + "\r\n");
                }
            }

            out.flush();
            clientSocket.close();
        }
    }

    private static boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }


    private static String getFileNameFromRequest(BufferedReader in) throws IOException {
        String request = in.readLine();
        String[] parts = request.split(" ");
        return parts[1];
    }

    private static String getContentType(String filename) {
        int index = filename.lastIndexOf('.');
        if (index > 0) {
            String extension = filename.substring(index + 1).toLowerCase();
            return CONTENT_TYPES.getOrDefault(extension, "text/html");
        }
        return "text/html";
    }
}
