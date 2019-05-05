package com.googlecode.mp4parser;

import android.support.v4.os.EnvironmentCompat;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.logging.Logger;

public class Version {
    private static final Logger LOG;
    public static final String VERSION;

    static {
        String readLine;
        LOG = Logger.getLogger(Version.class.getName());
        try {
            readLine = new LineNumberReader(new InputStreamReader(Version.class.getResourceAsStream("/version.txt"))).readLine();
        } catch (IOException e) {
            LOG.warning(e.getMessage());
            readLine = EnvironmentCompat.MEDIA_UNKNOWN;
        }
        VERSION = readLine;
    }
}
