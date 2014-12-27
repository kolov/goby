package com.akolov.goby;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Configuration {

    public Properties readProperties() {
        Properties props = new Properties();
        InputStream is = MpegServlet.class.getResourceAsStream("/goby.properties");
        if (is == null) {
            throw new RuntimeException("No goby.properties found on class path");
        }
        try {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Cant initialize from /goby.properties");
        }
        return props;
    }

}
