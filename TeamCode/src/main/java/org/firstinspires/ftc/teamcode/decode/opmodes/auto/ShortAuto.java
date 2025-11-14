package org.firstinspires.ftc.teamcode.decode.opmodes.auto;


import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.RaceAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.decode.auto.TagDetection;
import org.firstinspires.ftc.teamcode.decode.core.BallDetection;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Launcher;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.irondams.core.Logger;
import org.firstinspires.ftc.teamcode.irondams.core.auto.AutoDrive;
import org.firstinspires.ftc.teamcode.irondams.core.driveTrain.FourWheelDriveTrain;
import org.firstinspires.ftc.teamcode.irondams.core.driveTrain.MecanumDriveTrain;
import org.firstinspires.ftc.teamcode.irondams.core.odometry.Pinpoint;

@Autonomous(name = "SHORT", group = "IronDams")
public class ShortAuto extends LinearOpMode {
    private ElapsedTime timer;
    private Logger logger;

    private VoltageSensor voltageSensor;
    private BallDetection ballDetection;
    private Spindexer spindexer;
    private Intake intake;
    private Launcher launcher;

    private TagDetection tagDetection;

    private FourWheelDriveTrain fourWheelDriveTrain;
    private MecanumDriveTrain drive;
    private Pinpoint pinpoint;
    private AutoDrive autoDrive;

    private final double PINPOINT_RESET_WAIT = 0.35;
    private final double PICK_UP_POW = 0.3;

    private boolean _isRed = false;

    @Override
    public void runOpMode() {

        voltageSensor = hardwareMap.voltageSensor.iterator().next();
        timer = new ElapsedTime();
        logger = new Logger(this.getClass().getSimpleName());

        ballDetection = new BallDetection(this, logger);
        launcher = new Launcher(this, logger, 180);
        sleep(500);
        spindexer = new Spindexer(this, logger);
        intake = new Intake(this);

        tagDetection = new TagDetection(this, logger);

        fourWheelDriveTrain = new FourWheelDriveTrain(hardwareMap);
        drive = new MecanumDriveTrain(fourWheelDriveTrain, true);
        pinpoint = new Pinpoint(this);
        autoDrive = new AutoDrive(drive, pinpoint, logger);


        telemetry.addLine("READY");
        telemetry.update();

        while (opModeInInit()) {
            if (isStopRequested()) { tagDetection.stopStreaming(); }
            if (gamepad1.left_bumper) _isRed = true;
            else if (gamepad1.right_bumper) _isRed = false;

            tagDetection.setIsRed(_isRed);

            telemetry.addData("Color", _isRed ? "Red" : "Blue");
            telemetry.update();
        }

        waitForStart();

        if (isStopRequested()) {
            tagDetection.stopStreaming();
            return;
        }

        timer.reset();
        logger.writeToMemory(timer.milliseconds(), "color", _isRed ? "Red" : "Blue");
        logger.writeToMemory(timer.milliseconds(), "starting voltage", voltageSensor.getVoltage());

        Actions.runBlocking(
                new SequentialAction(
                        /* Indexing the artifacts that are in the spindexer while driving backwards. */
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingXPos();
                            autoDrive.setStartTime();
                        }),

                        new ParallelAction(
                                new RaceAction(
                                        autoDrive.driveTo(-8, 4, 4, 0.5, 0.8),
                                        new SleepAction(0.55)
                                ),
                                indexArtifacts()
                        ),

                        /* Read the obelisk */
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        tagDetection.webcamResetTimeout(),

                        new ParallelAction(
                            tagDetection.webcamReadAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                            indexArtifacts()
                        ),

                        /* Sort the artifacts in the spindexer */
                        spindexer.resetSortTimeoutAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                        spindexer.sortAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                        intake.setIntakeVelocityAction(timer.milliseconds(), 0),
                        new SleepAction(0.5),

                        /* shoot the balls */
                        shotArtifacts(1750, 100),

                        /* Drive back to get good angle on first tape line. */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(PINPOINT_RESET_WAIT),
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingXPos();
                            autoDrive.setStartTime();
                        }),

                        new RaceAction(
                                autoDrive.driveTo(-11, 4, 8, 0.3, 1.0),
                                new SleepAction(6.3)
                        ),

                        /* Turn to align ourselves to pick up new artifacts, 1st tape line */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(PINPOINT_RESET_WAIT),
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingHeadingPos();
                            autoDrive.setStartTime();
                        }),

                        new RaceAction(
                                autoDrive.turnTo(_isRed ? 332 : 28, 0, 25, 0.6, 0.6),
                                new SleepAction(0.4)
                        ),

                        /* Strafe to get in front of the artifacts, 1st tape */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(PINPOINT_RESET_WAIT),
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingYPos();
                            autoDrive.setStartTime();
                        }),

                        new RaceAction(
                                autoDrive.strafeTo(_isRed ? -16.75 : 16.75, 3, 11, 0.7, 1.0),
                                new SleepAction(0.9)
                        ),

                        /* move in to pick up artifacts, 1st tape */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(PINPOINT_RESET_WAIT),
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingXPos();
                            autoDrive.setStartTime();
                        }),
                        new ParallelAction(
                                indexArtifacts(),
                                new RaceAction(
                                        autoDrive.driveTo(25, 0, 0, PICK_UP_POW, PICK_UP_POW),
                                        new SleepAction(1.5)
                                )
                        ),

                        /* Turn to align ourselves to get good angle on goal */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(PINPOINT_RESET_WAIT),
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingHeadingPos();
                            autoDrive.setStartTime();
                        }),

                        new RaceAction(
                                autoDrive.turnTo( _isRed ? 25 : 335, 0, 45, 0.6, 0.6),
                                new SleepAction(0.35)
                        ),

                        /* Strafe to get in front of the goal */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(PINPOINT_RESET_WAIT),
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingYPos();
                            autoDrive.setStartTime();
                        }),
                        new ParallelAction(
                                new SequentialAction(
                                        indexArtifacts(),
                                        /* Sort the artifacts in the spindexer */
                                        spindexer.resetSortTimeoutAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                                        spindexer.sortAction(autoDrive.getDriveComplete(), timer.milliseconds())
                                ),

                                new RaceAction(
                                        autoDrive.strafeTo(_isRed ? 32.25 : -32.25, 3, 11, 0.7, 1.0),
                                        new SleepAction(1.3)
                                )
                        ),
                        intake.setIntakeVelocityAction(timer.milliseconds(), 0),

                        /* shoot the balls */
                        shotArtifacts(1820, 100),

                        /* Drive back to get good angle on 2nd tape line. */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(PINPOINT_RESET_WAIT),
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingXPos();
                            autoDrive.setStartTime();
                        }),

                        new RaceAction(
                                autoDrive.driveTo(-4, 0, 0, 0.4, 0.4),
                                new SleepAction(0.4)
                        ),

                        /* Turn to align ourselves to pick up new artifacts 2nd tape line */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(PINPOINT_RESET_WAIT),
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingHeadingPos();
                            autoDrive.setStartTime();
                        }),
                        new RaceAction(
                            autoDrive.turnTo(_isRed ? 328 : 32, 0, 45, 0.5, 0.5),
                            new SleepAction(0.5)
                        ),
                        /* Strafe to get in front of the artifacts 2nd tape line */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(PINPOINT_RESET_WAIT),
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingYPos();
                            autoDrive.setStartTime();
                        }),
                        new RaceAction(
                                autoDrive.strafeTo(_isRed ? -44.25 : 44.25, 3, 11, 0.7, 1.0),
                                new SleepAction(1.5)
                        ),

                        /* Turn to align ourselves to pick up new artifacts 2nd tape line */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(PINPOINT_RESET_WAIT),
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingHeadingPos();
                            autoDrive.setStartTime();
                        }),

                        new RaceAction(
                                autoDrive.turnTo(_isRed ? 3.5 : 356.5, 0, 45, 0.5, 0.5),
                                new SleepAction(0.2)
                        ),

                        /* move in to pick up artifacts 2nd tape line */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(PINPOINT_RESET_WAIT),
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingXPos();
                            autoDrive.setStartTime();
                        }),
                        new ParallelAction(
                                indexArtifacts(),
                                new RaceAction(
                                        autoDrive.driveTo(37.5, 0, 0, PICK_UP_POW, PICK_UP_POW),
                                        new SleepAction(2.4)
                                )
                        ),
                        new SleepAction(0.5),

                        /* move back to clear goal rack 2nd tape line */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(PINPOINT_RESET_WAIT),
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingXPos();
                            autoDrive.setStartTime();
                        }),
                        new ParallelAction(
                                indexArtifacts(),
                                new RaceAction(
                                        autoDrive.driveTo(-18, 3, 8, 0.4, 1),
                                        new SleepAction(0.7)
                                )
                        ),

                        /* Strafe to get in front of the goal */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(PINPOINT_RESET_WAIT),
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingYPos();
                            autoDrive.setStartTime();
                        }),
                        new ParallelAction(
                                new SequentialAction(
                                        indexArtifacts(),
                                        /* Sort the artifacts in the spindexer */
                                        spindexer.resetSortTimeoutAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                                        spindexer.sortAction(autoDrive.getDriveComplete(), timer.milliseconds())
                                ),

                                new RaceAction(
                                        autoDrive.strafeTo(_isRed ? 63.5 : -63.5, 3, 11, 0.7, 1.0),
                                        new SleepAction(1.9)
                                )
                        ),
                        intake.setIntakeVelocityAction(timer.milliseconds(), 0),

                        /* shoot the balls */
                        shotArtifacts(1750, 100),

                        new InstantAction(() -> {
                                logger.writeToMemory(timer.milliseconds(), "ending voltage", voltageSensor.getVoltage());
                                logger.flushToDisc();
                        })
                )
        );

    }


    Action indexArtifacts() {
        return new SequentialAction(
                launcher.closeAction(timer.milliseconds()),
                launcher.setLauncherModeAction(true, timer.milliseconds()),
                intake.setIntakeVelocityAction(timer.milliseconds(), 1000),
                new SleepAction(0.33),
                ballDetection.detectBallAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                spindexer.moveSpindexerAction(autoDrive.getDriveComplete(), timer.milliseconds()),
//                new SleepAction(0.25),
                ballDetection.detectBallAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                spindexer.moveSpindexerAction(autoDrive.getDriveComplete(), timer.milliseconds()),
//                new SleepAction(0.25),
                ballDetection.detectBallAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                spindexer.moveSpindexerAction(autoDrive.getDriveComplete(), timer.milliseconds()),
//                new SleepAction(0.25),
                launcher.setLauncherModeAction(false, timer.milliseconds())
        );
    }

    Action shotArtifacts(double velocity, double tolerance) {
        return new SequentialAction(
                launcher.setLauncherModeAction(false, timer.milliseconds()),
                intake.setIntakeVelocityAction(timer.milliseconds(), 0),
                launcher.startShootingAction(velocity, tolerance, timer.milliseconds()),
                new SleepAction(0.33),

                launcher.shotResetActionTimerAction(timer.milliseconds()),
                launcher.shootAction(timer.milliseconds()),
                spindexer.moveSpindexerAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                launcher.shotResetCompleteTimerAction(timer.milliseconds()),
                launcher.shotCompleteAction(timer.milliseconds()),
                new SleepAction(0.33),

                launcher.shotResetActionTimerAction(timer.milliseconds()),
                launcher.shootAction(timer.milliseconds()),
                spindexer.moveSpindexerAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                launcher.shotResetCompleteTimerAction(timer.milliseconds()),
                launcher.shotCompleteAction(timer.milliseconds()),
                new SleepAction(0.33),

                launcher.shotResetActionTimerAction(timer.milliseconds()),
                launcher.shootAction(timer.milliseconds()),
                spindexer.moveSpindexerAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                launcher.shotResetCompleteTimerAction(timer.milliseconds()),
                launcher.shotCompleteAction(timer.milliseconds()),
//                new SleepAction(0.5),

                launcher.resetToIndexMode(timer.milliseconds()),
                spindexer.moveSpindexerAction(autoDrive.getDriveComplete(), timer.milliseconds())
        );
    }

}