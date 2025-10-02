package org.firstinspires.ftc.teamcode.decode.core;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

public class ColorVision {
    private final ColorSensor _color;
    public ColorVision(LinearOpMode opMode) {
        _color = opMode.hardwareMap.get(ColorSensor.class, "color");
        _color.enableLed(true);
    }

    public int getArgb() {
        return _color.argb();
    }

    public int getGreen() {
        return _color.green();
    }

    public int getRed() {
        return _color.red();
    }

    public int getBlue() {
        return _color.blue();
    }
}
