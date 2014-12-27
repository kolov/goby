package com.akolov.goby;

import com.akolov.notipy.Notipy;
import com.akolov.notipy.NullListener;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

@WebServlet(name = "mpegServlet",
        urlPatterns = {"/mpeg"}, asyncSupported = true)
public class MpegServlet extends HttpServlet {

    private static final String BOUNDARY = "lysmata";

    private String fullFilePath;
    private String fileFolder;

    private JpegFilePrinter watcher;


    @Override
    public void init() {
        Properties props = new Configuration().readProperties();
        fullFilePath = props.getProperty("filename");
        if (fullFilePath == null) {
            throw new RuntimeException("Cant initialize");
        }
        int lastSeparator = fullFilePath.lastIndexOf(File.separator);
        fileFolder = fullFilePath.substring(0, lastSeparator);

        watcher = new JpegFilePrinter(fullFilePath);
    }



    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentType("multipart/x-mixed-replace;boundary=" + BOUNDARY);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Connection", "close");

        final AsyncContext aCtx = request.startAsync(request, response);
        ServletOutputStream out = aCtx.getResponse().getOutputStream();
        watcher.writeFile(out, BOUNDARY);

        new Notipy().addWatch(fileFolder, Notipy.FILE_MODIFIED, false, new MpegListener(aCtx, fullFilePath, watcher));
    }

    private static class MpegListener extends NullListener {
        private final AsyncContext aCtx;
        private final JpegFilePrinter watcher;
        private final String filename;

        public MpegListener(AsyncContext aCtx, String filename, JpegFilePrinter watcher) {
            this.aCtx = aCtx;
            this.filename = filename;
            this.watcher = watcher;
        }

        @Override
        public void fileModified(int wd, String folder, String name) {
            if (!name.equals(filename)) {
                return;
            }
            try {
                ServletOutputStream out = aCtx.getResponse().getOutputStream();
                watcher.writeFile(out, BOUNDARY);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void fileRenamed(int wd, String s, String s1, String s2) {

        }

    }
}