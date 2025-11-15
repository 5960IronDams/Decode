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

@Autonomous(name = "long", group = "IronDams")
public class LongAuto extends LinearOpMode {
    private ElapsedTime timer;
    private Logger logger;

    private VoltageSensor voltageSensor;
    private BallDetection ballDetection;
    private Spindexer spindexer;
    private Intake intake;
    private Launcher launcher;

//    private TagDetection tagDetection;

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

//        tagDetection = new TagDetection(this, logger);

        fourWheelDriveTrain = new FourWheelDriveTrain(hardwareMap);
        drive = new MecanumDriveTrain(fourWheelDriveTrain, true);
        pinpoint = new Pinpoint(this);
        autoDrive = new AutoDrive(drive, pinpoint, logger);


        telemetry.addLine("READY");
        telemetry.update();

        while (opModeInInit()) {
//            if (isStopRequested()) { tagDetection.stopStreaming(); }
            if (gamepad1.left_bumper || gamepad2.left_bumper) _isRed = true;
            else if (gamepad1.right_bumper || gamepad2.right_bumper) _isRed = false;
//            tagDetection.setIsRed(_isRed);

            telemetry.addData("Color", _isRed ? "Red" : "Blue");
            telemetry.update();
        }

        waitForStart();

//        if (isStopRequested()) {
//            tagDetection.stopStreaming();
//            return;
//        }

        timer.reset();
        logger.writeToMemory(timer.milliseconds(), "color", _isRed ? "Red" : "Blue");
        logger.writeToMemory(timer.milliseconds(), "starting voltage", voltageSensor.getVoltage());

        Actions.runBlocking(
                new SequentialAction(

                        /* Strafe away from the wall */
//                        new InstantAction(() -> {
//                            autoDrive.setDriveCompleted(false);
//                            autoDrive.setStartingYPos();
//                            autoDrive.setStartTime();
//                        }),
//                        autoDrive.strafeTo(_isRed ? 3 : -5, 0, 5, 0.7, 0.7),

                        /* Move into human player zone */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(PINPOINT_RESET_WAIT),
                        new InstantAction(() -> {
                            autoDrive.setDriveCompleted(false);
                            autoDrive.setStartingXPos();
                            autoDrive.setStartTime();
                        }),
                        intake.setIntakeVelocityAction(timer.milliseconds(), 1000),
                        new RaceAction(
                                new SleepAction(1.5),
                                autoDrive.driveTo(39, 4, 4, 0.5, 0.8),
                                indexArtifacts()
                        ),

                        tryToGetArtifacts(),
                        tryToGetArtifacts(),
                        tryToGetArtifacts(),
                        tryToGetArtifacts(),
                        tryToGetArtifacts(),
                        tryToGetArtifacts(),

                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new RaceAction(
                                new SleepAction(2),
                                indexArtifacts()
                        ),

                        intake.setIntakeVelocityAction(timer.milliseconds(), 0),

                        /* Read voltage at the end of the match */
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

    Action tryToGetArtifacts() {
        return new SequentialAction(
                /* move in to try and pick up more balls */
                new InstantAction(() -> autoDrive.resetPinpoint()),
                new SleepAction(PINPOINT_RESET_WAIT),
                new InstantAction(() -> {
                    autoDrive.setDriveCompleted(false);
                    autoDrive.setStartingYPos();
                    autoDrive.setStartTime();
                }),
                new RaceAction(
                        indexArtifacts(),
                        autoDrive.strafeTo(_isRed ? 8 : -8, 4, 4, 0.5, 0.8),
                        new SleepAction(1)
                ),
                /* move in to try and pick up more balls */
                new InstantAction(() -> autoDrive.resetPinpoint()),
                new SleepAction(PINPOINT_RESET_WAIT),
                new InstantAction(() -> {
                    autoDrive.setDriveCompleted(false);
                    autoDrive.setStartingYPos();
                    autoDrive.setStartTime();
                }),
                new RaceAction(
                        indexArtifacts(),
                        autoDrive.strafeTo(_isRed ? -10 : 10, 4, 4, 0.5, 0.8),
                        new SleepAction(1.1)
                ),
                /* Move into human player zone */
                new InstantAction(() -> autoDrive.resetPinpoint()),
                new SleepAction(PINPOINT_RESET_WAIT),
                new InstantAction(() -> {
                    autoDrive.setDriveCompleted(false);
                    autoDrive.setStartingXPos();
                    autoDrive.setStartTime();
                }),
                new RaceAction(
                        new SleepAction(1),
                        autoDrive.driveTo(8, 4, 4, 0.5, 0.8),
                        indexArtifacts()
                )//,
//                /* Move into human player zone */
//                new InstantAction(() -> autoDrive.resetPinpoint()),
//                new SleepAction(PINPOINT_RESET_WAIT),
//                new InstantAction(() -> {
//                    autoDrive.setDriveCompleted(false);
//                    autoDrive.setStartingHeadingPos();
//                    autoDrive.setStartTime();
//                })
//                ,
//                new RaceAction(
//                        new SleepAction(1),
//                        autoDrive.turnTo(320, 0, 20, 0.8, 0.8),
//                        indexArtifacts()
//                )
        );
    }

}