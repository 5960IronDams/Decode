package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ColorVision {
    private final ColorSensor _colorR;
    private final ColorSensor _colorL;
    private final ColorSensor _colorC;

    private final ColorReading _readingR = new ColorReading();
    private final ColorReading _readingL = new ColorReading();
    private final ColorReading _readingC = new ColorReading();

    public ColorVision(HardwareMap hardwareMap) {
        _colorR = hardwareMap.get(ColorSensor.class, "colorR");
        _colorL = hardwareMap.get(ColorSensor.class, "colorL");
        _colorC = hardwareMap.get(ColorSensor.class, "colorC");

        _colorR.enableLed(true);
    }

    public ColorReading GetRightReading() {
        _readingR.b = _colorR.blue();
        _readingR.r = _colorR.red();
        _readingR.g = _colorR.green();
        _readingR.a = _colorR.alpha();

        return _readingR;
    }

    public ColorReading GetLeftReading() {
        _readingL.b = _colorL.blue();
        _readingL.r = _colorL.red();
        _readingL.g = _colorL.green();
        _readingL.a = _colorL.alpha();

        return _readingL;
    }

    public ColorReading GetCenterReading() {
        _readingC.b = _colorC.blue();
        _readingC.r = _colorC.red();
        _readingC.g = _colorC.green();
        _readingC.a = _colorC.alpha();

        return _readingC;
    }

    public String getPattern(){
        ColorReading right = GetRightReading();
        ColorReading left = GetLeftReading();
        ColorReading  center = GetCenterReading();

        String pattern="";

        if (right.b > 100 && right.b > right.g)
        {
            pattern = "p";
        }
        else if (right.g > 100 && right.g > right.b)
        {
            pattern = "g";
        } else {
            pattern="u";
        }


        if (left.b > 100 && left.b > left.g) {
            pattern += "p";
        }
        else if (left.g > 100 && left.g > left.b) pattern += "g";
        else pattern+="u";


        if (center.b > 100 && center.b > left.g)pattern += "p";
        else if (center.g > 100 && center.g > center.b)pattern += "g";
        else pattern += "u";
        return pattern;
    }

    public Action getRightReading() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                ColorReading reading = GetRightReading();

                packet.put("Right R", reading.r);
                packet.put("Right G", reading.g);
                packet.put("Right B", reading.b);
                packet.put("Right A", reading.a);

                return true;
            }
        };
    }


    public Action readPattern() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                String reading = getPattern();

                packet.put("Pattern", reading);

                return true;
            }
        };
    }

    public Action getLeftReading() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                ColorReading reading = GetLeftReading();

                packet.put("Left R", reading.r);
                packet.put("Left G", reading.g);
                packet.put("Left B", reading.b);
                packet.put("Left A", reading.a);

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
