package org.firstinspires.ftc.teamcode.decode.core;

import static org.firstinspires.ftc.teamcode.decode.Constants.Spindexer.Mode;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.Pattern;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Will switch through the spindexer modes.<br>
 *  <u>GAMEPAD 2</u>
 *  <ul>
 *      <li>B - Shoot</li>
 *      <li>A - Intake Mode</li>
 *  </ul>
 */
public class Spindexer {
    private final LinearOpMode _opMode;
    private final Servo _spindexer;

    private final Intake _intake;
    private final ColorVision _colorVision;
    private final Launcher _launcher;
    private final Pattern _pattern;

    private int _currentPos = 0;
    private int _shootCount = 0;

    private final double _launchWaitTime = Constants.Launcher.BALL_DROP_DELAY;
    private final ElapsedTime _launchTime = new ElapsedTime();

    private Mode _mode = Mode.INTAKE;
    private boolean _isShooting;

    /**
     * The brains for capturing, sorting & shooting the balls.
     * @param opMode The current op mode.
     * @param intake The intake object.
     * @param colorVision The color vision object.
     * @param launcher The launcher object.
     * @param pattern The pattern object.
     */
    public Spindexer(LinearOpMode opMode,
                     Intake intake,
                     ColorVision colorVision,
                     Launcher launcher,
                     Pattern pattern) {
        _opMode = opMode;
        _intake = intake;
        _colorVision = colorVision;
        _launcher = launcher;
        _pattern = pattern;
        _spindexer = opMode.hardwareMap.get(Servo.class, Constants.Spindexer.SPINDEXER_ID);

        _spindexer.setPosition(Constants.Spindexer.Positions[0]);
    }

    /**
     * Will rotate through the available patterns in teleop or read the qr code in autonomous.
     * @return The spindexer object.
     */
    private Spindexer setPattern() {
        _pattern
            .rotatePatternId()
            .readPatternId()
            .setTargetPattern();
        return this;
    }

    private void runIntake() {
        boolean isIntakeRunning = _intake.getPower() > 0;
        _intake.setMode();
        if (_intake.getMode() == Constants.Intake.Mode.ACTIVE) {
            if (!isIntakeRunning) _intake.setPower();
        }
        else if (isIntakeRunning) {
            _intake.stop();
        }
    }

    /**
     * Manages the logic for capturing the balls.
     *  <ul>
     *      <li>Starts the intake.</li>
     *      <li>Detects the color of the ball.</li>
     *      <li>Updates the pattern builder with the color of the ball.</li>
     *      <li>Moves to the next position to accept another ball.</li>
     *      <li>Switches to SORT &amp; stops the intake when it has captured the 3rd ball.</li>
     *  </ul>
     * @return The spindexer object.
     */
    private Spindexer runIntakeMode() {
        if (_mode == Mode.INTAKE) {
            runIntake();

            boolean hasStateChanged = _colorVision.update().hasStateChange();
            if (hasStateChanged) {
                boolean hasBall = _colorVision.getCurrentlyHasBall();
                if (hasBall) {
                    if (_currentPos == 0) {
                        _pattern.updatePatternBuilder(2, _colorVision.getColorCode());
                        _currentPos = 2;
                        _spindexer.setPosition(Constants.Spindexer.Positions[2]);
                    } else if (_currentPos == 2) {
                        _pattern.updatePatternBuilder(0, _colorVision.getColorCode());
                        _currentPos = 4;
                        _spindexer.setPosition(Constants.Spindexer.Positions[4]);
                    } else if (_currentPos == 4) {
                        _pattern.updatePatternBuilder(1, _colorVision.getColorCode());
                        _mode = Mode.SORT;
                    }
                }
            }
        }

        return this;
    }

    /**
     * Manages the logic for sorting the balls.<br>
     *  <ul>
     *      <li>Compares the green balls actual location to the current location.</li>
     *      <li>Moves the spindexer to shoot in the correct order.</li>
     *      <li><i><b>If a green ball is not detected it will advance to shoot mode.</b></i></li>
     *  </ul>
     * @return The spindexer object.
     */
    public Spindexer runSortMode() {
        if (_mode == Mode.SORT) {
            int actualPos = _pattern.getGreenPosition();
            int targetPos = _pattern.getGreenTarget();

            if (actualPos != -1 && actualPos != targetPos) {
                if (
                    targetPos == 0 && actualPos == 1 ||
                    targetPos == 1 && actualPos == 2 ||
                    targetPos == 2 && actualPos == 0
                ) {
                    _currentPos = 0;
                } else if (
                    targetPos == 0 && actualPos == 2 ||
                    targetPos == 1 && actualPos == 0 ||
                    targetPos == 2 && actualPos == 1
                ) {
                    _currentPos = 2;
                }

                _spindexer.setPosition(Constants.Spindexer.Positions[_currentPos]);
            }

            _mode = Mode.SHOOT;
        }
        return this;
    }

    /**
     * Allows player 2 to active the launcher.
     *  <ul>
     *      <li>Uses the b button on gamepad2.</li>
     *      <li>Will advance the mode to SHOOT if it's not their.</li>
     *  </ul>
     */
    private void playerShoot() {
        if (_opMode.gamepad2.a) {
            if (_mode != Mode.SHOOT) _mode = Mode.SHOOT;
            _launchTime.reset();
            _isShooting = true;
            _opMode.sleep(Constants.WAIT_DURATION_MS);
        }
    }

    private void patternChange() {
        if (_opMode.gamepad2.x) {
            _launcher.close().stop();
            _pattern.resetPatternBuilder();
            _colorVision.resetStateChange();
            _shootCount = 0;
            _isShooting = false;
            _mode = Mode.INTAKE;
            _currentPos = 0;
            _spindexer.setPosition(_currentPos);
        }
    }

    /**
     * Manages the logic for shooting the balls.
     *  Opens the launcher door and starts the launcher.
     *  Moves the spindexer to shoot in the correct order.
     *  Will shoot 3x and then move back into INTAKE mode.
     */
    public void runShootMode() {
        if (_mode == Mode.SHOOT) {
            playerShoot();
            patternChange();

            if(_launcher.getPower() == 0){
                _launcher.open().setPower();
            }

            if (_isShooting) {
                _intake.stop();
                if (_shootCount < 4) {
                    if (_launchTime.milliseconds() > _launchWaitTime) {
//                        if (_launcher.getPower() == 0) {
//                            _launcher.open().setPower();
//                            _opMode.sleep(Constants.WAIT_DURATION_MS);
//                        }

                        if (_currentPos % 2 == 0) _currentPos += 1;
                        else _currentPos += 2;

                        _spindexer.setPosition(Constants.Spindexer.Positions[_currentPos]);
//                    _opMode.sleep(Constants.Launcher.BALL_DROP_DELAY);
                        _shootCount++;
                        _launchTime.reset();
                    }
                } else {
                    _launcher.close().stop();
                    _pattern.resetPatternBuilder();
                    _colorVision.resetStateChange();
                    _shootCount = 0;
                    _isShooting = false;
                    _mode = Mode.INTAKE;
                    _currentPos = 0;
                    _spindexer.setPosition(_currentPos);
                }
            }
        }

    }

    public Action runAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                setPattern()
                        .runIntakeMode()
                        .runSortMode()
                        .runShootMode();

                _opMode.telemetry.addData("Spindex Mode", _mode);
                _opMode.telemetry.addData("Intake Mode", _intake.getMode());
                _opMode.telemetry.addData("Target Pattern", _pattern.getTarget());

                packet.put("Spindexer Mode", _mode);
                packet.put("Spindexer pos", _spindexer.getPosition());

                packet.put("Intake Mode", _intake.getMode());

                packet.put("Pattern Target G", _pattern.getGreenTarget());
                packet.put("Pattern Actual G", _pattern.getGreenPosition());
                packet.put("Pattern Target", _pattern.getTarget());
                packet.put("Pattern Actual", _pattern.getPattern());
                packet.put("Spindexer Shoot Timer", _launchTime.milliseconds());
                packet.put("Spindexer Time Delay", _launchWaitTime);
                packet.put("Spindexer Shoot Counter", _shootCount);

                packet.put("CV red", _colorVision.getRed());
                packet.put("CV blue", _colorVision.getBlue());
                packet.put("CV green", _colorVision.getGreen());

                return true;
            }
        };
    }

    public Action runSpinner() {
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (_opMode.gamepad2.x) {
                    _currentPos = (_currentPos > (Constants.Spindexer.Positions.length - 2) ? 0 : _currentPos + 1);
                    _spindexer.setPosition(Constants.Spindexer.Positions[_currentPos]);
                    _opMode.sleep(250);
                } else if (_opMode.gamepad2.y) {
                    _spindexer.setPosition(_spindexer.getPosition() + 0.01);
                    _opMode.sleep(250);
                } else if (_opMode.gamepad2.a) {
                    _spindexer.setPosition(_spindexer.getPosition() - 0.01);
                    _opMode.sleep(250);
                }

                packet.put("Current Pos Index", _currentPos);
                packet.put("Servo Pos", _spindexer.getPosition());

                return true;
            }
        };
    }
}
