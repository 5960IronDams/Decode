package org.firstinspires.ftc.teamcode.decode.core;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.teamcode.decode.Constants;

public class ColorVision  {
    private final ColorSensor COLOR_SENSOR;

    private int _blue;
    private int _green;

    private boolean _hasBall;
    private boolean _currentlyHasBall;

    public ColorVision(LinearOpMode opMode) {
        COLOR_SENSOR = opMode.hardwareMap.get(ColorSensor.class, Constants.ColorVision.COLOR_CENTER_ID);
    }

    public ColorVision update() {
        _blue = COLOR_SENSOR.blue();
        _green = COLOR_SENSOR.green();
        return this;
    }
    public boolean getCurrentlyHasBall(){
        return _currentlyHasBall;
    }
    public int getRed(){
        return COLOR_SENSOR.red();
    }

    public int getBlue(){
        return COLOR_SENSOR.blue();
    }

    public int getGreen(){
        return COLOR_SENSOR.green();
    }

    public boolean hasBall() {
        _currentlyHasBall = _blue > Constants.ColorVision.COLOR_THRESHOLD || _green > Constants.ColorVision.COLOR_THRESHOLD;
        return _currentlyHasBall;
    }

    public void resetStateChange() {
        _currentlyHasBall = false;
        _hasBall = false;
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
        return _blue > Constants.ColorVision.COLOR_THRESHOLD && _blue > _green ? "P" :
                _green > Constants.ColorVision.COLOR_THRESHOLD && _green > _blue ? "G" : "U";
    }
}
