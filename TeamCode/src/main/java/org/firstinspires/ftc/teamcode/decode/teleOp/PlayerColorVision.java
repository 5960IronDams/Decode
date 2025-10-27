package org.firstinspires.ftc.teamcode.decode.teleOp;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.core.GreenBallPosition;

import java.util.function.BooleanSupplier;

/**
 * Responsible for detecting the balls that are entering the spindexer system.
 */
public class PlayerColorVision {
    private final ColorSensor COLOR_SENSOR;

    private final GreenBallPosition GREEN_BALL_POSITION;

    private int _blue;
    private int _green;

    private boolean _hasBall;
    private boolean _currentlyHasBall;


    public PlayerColorVision(LinearOpMode opMode, GreenBallPosition greenBallPosition) {
        COLOR_SENSOR = opMode.hardwareMap.get(ColorSensor.class, Constants.ColorVision.COLOR_CENTER_ID);
        GREEN_BALL_POSITION = greenBallPosition;
    }

    /**
     * Updated the blue & green colors from the sensor.
     * @return The color vision object.
     */
    public PlayerColorVision update() {
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
    public int getGreen() {
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

    public Action indexBallsAction() {
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
                packet.put("Color Can Process", canProcessBall());
                packet.put("Color _r", COLOR_SENSOR.red());
                packet.put("Color _g", COLOR_SENSOR.green());
                packet.put("Color _b", COLOR_SENSOR.blue());

                if (canProcessBall()) {
                    int currentPos = GREEN_BALL_POSITION.getSpindexerCurrentPos();

                    switch (currentPos) {
                        case 0:
                            GREEN_BALL_POSITION.setSpindexerDetectionPos(currentPos);
                            GREEN_BALL_POSITION.setActualColor(getColorCode(), 2);
                            GREEN_BALL_POSITION.setSpindexerCurrentPos(currentPos + 2);
                            GREEN_BALL_POSITION.setMoveSpindexer(true);
                            break;
                        case 2:
                            GREEN_BALL_POSITION.setSpindexerDetectionPos(currentPos);
                            GREEN_BALL_POSITION.setActualColor(getColorCode(), 0);
                            GREEN_BALL_POSITION.setSpindexerCurrentPos(currentPos + 2);
                            GREEN_BALL_POSITION.setMoveSpindexer(true);
                            break;
                        case 4:
                            GREEN_BALL_POSITION.setSpindexerDetectionPos(currentPos);
                            GREEN_BALL_POSITION.setActualColor(getColorCode(), 1);
                            break;
                    }
                }

                return true;
            }
        };
    }
}
