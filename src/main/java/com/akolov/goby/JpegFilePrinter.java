package com.akolov.goby;


import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class JpegFilePrinter {

    private final String filename;
    private static String NEWLINE = "\r\n";

    public JpegFilePrinter(String filename) {
        this.filename = filename;
    }

    public void writeFile(ServletOutputStream out, String separator) throws IOException {
        byte[] bytes;

        try {
            bytes = readfile();
        } catch (IOException e) {
            // do nothing
            return;
        }
        out.print("--" + separator + NEWLINE);
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
