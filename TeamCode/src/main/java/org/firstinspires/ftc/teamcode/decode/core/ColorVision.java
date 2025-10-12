package org.firstinspires.ftc.teamcode.decode.core;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.teamcode.decode.Constants;

public class ColorVision {
    private final ColorSensor _colorSensor;

    private int _blue;
    private int _green;

    private boolean _hasBall;

    public ColorVision(LinearOpMode opMode) {
        _colorSensor = opMode.hardwareMap.get(ColorSensor.class, Constants.ColorVision.COLOR_CENTER_ID);
    }

    public ColorVision update() {
        _blue = _colorSensor.blue();
        _green = _colorSensor.green();
        return this;
    }

    public boolean hasBall() {
        return _blue > Constants.ColorVision.COLOR_THRESHOLD || _green > Constants.ColorVision.COLOR_THRESHOLD;
    }

    public boolean hasStateChange() {
        if (hasBall() != _hasBall) {
            _hasBall = !_hasBall;;
            return true;
        }

        return false;
    }

    public String getColorCode() {
        return _blue > _green ? "P" : "G";
    }
}
