package com.akolov.goby;


import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class JpegFileSender {

    private final String filename;
    private static String NEWLINE = "\r\n";
    private String boundary;


    public JpegFileSender(String filename, String boundary) {
        this.filename = filename;
        this.boundary = boundary;
    }


    public void writeFile(List<AsyncContext> clients) {
        byte[] bytes;

        try {
            bytes = readfile();
        } catch (IOException e) {
            // do nothing
            return;
        }
        clients.stream().forEach(
                client -> {
                    try {
                        ServletOutputStream out = client.getResponse().getOutputStream();
                        out.print("--" + boundary + NEWLINE);
                        out.print("Content-Type: image/jpeg" + NEWLINE);
                        out.print("Content-Length: " + bytes.length + NEWLINE);
                        out.print(NEWLINE);
                        out.write(bytes);
                        out.print(NEWLINE);
                        out.flush();
                    } catch (IOException e) {
                        // nothing
                    }
                });
    }

    private byte[] readfile() throws IOException {
        byte[] result;

        File f = new File(filename);
        result = new byte[(int) f.length()];
        try (FileInputStream fis = new FileInputStream(f)) {
            fis.read(result);
        }
        return result;

    }
}
