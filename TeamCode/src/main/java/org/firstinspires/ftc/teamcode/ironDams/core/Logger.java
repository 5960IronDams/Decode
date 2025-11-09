package org.firstinspires.ftc.teamcode.ironDams.core;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Logger {
    private FileWriter WRITER;
    private final List<String> cache = new ArrayList<>();

    public Logger(String opMode) {
        try {
            Date now = new Date();
            long time = now.getTime();
            File logFile = new File(Environment.getExternalStorageDirectory().getPath() + "/FIRST/" + opMode + "_" + time + ".csv");

            WRITER = new FileWriter(logFile, true);
        } catch (Exception ignored) { }
    }

    public void writeToCache(double timestamp, String property, double value) {
        writeToMemory(timestamp, property, value);
    }

    public void writeToCache(double timestamp, String property, int value) {
        writeToMemory(timestamp, property, value);
    }

    public void writeToCache(double timestamp, String property, boolean value) {
        writeToMemory(timestamp, property, value);
    }

    private void writeToMemory(double timestamp, String property, Object value) {
        try {
            cache.add(timestamp + ", " + property + ", " + value + "\r\n");
        } catch (Exception ignored) { }
    }

    public void flushToDisc() {
        try {
            if (!cache.isEmpty()) {
                for (String line : cache) {
                    WRITER.write(line);
                }
                WRITER.flush();
                cache.clear();
            }
        } catch (Exception ignored) { }
    }
}
