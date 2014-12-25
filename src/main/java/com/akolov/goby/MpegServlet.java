package com.akolov.goby;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

@WebServlet(name = "mpegServlet",
        urlPatterns = {"/mpeg"}, asyncSupported = true)
public class MpegServlet extends HttpServlet {

    private static final String BOUNDARY = "lysmata";

    private String filename;

    private MpegFileWatcher watcher;

    public void init() {
        Properties props = new Properties();
        try {
            props.load(MpegServlet.class.getResourceAsStream("/goby.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Cant initialize");
        }
        filename = props.getProperty("filename");
        if (filename == null) {
            throw new RuntimeException("Cant initialize");
        }
        watcher = new MpegFileWatcher(filename);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(200);
        response.setContentType("multipart/x-mixed-replace;boundary=" + BOUNDARY);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Connection", "close");

        final AsyncContext aCtx = request.startAsync(request, response);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServletOutputStream out = aCtx.getResponse().getOutputStream();
                    watcher.writeFile(out, BOUNDARY);
                    aCtx.complete();
                } catch (IOException e) {
                    // nothing
                }
            }


        }).start();

    }
}