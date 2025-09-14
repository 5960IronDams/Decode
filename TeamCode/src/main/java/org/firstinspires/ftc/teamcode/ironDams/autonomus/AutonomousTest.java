package org.firstinspires.ftc.teamcode.ironDams.autonomus;

// RR-specific imports
import com.acmerobotics.dashboard.config.Config;

// Non-RR imports
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.NullAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.RaceAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Config
@Autonomous(name = "AUTONOMOUS_TEST", group = "Autonomous")
public class AutonomousTest extends LinearOpMode {
    int visionOutputPosition = 1;

    @Override
    public void runOpMode() {
        // Define the starting pose of the robot
        Pose2d initialPose = new Pose2d(0, 0, 0);
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        telemetry.addLine("Ready to start");
        telemetry.update();

        waitForStart();
//
//        Action moveForward = drive.actionBuilder(initialPose)
//                .lineToX(36)
//                .turn(Math.toRadians(9000))
//                .build();

        Actions.runBlocking(
                new ParallelAction(
                        drive.actionBuilder(new Pose2d(0, 0, 0))
                                .lineToX(-66)
                                .build(),
                        drive.actionBuilder(new Pose2d(0, 0, 0))
                                .turn(Math.toRadians(360)) // 25 full spins!
                                .build()
                )
        );
    }
}

