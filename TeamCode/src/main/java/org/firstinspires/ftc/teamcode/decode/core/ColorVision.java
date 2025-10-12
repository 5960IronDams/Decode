package org.firstinspires.ftc.teamcode.decode.core;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.teamcode.decode.Constants;

public class ColorVision {
    private final ColorSensor _colorSensor;

    private int _blue;
    private int _green;

    private boolean _hasBall;
    private boolean _currentlyHasBall;

    public ColorVision(LinearOpMode opMode) {
        _colorSensor = opMode.hardwareMap.get(ColorSensor.class, Constants.ColorVision.COLOR_CENTER_ID);
    }

    public ColorVision update() {
        _blue = _colorSensor.blue();
        _green = _colorSensor.green();
        return this;
    }
    public boolean getCurrentlyHasBall(){
        return _currentlyHasBall;
    }
    public int getRed(){
        return _colorSensor.red();
    }

    public int getBlue(){
        return _colorSensor.blue();
    }

    public int getGreen(){
        return _colorSensor.green();
    }

    public void hasBall() {
        _currentlyHasBall = _blue > Constants.ColorVision.COLOR_THRESHOLD || _green > Constants.ColorVision.COLOR_THRESHOLD;
    }

    public boolean hasStateChange() {
        hasBall();
        if (_currentlyHasBall != _hasBall) {
            _hasBall = !_hasBall;
            return true;
        }

        return false;
    }

    public String getColorCode() {
        return _blue > _green ? "P" : "G";
    }
}
