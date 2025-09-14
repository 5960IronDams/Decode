package org.firstinspires.ftc.teamcode.ironDams.autonomus;

// RR-specific imports

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.AprilTagReader;

@Config
//@Autonomous(name = "AUTONOMOUS_CAMERA", group = "Autonomous")
@TeleOp(name = "AUTONOMOUS_CAMERA", group = "IronDams")
public class AutonomousCamera extends LinearOpMode {


//    private final Pose2d initPose = new Pose2d(0,0,0);
//    private final MecanumDrive drive = new MecanumDrive(hardwareMap, initPose);

    @Override
    public void runOpMode() {
        AprilTagReader aprilTagReader = new AprilTagReader(this, true);

        waitForStart();

//        TrajectoryActionBuilder builder = drive.actionBuilder(initPose);

        Actions.runBlocking(
            new SequentialAction(
                aprilTagReader.readTagAction(),
                // sort balls in intake. pass seq. # in as argument, aprilTagReader.getSequenceCode();
                // shoot balls into hoper.
                // drive to first pickup location.
                new ParallelAction(
                    // drive to shoot location
                    // sort balls in intake
                ),
                // shoot balls into hoper.
                // drive to second pickup location.
                new ParallelAction(
                    // drive to shoot location
                    // sort balls in intake
                ),
                // shoot balls into hoper.
                // drive to third pickup location.
                new ParallelAction(
                    // drive to shoot location
                    // sort balls in intake
                )
                // shoot balls into hoper.
            )
        );

//        telemetry.addData("Sequence Code", aprilTagReader.getSequenceCode());
//        telemetry.update();

        sleep(20000);
    }
}
