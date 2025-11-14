package org.firstinspires.ftc.teamcode.irondams.core;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Logger {
    private FileWriter writer;
    private final List<String> CACHE = new ArrayList<>();

    public Logger(String opMode) {
        Date now = new Date();
        long time = now.getTime();
        File logFile = new File( Environment.getExternalStorageDirectory().getPath() + "/FIRST/" + opMode + "_" + time + ".csv");
        try {
            writer = new FileWriter(logFile, true);
            writer.write("time, property, value\r\n");
            writer.flush();
        } catch (Exception ignore) { }
    }

    public void writeToMemory(double timestamp, String property, double value) {
        toMemory(timestamp, property, value);
    }

    public void writeToMemory(double timestamp, String property, int value) {
        toMemory(timestamp, property, value);
    }

    public void writeToMemory(double timestamp, String property, boolean value) {
        toMemory(timestamp, property, value);
    }

    public void writeToMemory(double timestamp, String property, String value) {
        toMemory(timestamp, property, value);
    }

    private void toMemory(double timestamp, String property, Object value) {
        try {
            CACHE.add(timestamp + ", " + property + ", " + value + "\r\n");
        } catch (Exception ignored) { }
    }

    public void flushToDisc() {
        try {
            if (!CACHE.isEmpty()) {
                for (String line : CACHE) {
                    writer.write(line);
                }

                CACHE.clear();
                writer.flush();
            }
        } catch (Exception ignored) { }
    }
}
