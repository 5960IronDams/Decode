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

public class Spindexer {
    private final LinearOpMode _opMode;

    private final Intake _intake;

    private final CRServo _spindexer;
    private final ColorSensor _colorCenter;
//    private final ColorSensor _colorRight;
    private final DcMotor _encoder;

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
        _spindexer = opMode.hardwareMap.get(CRServo.class, Constants.Spindexer.SPINDEXER_ID);

        _colorCenter = opMode.hardwareMap.get(ColorSensor.class, Constants.Spindexer.COLOR_CENTER_ID);

        _encoder = opMode.hardwareMap.get(DcMotor.class, Constants.Spindexer.ABS_ENCODER_ID);
        _encoder.setDirection(DcMotorSimple.Direction.REVERSE);

//        closeLauncher();
    }

    public Spindexer playMode() {
        _spindexer.setPower(_opMode.gamepad2.left_stick_y);
        return this;
    }

    public Spindexer openLauncher() {
        double openPos = 1.0;
        return this;
    }

    public int getCurrentPosition() {
        return _encoder.getCurrentPosition();
    }

    public Spindexer closeLauncher() {
//        double closedPos = 0.0;
        return this;
    }

    public Spindexer setPower(double power) {
        _spindexer.setPower(power);
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

//                _spindexer.setPower(0.1);
                _intake.setPower(0.5); // 2500c
//                _intake.setPower(0.75); //3000c
                boolean moveSpindexer;
                double intakeCurrent = _intake.getCurrent();

                if (intakeCurrent > 2500) _scanCycles++;
                else _scanCycles = 0;

                if (_scanCycles >= 6) {
                    _spindexer.setPower(0.12);
                    _targetPos = getCurrentPosition() + 2200;
                } else if (_scanCycles == 0 && getCurrentPosition() >= _targetPos) {
                    _spindexer.setPower(0);
                }

                packet.put("current", intakeCurrent);
                packet.put("power", _spindexer.getPower());
                packet.put("scans", _scanCycles);

                return true;

//                packet.put("Power", _spindexer.getPower());


//                boolean hasBall = false;
//                double power = 0;
//
//                if (_mode == Mode.INTAKE) {
//                    if (_isRunning) {
//                        _intake.stop();
//                        int cp = getCurrentPosition();
////                        int tolerance = 800;
////                        if (_ballCount > 1) tolerance += 50 * (_ballCount - 1);
//                        if (cp >= _targetPos) {
//                            _isRunning = false;
//                            if (pattern[0] != null && pattern[2] != null && pattern[1] != null && !pattern[0].isEmpty() && !pattern[1].isEmpty() && !pattern[2].isEmpty()) _mode = Mode.SORT;
//                            setPower(0);
//                        } else {
//                            power = Acceleration.getPower(_targetPos, cp, 2000, 0.1);
//                            setPower(power);
//                        }
//                    } else {
//                        _intake.run(Constants.Intake.MAX_POWER);
//                        int blue = _colorCenter.blue();
//                        int green = _colorCenter.green();
//                        hasBall = blue > COLOR_THRESHOLD || green > COLOR_THRESHOLD;
//
//                        if (hasBall != _instakeModeHasBall) {
//                            _instakeModeHasBall = hasBall;
//                            if (hasBall) {
//                                _patternIndex = (_patternIndex == 2) ? 0 : _patternIndex + 1;
//
//                                _opMode.sleep(Constants.WAIT_DURATION_MS);
//                                blue = _colorCenter.blue();
//                                green = _colorCenter.green();
//                                pattern[_patternIndex] = green > blue ? "G" : "P";
//                                _ballCount += 1;
//
//                                _isRunning = true;
//                                _targetPos = getCurrentPosition() + TURN_TICKS;
//                            }
//                        }
//                    }
//                }else {
//                    _intake.stop();
//                }
//
//                packet.addLine("SPINDEXER");
//                packet.put("Mode", _mode);
//                packet.put("Spin Pos", getCurrentPosition());
//                packet.put("Target Pos", _targetPos);
//                packet.put("Center Red", _colorCenter.red());
//                packet.put("Center Green", _colorCenter.green());
//                packet.put("Center Blue", _colorCenter.blue());
//                packet.put("Center Loaded", hasBall);
//                packet.put("pattern", String.join(", ", pattern));
//                packet.put("isRunning", _isRunning);
//                packet.put("power", power);
//                packet.put("truePower", _spindexer.getPower());

//                return true;
            }
        };
    }
}
