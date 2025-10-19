package org.firstinspires.ftc.teamcode.decode.core;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.teamcode.decode.Constants;

/**
 * Responsible for detecting the balls that are entering the spindexer system.
 */
public class ColorVision  {
    private final ColorSensor COLOR_SENSOR;

    private int _blue;
    private int _green;

    private boolean _hasBall;
    private boolean _currentlyHasBall;

    /**
     * Initializes the color sensor.
     * @param opMode The current op mode.
     */
    public ColorVision(LinearOpMode opMode) {
        COLOR_SENSOR = opMode.hardwareMap.get(ColorSensor.class, Constants.ColorVision.COLOR_CENTER_ID);
    }

    /**
     * Updated the blue & green colors from the sensor.
     * @return The color vision object.
     */
    public ColorVision update() {
        _blue = COLOR_SENSOR.blue();
        _green = COLOR_SENSOR.green();
        return this;
    }

    /**
     * Gets the red value from the sensor.
     * @return The red value from the sensor.
     */
    public int getRed(){
        return COLOR_SENSOR.red();
    }

    /**
     * Gets the blue value from the sensor.
     * @return The blue value from the sensor.
     */
    public int getBlue(){
        return COLOR_SENSOR.blue();
    }

    /**
     * Gets the green value from the sensor.
     * @return The green value from the sensor.
     */
    public int getGreen(){
        return COLOR_SENSOR.green();
    }

    /**
     * Determines if a ball has been detected by comparing the blue and green color values to the threshold.
     * @return True if a ball has been detected.
     */
    public boolean hasBall() {
        _currentlyHasBall = _blue > Constants.ColorVision.COLOR_THRESHOLD || _green > Constants.ColorVision.COLOR_THRESHOLD;
        return _currentlyHasBall;
    }

    /**
     * Resets the state change class variables.
     */
    public void resetStateChange() {
        _currentlyHasBall = false;
        _hasBall = false;
    }

    /**
     * Will detect when the current detection state is different from the last state
     * @return True if the state has changed.
     */
    public boolean hasStateChange() {
        hasBall();
        if (_currentlyHasBall != _hasBall) {
            _hasBall = !_hasBall;
            return true;
        }

        return false;
    }

    /**
     * Determines the color code of the ball or "U" if it's not a ball.
     * @return The color code of the ball. P:Purple, G:Green, U:Unknown
     */
    public String getColorCode() {
        return _blue > Constants.ColorVision.COLOR_THRESHOLD && _blue > _green ? "P" :
                _green > Constants.ColorVision.COLOR_THRESHOLD && _green > _blue ? "G" : "U";
    }
}
