package org.firstinspires.ftc.teamcode.decode.opmodes;

import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.decode.auto.TagDetection;
import org.firstinspires.ftc.teamcode.decode.core.BallDetection;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Shooter;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.ironDams.auto.AutoDrive;
import org.firstinspires.ftc.teamcode.ironDams.core.Logger;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.FourWheelDriveTrain;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.MecanumDriveTrain;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;

import java.util.concurrent.atomic.AtomicReference;

@Autonomous(name = "red_short", group = "@@@@IronDams")
public class RED_SHORT_OpMode extends LinearOpMode {
    @Override
    public void runOpMode() {
        AtomicReference<Boolean> moveSpindexer = new AtomicReference<>(false);

        SharedData data = new SharedData();
        Logger log = new Logger("red_short");

        TagDetection tagDetection = new TagDetection(this, data);

        FourWheelDriveTrain dt = new FourWheelDriveTrain(this.hardwareMap);
        MecanumDriveTrain drive = new MecanumDriveTrain(dt);
        Pinpoint pinpoint = new Pinpoint(this);
        AutoDrive autoDrive = new AutoDrive(drive, pinpoint, log);

        Shooter shooter = new Shooter(this, data);
        BallDetection ballDetection = new BallDetection(this, data);
        Spindexer spindexer = new Spindexer(this, data);
        Intake intake = new Intake(this);

//        tagDetection.stopStreaming();
        while (opModeInInit()) {
            if (isStopRequested()) {
                tagDetection.stopStreaming();
                return;
            }
        }

        waitForStart();

        if (isStopRequested()) {
            tagDetection.stopStreaming();
            return;
        }

        Actions.runBlocking(
                new SequentialAction(
//                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
//                        new InstantAction(autoDrive::setStartingXPos),
                        /* Start the intake */
                        new InstantAction(() -> intake.setVelocity(800)),

                        /* Index the artifacts in the spindexer */
                        new InstantAction(() -> ballDetection.setProcessColor(true)),
                        ballDetection.detectionAction(autoDrive.getDriveComplete()),
                        spindexer.moveDistAction(2),
                        new SleepAction(0.5),
                        new InstantAction(() -> ballDetection.setProcessColor(true)),
                        ballDetection.detectionAction(autoDrive.getDriveComplete()),
                        spindexer.moveDistAction(2),
                        new SleepAction(0.5),
                        new InstantAction(() -> ballDetection.setProcessColor(true)),
                        ballDetection.detectionAction(autoDrive.getDriveComplete()),

                        /* Drive backwards and read the obelisk. */
                        autoDrive.driveTo(-14, 4, 10, 0.3, 1.0),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        tagDetection.webcamReadAction(autoDrive.getDriveComplete()),
//TODO: I think the commented line below was duplicated.
//                        ballDetection.detectionAction(autoDrive.getDriveComplete()),
                        /* Decode the artifacts and move forward to shoot. */
                        new InstantAction(autoDrive::setStartingXPos),
                        new ParallelAction(
                                spindexer.sortAction(autoDrive.getDriveComplete()),
                                autoDrive.driveTo(-10, 4, 9, 0.3, 1.0)
                        ),
                        /* Stop streaming the cameras */
                        new InstantAction(tagDetection::stopStreaming),
//TODO: I don't think this is need because we moved the sortAction into the parallel with the drive.
//                        new SleepAction(0.5),
                        /* Shoot the artifacts into the goal. */
                        shooter.startAction(Constants.Shooter.TARGET_VELOCITY - 175),
                        spindexer.moveDistAction(1),
                        new SleepAction(1.2),
                        shooter.startAction(Constants.Shooter.TARGET_VELOCITY - 150),
                        spindexer.moveDistAction(2),
                        new SleepAction(1.2),
                        shooter.startAction(Constants.Shooter.TARGET_VELOCITY - 100),
                        spindexer.moveDistAction(2),
                        new SleepAction(1.2),
                        spindexer.moveDistAction(2),
                        new InstantAction(() -> intake.setVelocity(0)),
                        new SleepAction(1.2),
                        ballDetection.resetActualPattern(),
                        shooter.stopAction(),

                        /* Move backward, reset the spindexer and try to read the obelisk again in case we missed it the first time. */
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingXPos),
                        new ParallelAction(
                                spindexer.moveIndexAction(0),
                                tagDetection.webcamReadAction(autoDrive.getDriveComplete()),
                                autoDrive.driveTo(-28, 4, 10, 0.3, 0.7)
                        ),

                        /* Turn to align ourselves to pick up new artifacts */
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingHeadingPos),
                        autoDrive.turnTo(330, 10, 15, 0.2, 0.7),

                        /* Strafe to get in front of the artifacts */
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingYPos),
                        new ParallelAction(
//                            spindexer.indexAction(autoDrive.getDriveComplete()),
                            autoDrive.strafeTo(-14, 3, 11, 0.3, 0.7)
                        ),

                        /* Start the intake, move into the artifacts and index the artifacts as they come in */
                        intake.setVelocityAction(Constants.Intake.TARGET_VELOCITY),
                        new InstantAction(autoDrive::setStartingXPos),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        shooter.closeAction(),
                        new ParallelAction(
                                new SequentialAction(
                                        new InstantAction(() -> data.setArtifactDetection(true)),
                                        new InstantAction(() -> ballDetection.setProcessColor(true)),
                                        ballDetection.detectionAction(autoDrive.getDriveComplete()),
                                        spindexer.moveDistAction(2),
                                        new SleepAction(0.5),
                                        new InstantAction(() -> ballDetection.setProcessColor(true)),
                                        ballDetection.detectionAction(autoDrive.getDriveComplete()),
                                        spindexer.moveDistAction(2),
                                        new SleepAction(0.5),
                                        new InstantAction(() -> ballDetection.setProcessColor(true)),
                                        ballDetection.detectionAction(autoDrive.getDriveComplete())
                                ),
                                autoDrive.driveTo(-23, 0, 0, 0.1, 0.1)
                        ),

                        /* Strafe back towards the goal */
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingYPos),
                        new ParallelAction(
                                spindexer.sortAction(autoDrive.getDriveComplete()),
                                autoDrive.strafeTo(-2, 3, 11, 0.3, 0.7)
                        ),


                        new InstantAction(autoDrive::setStartingHeadingPos),
                        autoDrive.turnTo(357, 10, 15, 0.2, 0.7),

                        /* Shoot the artifacts into the goal. */
                        shooter.startAction(Constants.Shooter.TARGET_VELOCITY - 175),
                        spindexer.moveDistAction(1),
                        new SleepAction(1.2),
                        shooter.startAction(Constants.Shooter.TARGET_VELOCITY - 150),
                        spindexer.moveDistAction(2),
                        new SleepAction(1.2),
                        shooter.startAction(Constants.Shooter.TARGET_VELOCITY - 100),
                        spindexer.moveDistAction(2),
                        new SleepAction(1.2),
                        spindexer.moveDistAction(2),
                        new InstantAction(() -> intake.setVelocity(0)),
                        new SleepAction(1.2),
                        ballDetection.resetActualPattern(),
                        shooter.stopAction()
                )
        );

        if (isStopRequested()) {
            tagDetection.stopStreaming();
        }
    }
}