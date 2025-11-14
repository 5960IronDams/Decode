package org.firstinspires.ftc.teamcode.decode.opmodes.auto;


import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.decode.SharedData;
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

@Autonomous(name = "RED_SHORT", group = "IronDams")
public class RedShortAuto extends LinearOpMode {
    private ElapsedTime timer;
    private Logger logger;

//    private VoltageSensor voltageSensor;
    private BallDetection ballDetection;
    private Spindexer spindexer;
    private Intake intake;
    private Launcher launcher;

    private TagDetection tagDetection;

    private FourWheelDriveTrain fourWheelDriveTrain;
    private MecanumDriveTrain drive;
    private Pinpoint pinpoint;
    private AutoDrive autoDrive;

    @Override
    public void runOpMode() {

//        voltageSensor = hardwareMap.voltageSensor.iterator().next();
        timer = new ElapsedTime();
        logger = new Logger(this.getClass().getSimpleName());

//    private VoltageSensor voltageSensor;
        ballDetection = new BallDetection(this, logger);
        spindexer = new Spindexer(this, logger);
        intake = new Intake(this);
        launcher = new Launcher(this, logger, 180);

        //TODO: Change this for the Blue side to be false.
        tagDetection = new TagDetection(this, logger, true);

        fourWheelDriveTrain = new FourWheelDriveTrain(hardwareMap);
        drive = new MecanumDriveTrain(fourWheelDriveTrain, true);
        pinpoint = new Pinpoint(this);
        autoDrive = new AutoDrive(drive, pinpoint, logger);


//        launcher.outtakePos();

        if (isStopRequested()) {
            tagDetection.stopStreaming();
            return;
        }

        waitForStart();

//        tagDetection.stopStreaming();

        if (isStopRequested()) {
            tagDetection.stopStreaming();
            return;
        }

        timer.reset();

        Actions.runBlocking(
                new SequentialAction(
                        /* Indexing the artifacts that are in the spindexer while driving backwards. */
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingXPos),
                        indexArtifacts(),

                        autoDrive.driveTo(-8, 4, 4, 0.3, 0.8),

                        /* Read the obelisk */
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        tagDetection.webcamResetTimeout(),
                        tagDetection.webcamReadAction(autoDrive.getDriveComplete(), timer.milliseconds()),

                        /* Sort the artifacts in the spindexer */
                        spindexer.resetSortTimeoutAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                        spindexer.sortAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                        new SleepAction(0.5),

                        /* shoot the balls */
                        shotArtifacts(1750, 100),

                        /* Drive back to get good angle on first tape line. */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(0.3),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingXPos),
                        autoDrive.driveTo(-18, 4, 12, 0.3, 1.0),

                        /* Turn to align ourselves to pick up new artifacts */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(0.3),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingHeadingPos),
                        autoDrive.turnTo(331, 0, 45, 0.5, 0.5),

                        /* Strafe to get in front of the artifacts */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(0.3),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingYPos),
                        new ParallelAction(
                                autoDrive.strafeTo(-13, 3, 11, 0.4, 1.0)
                        ),

                        /* move in to pick up artifacts */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(0.3),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingXPos),
                        new ParallelAction(
                                indexArtifacts(),
                                autoDrive.driveTo(30.5, 0, 0, 0.3, 0.3)
                        ),

                        /* Turn to align ourselves to pick up new artifacts */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(0.3),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingHeadingPos),
                        autoDrive.turnTo(18, 0, 45, 0.5, 0.5),

                        /* Strafe to get in front of the goal */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(0.3),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingYPos),
                        new ParallelAction(
                                indexArtifacts(),
                                autoDrive.strafeTo(30, 3, 11, 0.4, 1.0)
                        ),

                        /* Sort the artifacts in the spindexer */
                        spindexer.resetSortTimeoutAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                        spindexer.sortAction(autoDrive.getDriveComplete(), timer.milliseconds()),

                        /* shoot the balls */
                        shotArtifacts(1750, 100),

                        /* Drive back to get good angle on first tape line. */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(0.3),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingXPos),
                        autoDrive.driveTo(-4, 0, 0, 0.3, 0.3),

                        /* Turn to align ourselves to pick up new artifacts */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(0.3),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingHeadingPos),
                        autoDrive.turnTo(325, 0, 45, 0.5, 0.5),

                        /* Strafe to get in front of the artifacts 2nd tape line */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(0.3),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingYPos),
                        new ParallelAction(
                                autoDrive.strafeTo(-39, 3, 11, 0.4, 1.0)
                        ),

                        /* move in to pick up artifacts */
                        new InstantAction(() -> autoDrive.resetPinpoint()),
                        new SleepAction(0.3),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingXPos),
                        new ParallelAction(
                                indexArtifacts(),
                                autoDrive.driveTo(38, 0, 0, 0.3, 0.3)
                        ),

                        new SleepAction(3)
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
                intake.setIntakeVelocityAction(timer.milliseconds(), 0),
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

                launcher.resetToIndexMode(timer.milliseconds()),
                spindexer.moveSpindexerAction(autoDrive.getDriveComplete(), timer.milliseconds())
        );
    }

}