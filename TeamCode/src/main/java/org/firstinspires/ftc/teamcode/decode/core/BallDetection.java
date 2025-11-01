package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.ironDams.Config;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

import java.util.function.BooleanSupplier;

/**
 * Responsible for detecting the balls that are entering the spindexer system.
 */
public class BallDetection  {
    private final ColorSensor COLOR_SENSOR;
    private final SharedData DATA;
//    private final WaitFor GIVEUP = new WaitFor(1000);

    private int _blue;
    private int _green;

    private boolean _hasBall;
    private boolean _currentlyHasBall;

    private boolean _processColor = false;

    public BallDetection(LinearOpMode opMode, SharedData data) {
        COLOR_SENSOR = opMode.hardwareMap.get(ColorSensor.class, Config.Hardware.Sensors.Spindexer.COLOR_ID);
        DATA = data;
    }

    /**
     * Updated the blue & green colors from the sensor.
     * @return The color vision object.
     */
    public BallDetection update() {
        _blue = COLOR_SENSOR.blue();
        _green = COLOR_SENSOR.green();
        return this;
    }

    public int getBlue(){
        return _blue;
    }

    /**
     * Gets the green value from the sensor.
     * @return The green value from the sensor.
     */
    public int getGreen() {
        return _green;
    }

    /**
     * Determines if a ball has been detected by comparing the blue and green color values to the threshold.
     * @return True if a ball has been detected.
     */
    public boolean hasBall() {
        _currentlyHasBall = _blue > Constants.ColorVision.BLUETHRESHOLD || _green > Constants.ColorVision.GREENTHRESHOLD;
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
        return _blue > Constants.ColorVision.BLUETHRESHOLD && _blue > _green ? "P" :
                _green > Constants.ColorVision.GREENTHRESHOLD && _green > _blue ? "G" : "";
    }

    public boolean canProcessBall() {
        update();
        return hasStateChange() &&
                hasBall() &&
                !DATA.getMoveSpindexer();
//                &&
//                DATA.getSpindexerDetectionIndex() != DATA.getSpindexerCurrentIndex();
    }

//    public void processColor() {
//        if (canProcessBall()) {
//            int spindexerCurrentIndex = DATA.getSpindexerCurrentIndex();
//            switch (spindexerCurrentIndex) {
//                case 0:
//                    DATA.setActualColorCode(getColorCode(), 2);
//                    DATA.setMoveSpindexer(true);
//                    break;
//                case 2:
//                    DATA.setActualColorCode(getColorCode(), 0);
//                    DATA.setMoveSpindexer(true);
//                    break;
//                case 4:
//                    DATA.setActualColorCode(getColorCode(), 1);
//                    break;
//            }
//        }
//    }

    public void setProcessColor(boolean process) {
        _processColor = process;
    }

    public boolean process() {
        if (_processColor) {
            _green = COLOR_SENSOR.green();
            _blue = COLOR_SENSOR.blue();

            if (_blue > Constants.ColorVision.BLUETHRESHOLD && _blue > _green) {
                if (DATA.getActualPattern()[2].isEmpty()) DATA.setActualColorCode("P", 2);
                else if (DATA.getActualPattern()[0].isEmpty()) DATA.setActualColorCode("P", 0);
                else DATA.setActualColorCode("P", 1);

                _green = 0;
                _blue = 0;
                _processColor = false;
                return true;
            } else if (_green > Constants.ColorVision.GREENTHRESHOLD && _green > _blue) {
                if (DATA.getActualPattern()[2].isEmpty()) DATA.setActualColorCode("G", 2);
                else if (DATA.getActualPattern()[0].isEmpty()) DATA.setActualColorCode("G", 0);
                else DATA.setActualColorCode("G", 1);

                _green = 0;
                _blue = 0;
                _processColor = false;
                return true;
            }
        }

        return false;
    }

    public boolean autoProcessedColor() {
        if (canProcessBall()) {
            if (DATA.getActualPattern()[2].isEmpty()) DATA.setActualColorCode(getColorCode(), 2);
            else if (DATA.getActualPattern()[0].isEmpty()) DATA.setActualColorCode(getColorCode(), 0);
            else DATA.setActualColorCode(getColorCode(), 1);

            _green = 0;
            _blue = 0;

            return true;
        }

        return false;
    }

    public Action hasBallAction(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
//                    GIVEUP.reset();
                    initialized = true;
                }

                packet.put("Color Blue", _blue);
                packet.put("Color Green", _green);
                packet.put("Color Detected", getColorCode());
                packet.put("Color Allow Detection", DATA.getArtifactDetection());

                boolean isComplete = /*!DATA.getArtifactDetection() ||*/ process();
//                if (isComplete) DATA.setMoveSpindexer(true);

                packet.put("Color Actual Pattern", String.join(", ", DATA.getActualPattern()));
                packet.put("Color GB Actual Index", DATA.getGreenBallActualIndex());

                if (isComplete) {
                    packet.put("Status Color Detection", "Finished");
                    return false;
                }
                else {
                    if (driveComplete.getAsBoolean()) packet.put("Status Color Detection", "Finished");
                    else packet.put("Status Color Detection", "Running");

                    if (/*GIVEUP.allowExec() ||*/ driveComplete.getAsBoolean())
                    {
//                        DATA.setArtifactDetection(false);
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public Action detectionAction(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
//                    GIVEUP.reset();
                    initialized = true;
                }

                packet.put("Color Blue", _blue);
                packet.put("Color Green", _green);
                packet.put("Color Detected", getColorCode());
                packet.put("Color Allow Detection", DATA.getArtifactDetection());

                boolean isComplete = process(); //!DATA.getArtifactDetection() || process();
//                if (isComplete) DATA.setMoveSpindexer(true);

                packet.put("Color Actual Pattern", String.join(", ", DATA.getActualPattern()));
                packet.put("Color GB Actual Index", DATA.getGreenBallActualIndex());

                if (isComplete) {
                    packet.put("Status Color Detection", "Finished");
                    return false;
                }
                else {
                    if (driveComplete.getAsBoolean()) packet.put("Status Color Detection", "Finished");
                    else packet.put("Status Color Detection", "Running");

                    if (/*GIVEUP.allowExec() ||*/ driveComplete.getAsBoolean())
                    {
//                        DATA.setArtifactDetection(false);
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public Action autoDetectionAction(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

//                processColor();

                packet.put("Color Actual Pattern", String.join(", ", DATA.getActualPattern()));
                packet.put("Color GB Actual Index", DATA.getGreenBallActualIndex());
                packet.put("Color Detected", getColorCode());
//                packet.put("Color Spin Current Index", DATA.getSpindexerCurrentIndex());
                packet.put("Color IsMoving", DATA.getMoveSpindexer());
                packet.put("Color IsLoaded", DATA.isSpindexerLoaded());

                if (DATA.isSpindexerLoaded()) {
                    packet.put("Status Color Detection", "Finished");
                    return false;
                }
                else {
                    if (driveComplete.getAsBoolean()) packet.put("Status Color Detection", "Finished");
                    else packet.put("Status Color Detection", "Running");
                    return !driveComplete.getAsBoolean();
                }
            }
        };
    }

    public Action resetActualPattern() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                DATA.resetActualPattern();

                return false;
            }
        };
    }
}