package org.firstinspires.ftc.teamcode.decode.core;

import static org.firstinspires.ftc.teamcode.decode.Constants.Spindexer.COLOR_THRESHOLD;
import static org.firstinspires.ftc.teamcode.decode.Constants.Spindexer.Mode;
import static org.firstinspires.ftc.teamcode.decode.Constants.Spindexer.TURN_TICKS;


import androidx.annotation.NonNull;

import org.firstinspires.ftc.teamcode.decode.Constants;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

public class Spindexer {
    private final LinearOpMode _opMode;

    private final Intake _intake;
    private boolean gotBall;
    private boolean _gotBall;
    private final Servo _spindexer;
    private int rotationNumber = 1;
    private double rotationPercentOne = 0;
    private double rotationPercentTwo = 0.033;
    private double rotationPercentThree = 0.066;
    private double rotationPercentFour = 0.099;
    private double rotationPercentFive = 0.132;
    private double rotationPercentSix = 0.165;
    private final ColorSensor _colorCenter;

    private Mode _mode = Mode.INTAKE;
    private boolean _instakeModeHasBall = false;
    private boolean _isRunning = false;

    private int _targetPos;
    private int _ballCount;
    private long _scanCycles;

    private String[] pattern = new String[3];
    private int _patternIndex = 0;

    public Spindexer(LinearOpMode opMode, Intake intake) {
        _opMode = opMode;

        _intake = intake;
        _spindexer = opMode.hardwareMap.get(Servo.class, Constants.Spindexer.SPINDEXER_ID);
        _colorCenter = opMode.hardwareMap.get(ColorSensor.class, Constants.Spindexer.COLOR_CENTER_ID);

//        closeLauncher();
    }

    public Spindexer playMode() {
        _spindexer.setPosition(_opMode.gamepad2.left_stick_y);
        return this;
    }

    public Spindexer openLauncher() {
        double openPos = 1.0;
        return this;
    }

    public Spindexer closeLauncher() {
//        double closedPos = 0.0;
        return this;
    }

    public Spindexer setPosition(double power) {
        _spindexer.setPosition(power);
        return this;
    }

    public Action runAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }
                int blue = _colorCenter.blue();
                int green = _colorCenter.green();

                if(blue > COLOR_THRESHOLD || green > COLOR_THRESHOLD){
                    gotBall = true;
                }else{
                    gotBall = false;
                }

                if(rotationNumber == 1){
                    _spindexer.setPosition(rotationPercentOne);
                }else if(rotationNumber == 2){
                    _spindexer.setPosition(rotationPercentTwo);
                }else if(rotationNumber == 3){
                    _spindexer.setPosition(rotationPercentThree);
                }else if(rotationNumber == 4){
                    _spindexer.setPosition(rotationPercentFour);
                }else if(rotationNumber == 5){
                    _spindexer.setPosition(rotationPercentFive);
                }else if(rotationNumber == 6){
                    _spindexer.setPosition(rotationPercentSix);
                }

                _intake.setPower(0.5);

                if(gotBall && _gotBall == false){
                    if (rotationNumber == 1){
                        rotationNumber = 3;
                        pattern[2] = green > blue ? "G" : "P";
                    } else if (rotationNumber == 3){
                        rotationNumber = 5;
                        pattern[0] = green > blue ? "G" : "P";
                    }else if(rotationNumber == 5){
                        pattern[1] = green > blue ? "G" : "P";
                    }
                    _gotBall = true;
                } else if (gotBall == false && _gotBall){
                    _gotBall = false;
                }

                packet.put("Servo pos", _spindexer.getPosition());
                packet.put("rotationNumber", rotationNumber);
                packet.put("got Ball", gotBall);
                packet.put("_got Ball", _gotBall);
                packet.put("pattern", String.join(", ", pattern));
                packet.put("red", _colorCenter.red());
                packet.put("blue", _colorCenter.blue());
                packet.put("green", _colorCenter.green());

                return true;
            }
        };
    }
}
