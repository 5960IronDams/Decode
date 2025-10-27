package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.teamcode.decode.Constants;

import java.util.function.BooleanSupplier;

/**
 * Responsible for detecting the balls that are entering the spindexer system.
 */
public class ColorVision  {
    private final ColorSensor COLOR_SENSOR;

    private final GreenBallPosition GREEN_BALL_POSITION;

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
        GREEN_BALL_POSITION = new GreenBallPosition();
    }

    public ColorVision(LinearOpMode opMode, GreenBallPosition greenBallPosition) {
        COLOR_SENSOR = opMode.hardwareMap.get(ColorSensor.class, Constants.ColorVision.COLOR_CENTER_ID);
        GREEN_BALL_POSITION = greenBallPosition;
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
                _green > Constants.ColorVision.COLOR_THRESHOLD && _green > _blue ? "G" : "";
    }

    public boolean canProcessBall() {
        update();
        return hasStateChange() &&
                hasBall() &&
                !GREEN_BALL_POSITION.getMoveSpindexer() &&
                GREEN_BALL_POSITION.getSpindexerDetectionPos() != GREEN_BALL_POSITION.getSpindexerCurrentPos();
    }

    public Action indexBalls(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                packet.put("Color Actual Patter", String.join(", ", GREEN_BALL_POSITION.getActualPattern()));
                packet.put("Color Actual Index", GREEN_BALL_POSITION.getActualIndex());
                packet.put("Color Detected", getColorCode());
                packet.put("Color Current Pos", GREEN_BALL_POSITION.getSpindexerCurrentPos());
                packet.put("Color IsSpinning", GREEN_BALL_POSITION.getMoveSpindexer());
                packet.put("Color IsLoaded", GREEN_BALL_POSITION.isLoaded());

                if (!canProcessBall()) {
                    if (GREEN_BALL_POSITION.isLoaded()) {
                        packet.put("Status Color  Index Balls", "Finished");
                        return false;
                    }
                    else {
                        if (driveComplete.getAsBoolean()) packet.put("Status Color  Index Balls", "Finished");
                        else packet.put("Status Color Index Balls", "Running");
                        return !driveComplete.getAsBoolean();
                    }
                }

                int currentPos = GREEN_BALL_POSITION.getSpindexerCurrentPos();

                GREEN_BALL_POSITION.setSpindexerDetectionPos(currentPos);

                switch (currentPos) {
                    case 0:
                        GREEN_BALL_POSITION.setActualColor(getColorCode(), 2);
                        GREEN_BALL_POSITION.setSpindexerCurrentPos(currentPos + 2);
                        GREEN_BALL_POSITION.setMoveSpindexer(true);
                        break;
                    case 2:
                        GREEN_BALL_POSITION.setActualColor(getColorCode(), 0);
                        GREEN_BALL_POSITION.setSpindexerCurrentPos(currentPos + 2);
                        GREEN_BALL_POSITION.setMoveSpindexer(true);
                        break;
                    case 4:
                        GREEN_BALL_POSITION.setActualColor(getColorCode(), 1);
                        break;
                }

                if (GREEN_BALL_POSITION.isLoaded()) {
                    packet.put("Status Color Index Balls", "Finished");
                    return false;
                }
                else {
                    if (driveComplete.getAsBoolean()) packet.put("Status Color  Index Balls", "Finished");
                    else packet.put("Status Color Index Balls", "Running");
                    return !driveComplete.getAsBoolean();
                }
            }
        };
    }
}
