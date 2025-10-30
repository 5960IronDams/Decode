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
import org.firstinspires.ftc.teamcode.decode.player.Pattern;
import org.firstinspires.ftc.teamcode.ironDams.auto.AutoDrive;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.MecanumDriveTrain;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;

import java.util.concurrent.atomic.AtomicReference;

@Autonomous(name = "AUTO_RED_SHORT", group = "@@@@IronDams")
public class RED_SHORT_OpMode extends LinearOpMode {
    @Override
    public void runOpMode() {
        AtomicReference<Boolean> moveSpindexer = new AtomicReference<>(false);

        SharedData data = new SharedData();

        TagDetection tagDetection = new TagDetection(this, data);

        MecanumDriveTrain drive = new MecanumDriveTrain(this);
        Pinpoint pinpoint = new Pinpoint(this);
        AutoDrive autoDrive = new AutoDrive(drive, pinpoint);

        Shooter shooter = new Shooter(this, data);
        Pattern pattern = new Pattern(this, data);
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
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingXPos),
                        new InstantAction(() -> intake.setVelocity(800)),

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
                        autoDrive.driveTo(-14, 4, 10, 0.3, 1.0),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),

                        tagDetection.webcamReadAction(autoDrive.getDriveComplete()),

                        ballDetection.detectionAction(autoDrive.getDriveComplete()),
                        new ParallelAction(
                                spindexer.sortAction(autoDrive.getDriveComplete()),
                                autoDrive.driveTo(13, 4, 9, 0.3, 1.0)
                        ),
                        new InstantAction(tagDetection::stopStreaming),
//                        new SleepAction(0.5),

                        shooter.startAction(Constants.Shooter.TARGET_VELOCITY + 100),
                        spindexer.moveDistAction(1),
                        new SleepAction(1.2),
                        shooter.startAction(Constants.Shooter.TARGET_VELOCITY + 100),
                        spindexer.moveDistAction(2),
                        new SleepAction(1.2),
                        shooter.startAction(Constants.Shooter.TARGET_VELOCITY + 100),
                        spindexer.moveDistAction(2),
                        new SleepAction(1.2),

                        new InstantAction(() -> intake.setVelocity(0)),
                        ballDetection.resetActualPattern(),
                        shooter.stopAction(),

                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingXPos),
                        new ParallelAction(
                                spindexer.moveIndexAction(0),
                                tagDetection.webcamReadAction(autoDrive.getDriveComplete()),
                                autoDrive.driveTo(-28, 4, 10, 0.3, 0.7)
                        ),

                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingHeadingPos),
                        autoDrive.turnTo(333, 10, 15, 0.2, 0.7),

                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingYPos),
                        new ParallelAction(
//                            spindexer.indexAction(autoDrive.getDriveComplete()),
                            autoDrive.strafeTo(-15, 3, 11, 0.3, 0.7)
                        ),
                        intake.setVelocityAction(Constants.Intake.TARGET_VELOCITY),

                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingXPos),
                        new ParallelAction(
                                new SequentialAction(
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
                                autoDrive.driveTo(-28, 0, 0, 0.2, 0.2)
                        ),

                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingYPos),
                        new ParallelAction(
                                new SequentialAction(
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
                                autoDrive.strafeTo(15, 3, 11, 0.3, 0.7)
                        )
                )
        );

        if (isStopRequested()) {
            tagDetection.stopStreaming();
        }
    }
}