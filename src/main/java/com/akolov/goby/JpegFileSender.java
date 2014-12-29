package com.akolov.goby;


import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class JpegFileSender {

    private final String filename;
    private static String NEWLINE = "\r\n";
    private String boundary;

    public JpegFileSender(String filename, String boundary) {
        this.filename = filename;
        this.boundary = boundary;
    }

    public void writeFile(ServletOutputStream out) throws IOException {
        byte[] bytes;

        try {
            bytes = readfile();
        } catch (IOException e) {
            // do nothing
            return;
        }
        out.print("--" + boundary + NEWLINE);
        out.print("Content-Type: image/jpeg" + NEWLINE);
        out.print("Content-Length: " + bytes.length + NEWLINE);
        out.print(NEWLINE);
        out.write(bytes);
        out.print(NEWLINE);
        out.flush();
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
