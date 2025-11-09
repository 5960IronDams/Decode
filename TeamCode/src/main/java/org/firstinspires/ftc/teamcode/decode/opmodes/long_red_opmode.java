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

@Autonomous(name = "long_red", group = "@@@@IronDams")
public class long_red_opmode extends LinearOpMode {
    @Override
    public void runOpMode() {
        AtomicReference<Boolean> moveSpindexer = new AtomicReference<>(false);

        SharedData data = new SharedData();

        Logger log = new Logger("long_red");

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

                        /* Strafe towards the Obelisk */
                        new ParallelAction(
                                tagDetection.webcamReadAction(autoDrive.getDriveComplete()),
                                autoDrive.strafeTo(124, 4, 10, 0.3, 0.5)
                        ),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingXPos),
                        tagDetection.webcamStopStreamingAction(),

                        new ParallelAction(
                                spindexer.sortAction(data.getHasPatternChanged()),
                                new SleepAction(9)
                        ),

                        autoDrive.driveTo(16, 4, 10, 0.3, 0.7),

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
                        shooter.closeAction()
                )
        );

        if (isStopRequested()) {
            tagDetection.stopStreaming();
        }
    }
}