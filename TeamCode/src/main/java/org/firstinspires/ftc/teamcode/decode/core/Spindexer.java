package org.firstinspires.ftc.teamcode.decode.core;

import static org.firstinspires.ftc.teamcode.decode.Constants.Spindexer.Mode;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Will switch through the spindexer modes.<br>
 *  <u>GAMEPAD 2</u>
 *  <ul>
 *      <li>B - Shoot</li>
 *      <li>A - Intake Mode</li>
 *  </ul>
 */
public class Spindexer {
    private final LinearOpMode OP_MODE;
    private final Servo SPINDEXER;
    private final Intake INTAKE;
    private final ColorVision COLOR_VISION;
    private final Shooter SHOOTER;
    private final Pattern PATTERN;

    private final WaitFor USER_BTN_DELAY = new WaitFor(Constants.WAIT_DURATION_MS);
    private final WaitFor PATTERN_SORT_DELAY = new WaitFor(500);

    private int _currentPos = 0;
    private int _detectionPos = -1;
    private int _shootCount = 0;


    private Mode _mode;
    private boolean _isShooting;
    private boolean _waitToDetectShot;


    private boolean _isPatternChanging;
    private final WaitFor PATTERN_CHANGE_DELAY = new WaitFor(1000);
    private final WaitFor BALL_LAUNCH_DELAY = new WaitFor(250);
    private boolean startTracking = false;
    /**
     * Tracks the shooters left motor current.
     */
    double minLeftCurrent = 0;
    /**
     * Tracks the shooters right motor current.
     */
    double minRightCurrent = 0;
    /**
     * Tracks the shooters left motor current.
     */
    double maxLeftCurrent = 0;
    /**
     * Tracks the shooters right motor current.
     */
    double maxRightCurrent = 0;

    private final WaitFor SHOOTER_CURRENT_TRACKER = new WaitFor(5000);

    private final WaitFor SHOOT_DELAY_TIMER = new WaitFor(Constants.Shooter.BALL_DROP_DELAY);

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
                     Shooter launcher,
                     Pattern pattern) {
        OP_MODE = opMode;
        INTAKE = intake;
        COLOR_VISION = colorVision;
        SHOOTER = launcher;
        PATTERN = pattern;
        SPINDEXER = opMode.hardwareMap.get(Servo.class, Constants.Spindexer.SPINDEXER_ID);

        SPINDEXER.setPosition(Constants.Spindexer.Positions[0]);
        switchMode(Mode.INDEX);
    }

    private void switchMode(Mode mode) {
        _mode = mode;
    }

    public Mode getMode() {
        return _mode;
    }

    /**
     * Will rotate through the available patterns in teleop or read the qr code in autonomous.
     * @return The spindexer object.
     */
    private Spindexer setPattern() {
        PATTERN.setTargetPattern();

//        switchMode(PATTERN.hasActualPattern() ? Mode.INTAKE : Mode.SORT);
        return this;
    }

    /**
     * INTAKE: Starts the intake if the Intake mode is Active.
     */
    private void runIntake() {
        boolean isIntakeRunning = INTAKE.getPower() == Constants.Intake.MAX_POWER;
        INTAKE.setMode();
        if (INTAKE.getMode() == Constants.Intake.Mode.ACTIVE) {
            if (!isIntakeRunning) INTAKE.setPower(Constants.Intake.MAX_POWER);
        }
        else if (isIntakeRunning) {
            INTAKE.stop();
        }
    }

    /**
     * Intake: Reads the color & determines if a ball has been captured and is ready for processing.
     * @return True if a ball has been captured and is ready for processing.
     */
    private boolean canProcessBallPattern() {
        return COLOR_VISION.update()
                    .hasStateChange() &&
                COLOR_VISION.hasBall() &&
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
    public String updatePattern(int patternIndex) {
        String colorCode = COLOR_VISION.getColorCode();
        PATTERN.updateActualPattern(patternIndex, colorCode);
        return colorCode;
    }

    public void changePosition(double pos) {
        SPINDEXER.setPosition(pos);
    }

    public void changePosition(int index) {
        SPINDEXER.setPosition(Constants.Spindexer.Positions[index]);
    }

    public double getPosition() {
        return SPINDEXER.getPosition();
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
    public Spindexer runIntakeMode(boolean switchToSort) {
        if (_mode == Mode.INDEX) {
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
                        updatePattern(1);
                        if (switchToSort) switchMode(Mode.SORT);
                        break;
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
    public Spindexer runSortMode(boolean switchToShoot) {
        if (_mode == Mode.SORT) {
            _isPatternChanging = false;
            if (INTAKE.getPower() != Constants.Intake.SORT_POWER) INTAKE.setPower(Constants.Intake.SORT_POWER);
            int actualPos = PATTERN.getGreenActualPos();
            int targetPos = PATTERN.getGreenTargetPos();

            if (targetPos != -1 && actualPos != -1 && targetPos != actualPos) {
                PATTERN_SORT_DELAY.reset();
                int distance = actualPos - targetPos;
                _currentPos = _currentPos + distance * 2;
                SPINDEXER.setPosition(Constants.Spindexer.Positions[_currentPos]);
                PATTERN.makeActualMatchTarget();
            }

            if (switchToShoot && PATTERN_SORT_DELAY.allowExec()) switchMode(Mode.SHOOT);
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
        if (OP_MODE.gamepad2.dpad_down && USER_BTN_DELAY.allowExec()) {
            if (_mode != Mode.SHOOT) _mode = Mode.SHOOT;
            _isShooting = true;
        }
    }

    private void patternChange() {
        if ((OP_MODE.gamepad2.x || OP_MODE.gamepad2.a || OP_MODE.gamepad2.b) && _mode == Mode.SHOOT) {
            SHOOTER.close().stop();
            _isShooting = false;
            _isPatternChanging = true;
            PATTERN_CHANGE_DELAY.reset();
        }

        if (_isPatternChanging && PATTERN_CHANGE_DELAY.allowExec()) {
            _isPatternChanging = false;
            switchMode(Mode.SORT);
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
            if (SHOOTER.getPower() == 0 && !_isPatternChanging) {
                SHOOTER.open().setPower(Constants.Shooter.MAX_POWER);
                OP_MODE.sleep(Constants.WAIT_DURATION_MS);
            }

            playerShoot();
            patternChange();

            if (_isShooting) {
                if (INTAKE.getPower() != 0) INTAKE.stop();
                if (_shootCount < 4) {
                    /*  The shooter is within the current limit
                     *  We are allowing the ball to escape. */
                    if (_waitToDetectShot) {
                        _waitToDetectShot = !(SHOOTER.getLeftCurrent(CurrentUnit.MILLIAMPS) > Constants.Shooter.BALL_DETECTION_CURRENT);
                        SHOOT_DELAY_TIMER.reset();
                    }
                    else if (SHOOTER.isInRange() && SHOOT_DELAY_TIMER.allowExec()) {
                        _waitToDetectShot = true;
                        if (_currentPos % 2 == 0) _currentPos += 1;
                        else _currentPos += 2;


                        SPINDEXER.setPosition(Constants.Spindexer.Positions[_currentPos]);
//                        OP_MODE.sleep(Constants.Shooter.BALL_DROP_DELAY);
                        _shootCount++;
                    }
                } else {
                    SHOOTER.close().stop();
                    PATTERN.clearActualPattern();
                    COLOR_VISION.resetStateChange();
                    _shootCount = 0;
                    _currentPos = 0;
                    _detectionPos = -1;
                    _waitToDetectShot = false;
                    SPINDEXER.setPosition(Constants.Spindexer.Positions[_currentPos]);
                    _isShooting = false;
                    _mode = Mode.INDEX;
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
                        .runIntakeMode(true)
                        .runSortMode(true)
                        .runShootMode();

                double leftCurrent = SHOOTER.getLeftCurrent(CurrentUnit.MILLIAMPS);
                double rightCurrent = SHOOTER.getRightCurrent(CurrentUnit.MILLIAMPS);

                if (SHOOTER.getPower() != 0 && SHOOT_DELAY_TIMER.allowExec()) {
                    startTracking = true;
                    minLeftCurrent = 10000;
                    minRightCurrent = 10000;
                    maxLeftCurrent = 0;
                    maxRightCurrent = 0;
                } else if (SHOOTER.getPower() == 0) {
                    startTracking = false;
                }


                if (startTracking) {
                    if (leftCurrent < minLeftCurrent || minLeftCurrent == 0) minLeftCurrent = leftCurrent;
                    if (rightCurrent < minRightCurrent || minRightCurrent == 0) minRightCurrent = rightCurrent;
                    if (leftCurrent > maxLeftCurrent) maxLeftCurrent = leftCurrent;
                    if (rightCurrent > maxRightCurrent) maxRightCurrent = rightCurrent;
                }

                packet.put("Spindexer Mode", _mode);
                packet.put("Spindexer pos", SPINDEXER.getPosition());
                packet.put("Spindexer Pattern Change", _isPatternChanging);
                packet.put("Wait for shot", _waitToDetectShot);
                packet.put("Spindexer Shoot Count", _shootCount);

                packet.put("Intake Mode", INTAKE.getMode());

                packet.put("Pattern Target G", PATTERN.getGreenTargetPos());
                packet.put("Pattern Actual G", PATTERN.getGreenActualPos());
                packet.put("Pattern Target", PATTERN.getTarget());
                packet.put("Pattern Actual", PATTERN.getActual());
                packet.put("Servo pos", SPINDEXER.getPosition());
                packet.put("Launcher RC", SHOOTER.getRightCurrent(CurrentUnit.MILLIAMPS));
                packet.put("Launcher LC", SHOOTER.getLeftCurrent(CurrentUnit.MILLIAMPS));
                packet.put("Launcher RCMax", maxRightCurrent);
                packet.put("Launcher LCMax", maxLeftCurrent);
                packet.put("Launcher RCMin", minRightCurrent);
                packet.put("Launcher LCMin", minLeftCurrent);
                packet.put("Launcher In Range", SHOOTER.isInRange());


                return true;
            }
        };
    }
}
