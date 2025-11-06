package org.firstinspires.ftc.teamcode.ironDams.core;

import android.os.Environment;
import android.provider.ContactsContract;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger {
    private final FileWriter WRITER;

    public Logger(String opMode) {
        Date now = new Date();
        long time = now.getTime();
        File logFile = new File( Environment.getExternalStorageDirectory().getPath() + "/FIRST/" + opMode + "_" + time + ".csv");
        try {
            WRITER = new FileWriter(logFile, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(double timestamp, String property, double value) {
        writeToFile(timestamp, property, value);
    }

    public void write(double timestamp, String property, int value) {
        writeToFile(timestamp, property, value);
    }

    public void write(double timestamp, String property, boolean value) {
        writeToFile(timestamp, property, value);
    }


    private void writeToFile(double timestamp, String property, Object value) {
        try {
            WRITER.write(timestamp + ", " + property + ", " + value + "\r\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
