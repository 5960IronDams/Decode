package org.firstinspires.ftc.teamcode.decode.opmodes;

import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.decode.auto.TagDetection;
import org.firstinspires.ftc.teamcode.decode.core.BallDetection;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Shooter;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.ironDams.auto.AutoDrive;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.FourWheelDriveTrain;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.MecanumDriveTrain;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;

import java.util.concurrent.atomic.AtomicReference;

@Autonomous(name = "red_long_LEAVE", group = "@@@@IronDams")
public class long_leave_line_red_opmode extends LinearOpMode {
    @Override
    public void runOpMode() {
        AtomicReference<Boolean> moveSpindexer = new AtomicReference<>(false);

        SharedData data = new SharedData();

        TagDetection tagDetection = new TagDetection(this, data);

        FourWheelDriveTrain dt = new FourWheelDriveTrain(this.hardwareMap);
        MecanumDriveTrain drive = new MecanumDriveTrain(dt);
        Pinpoint pinpoint = new Pinpoint(this);
        AutoDrive autoDrive = new AutoDrive(drive, pinpoint);

        Shooter shooter = new Shooter(this, data);
        BallDetection ballDetection = new BallDetection(this, data);
        Spindexer spindexer = new Spindexer(this, data);
        Intake intake = new Intake(this);

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
                            autoDrive.strafeTo(24, 4, 10, 0.3, 0.5)
                        ),

                        tagDetection.webcamStopStreamingAction()

                )
        );

        if (isStopRequested()) {
            tagDetection.stopStreaming();
        }
    }
}