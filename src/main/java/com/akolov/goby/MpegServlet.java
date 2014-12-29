package com.akolov.goby;

import com.akolov.notipy.Mode;
import com.akolov.notipy.Notipy;
import com.akolov.notipy.NullListener;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "mpegServlet",
        urlPatterns = {"/mpeg"}, asyncSupported = true)
public class MpegServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(MpegServlet.class.getName());

    private static final String BOUNDARY = "lysmata";

    private String fullFilePath;
    private String fileFolder;
    private String fileName;

    private JpegFileSender watcher;
    private Mode notipyMode = null;
    private List<AsyncContext> contexts = new CopyOnWriteArrayList<>();


    @Override
    public void init() {
        Properties props = new Configuration().readProperties();

        notipyMode = Mode.valueOf(props.getProperty("notipy.mode", Mode.SCAN.toString()));

        fullFilePath = props.getProperty("filename");
        if (fullFilePath == null) {
            throw new RuntimeException("Cant initialize");
        }
        int lastSeparator = fullFilePath.lastIndexOf(File.separator);
        fileFolder = fullFilePath.substring(0, lastSeparator);
        fileName = fullFilePath.substring(lastSeparator + 1);

        watcher = new JpegFileSender(fullFilePath, BOUNDARY);
        Notipy.getInstance(notipyMode).addWatch(fileFolder, Notipy.FILE_MODIFIED | Notipy.FILE_RENAMED, false, new MpegListener(contexts,
                fileName, watcher));
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentType("multipart/x-mixed-replace;boundary=" + BOUNDARY);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Connection", "close");

        final AsyncContext aCtx = request.startAsync(request, response);

        aCtx.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) {
                contexts.remove(asyncEvent.getAsyncContext());
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) {
                contexts.remove(asyncEvent.getAsyncContext());
            }

            @Override
            public void onError(AsyncEvent asyncEvent) {
                contexts.remove(asyncEvent.getAsyncContext());
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) {

            }
        });
        watcher.writeFile(Arrays.asList(aCtx));

        contexts.add(aCtx);


    }

    private static class MpegListener extends NullListener {
        private List<AsyncContext> contexts;
        private final JpegFileSender watcher;
        private final String filename;

        public MpegListener(List<AsyncContext> contexts, String filename, JpegFileSender watcher) {
            this.contexts = contexts;
            this.filename = filename;
            this.watcher = watcher;
        }

        @Override
        public void fileModified(int wd, String folder, String name) {
            if (!name.equals(filename)) {
                return;
            }
            watcher.writeFile(contexts);
        }


        @Override
        public void fileRenamed(int wd, String root, String oldName, String newName) {
            if (!newName.equals(filename)) {
                LOG.log(Level.FINE, "Skipping rename " + oldName + "->" + newName);
                return;
            }
            watcher.writeFile(contexts);
        }

    }
}