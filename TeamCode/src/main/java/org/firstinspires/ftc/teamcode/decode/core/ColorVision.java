package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ColorVision {
    private String _pattern = "UUU";
    private final ColorSensor _colorC;

    private final ColorReading _readingR = new ColorReading();
    private final ColorReading _readingL = new ColorReading();
    private final ColorReading _readingC = new ColorReading();

    public ColorVision(HardwareMap hardwareMap) {
        _colorC = hardwareMap.get(ColorSensor.class, "colorC");
    }

    public String getPattern() {
        return _pattern;
    }

    public ColorReading GetCenterReading() {
        _readingC.b = _colorC.blue();
        _readingC.r = _colorC.red();
        _readingC.g = _colorC.green();
        _readingC.a = _colorC.alpha();

        return _readingC;
    }

    public String setPattern(){
        ColorReading  center = GetCenterReading();

        String pattern="";


        if (center.b > 100 && center.b > center.g)pattern += "P";
        else if (center.g > 100 && center.g > center.b)pattern += "G";
        else pattern += "U";

        _pattern = pattern;
        return pattern;
    }
    public Action readSensors() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                String reading = setPattern();

                packet.put("Pattern", reading);

                return true;
            }
        };
    }
    public Action getCenterReading() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                ColorReading reading = GetCenterReading();

                packet.put("Center R", reading.r);
                packet.put("Center G", reading.g);
                packet.put("Center B", reading.b);
                packet.put("Center A", reading.a);

                return true;
            }
        };
    }
}
