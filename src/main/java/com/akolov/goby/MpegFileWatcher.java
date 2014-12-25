package com.akolov.goby;


import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MpegFileWatcher {

    private final String filename;

    public MpegFileWatcher(String filename) {
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
        out.print("--" + separator);
        out.print("\r\n");
        out.print("Content-Type: image/jpeg\r\n");
        out.print("Content-Length: " + bytes.length + "\r\n");
        out.print("\r\n");
        out.write(bytes);
        out.print("\r\n");
        out.print("--" + separator);
    }

    private byte[] readfile() throws IOException {
        File f = new File(filename);
        byte[] result = new byte[(int) f.length()];
        FileInputStream fis = new FileInputStream(f);
        fis.read(result);
        fis.close();

        return result;

    }
}
