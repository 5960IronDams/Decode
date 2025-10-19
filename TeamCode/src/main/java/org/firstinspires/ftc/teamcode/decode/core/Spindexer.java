package org.firstinspires.ftc.teamcode.decode.core;

import static org.firstinspires.ftc.teamcode.decode.Constants.Spindexer.Mode;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.Pattern;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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
    double minLeftCurrent = 0;
    double minRightCurrent = 0;
    double maxLeftCurrent = 0;
    double maxRightCurrent = 0;

    private final LinearOpMode OP_MODE;
    private final Servo _spindexer;
    private ElapsedTime TIMER_LAUNCHER;
    private final Intake _intake;
    private final ColorVision _colorVision;
    private final Launcher _launcher;
    private final Pattern _pattern;

    private int _currentPos = 0;
    private int _detectionPos = -1;
    private int _shootCount = 0;

    private final double _launchWaitTime = Constants.Launcher.BALL_DROP_DELAY;
    private final ElapsedTime _launchTime = new ElapsedTime();

    private Mode _mode;
    private boolean _isShooting;

    private final WaitFor _shootDelay = new WaitFor(Constants.Launcher.BALL_DROP_DELAY);

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
        OP_MODE = opMode;
        _intake = intake;
        _colorVision = colorVision;
        _launcher = launcher;
        _pattern = pattern;
        _spindexer = opMode.hardwareMap.get(Servo.class, Constants.Spindexer.SPINDEXER_ID);

        _spindexer.setPosition(Constants.Spindexer.Positions[0]);
        switchMode(Mode.INTAKE);
    }

    private void switchMode(Mode mode) {
        _mode = mode;
    }

    /**
     * Will rotate through the available patterns in teleop or read the qr code in autonomous.
     * @return The spindexer object.
     */
    private Spindexer setPattern() {
        _pattern
            .setTargetPattern()
            .readPatternId();

        switchMode(_pattern.hasActualPattern() ? Mode.INTAKE : Mode.SORT);
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
     * Intake: Determines if a ball has been captured and is ready for processing.
     * @return True if a ball has been captured and is ready for processing.
     */
    private boolean canProcessBallPattern() {
        return _colorVision.update()
                    .hasStateChange() &&
                _colorVision.hasBall() &&
                _detectionPos != _currentPos;
    }

    /**
     * Intake: Sets the detection position to prevent us from processing the same ball twice.
     * @param currentPos The position the spindexer is in right now.
     */
    private void setDetectionPos(int currentPos) {
        _detectionPos = currentPos;
    }

    /**
     * Intake: Will update the pattern array that is used for sorting the balls.
     * @param patternIndex The index in the array to update.
     */
    private void updatePattern(int patternIndex) {
        _pattern.updateActualPattern(patternIndex, _colorVision.getColorCode());
    }

    public void changePosition(double pos) {
        _spindexer.setPosition(pos);
    }

    public void changePosition(int index) {
        _spindexer.setPosition(Constants.Spindexer.Positions[index]);
    }

    public double getPosition() {
        return _spindexer.getPosition();
    }

    /**
     * Intake: Will index the ball and update the actual pattern. Assumes ball consumption starts at pos 0
     */
    private void processBall(int patternIndex) {
        _currentPos += 2;
        updatePattern(patternIndex);
        changePosition(_currentPos);
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

            if (canProcessBallPattern()) {
                setDetectionPos(_currentPos);
                switch (_currentPos) {
                    case 0:
                        processBall(2);
                        break;
                    case 2:
                        processBall(0);
                        break;
                    case 4:
                        updatePattern(4);
                        switchMode(Mode.SORT);
                        break;
                }
            }

//            boolean hasStateChanged = _colorVision.update().hasStateChange();
//            if (hasStateChanged) {
//                boolean hasBall = _colorVision.hasBall();
//                if (hasBall && _detectionPos != _currentPos) {
//                    _detectionPos = _currentPos;
//                    if (_currentPos == 0) {
//                        _pattern.updatePatternBuilder(2, _colorVision.getColorCode());
//                        _currentPos = 2;
//                        _spindexer.setPosition(Constants.Spindexer.Positions[2]);
//                    } else if (_currentPos == 2) {
//                        _pattern.updatePatternBuilder(0, _colorVision.getColorCode());
//                        _currentPos = 4;
//                        _spindexer.setPosition(Constants.Spindexer.Positions[4]);
//                    } else if (_currentPos == 4) {
//                        _pattern.updatePatternBuilder(1, _colorVision.getColorCode());
//                        _mode = Mode.SORT;
//                    }
//                }
//            }
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
            int actualPos = _pattern.getGreenActualPos();
            int targetPos = _pattern.getGreenTargetPos();

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
        if (OP_MODE.gamepad2.a && TIMER_LAUNCHER.milliseconds() > Constants.WAIT_DURATION_MS) {
            if (_mode != Mode.SHOOT) _mode = Mode.SHOOT;
            _launchTime.reset();
            _isShooting = true;
            TIMER_LAUNCHER.reset();
        }
    }

    private void patternChange() {
        if (OP_MODE.gamepad2.x && TIMER_LAUNCHER.milliseconds() > Constants.WAIT_DURATION_MS) {
            _launcher.close().stop();
            _pattern.clearActualPattern();
            _colorVision.resetStateChange();
            _shootCount = 0;
            _isShooting = false;
            _mode = Mode.INTAKE;
            _currentPos = 0;
            _spindexer.setPosition(_currentPos);
            TIMER_LAUNCHER.reset();
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
            if (_launcher.getPower() == 0) {
                _launcher.open().setPower();
                OP_MODE.sleep(Constants.WAIT_DURATION_MS);
            }

            playerShoot();
            patternChange();

            if(_launcher.getPower() == 0){
                _launcher.open().setPower();
            }

            if (_isShooting) {
                if (_shootCount < 4) {
                    if (_shootDelay.allowExec()) { //&& _launcher.IsInRange()) {
                        if (_currentPos % 2 == 0) _currentPos += 1;
                        else _currentPos += 2;

                        _spindexer.setPosition(Constants.Spindexer.Positions[_currentPos]);
                        OP_MODE.sleep(Constants.Launcher.BALL_DROP_DELAY);
                        _shootCount++;
                    }
                } else {
                    _launcher.close().stop();
                    _pattern.clearActualPattern();
                    _colorVision.resetStateChange();
                    _shootCount = 0;
                    _currentPos = 0;
                    _detectionPos = -1;
                    _spindexer.setPosition(Constants.Spindexer.Positions[_currentPos]);
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

                double leftCurrent = _launcher.getLeftCurrent(CurrentUnit.MILLIAMPS);
                double rightCurrent = _launcher.getRightCurrent(CurrentUnit.MILLIAMPS);

                boolean startTracking =false;

                if (leftCurrent > 1030 && rightCurrent > 1100) startTracking = true;


                if (startTracking) {
                    if (leftCurrent < minLeftCurrent || minLeftCurrent == 0) minLeftCurrent = leftCurrent;
                    if (rightCurrent < minRightCurrent || minRightCurrent == 0) minRightCurrent = rightCurrent;
                    if (leftCurrent > maxLeftCurrent) maxLeftCurrent = leftCurrent;
                    if (rightCurrent > maxRightCurrent) maxRightCurrent = rightCurrent;
                }

                packet.put("Spindexer Mode", _mode);
                packet.put("Spindexer pos", _spindexer.getPosition());

                packet.put("Intake Mode", _intake.getMode());

                packet.put("Pattern Target G", _pattern.getGreenTargetPos());
                packet.put("Pattern Actual G", _pattern.getGreenActualPos());
                packet.put("Pattern Target", _pattern.getTarget());
                packet.put("Pattern Actual", _pattern.getPattern());
                packet.put("Servo pos", _spindexer.getPosition());
                packet.put("Launcher RC", _launcher.getRightCurrent(CurrentUnit.MILLIAMPS));
                packet.put("Launcher LC", _launcher.getLeftCurrent(CurrentUnit.MILLIAMPS));
                packet.put("Launcher RCMax", maxRightCurrent);
                packet.put("Launcher LCMax", maxLeftCurrent);
                packet.put("Launcher RCMin", minRightCurrent);
                packet.put("Launcher LCMin", minLeftCurrent);
                packet.put("Launcher In Range", _launcher.isInRange());

                return true;
            }
        };
    }
}
