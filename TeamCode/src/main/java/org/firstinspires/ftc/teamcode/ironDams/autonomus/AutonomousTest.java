package org.firstinspires.ftc.teamcode.ironDams.autonomus;

// RR-specific imports
import com.acmerobotics.dashboard.config.Config;

// Non-RR imports
import com.acmerobotics.roadrunner.Action;
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
        Pose2d initialPose = new Pose2d(0, 0, 0);//new Pose2d(11.8, 61.7, Math.toRadians(90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        waitForStart();

        TrajectoryActionBuilder tab1 = drive.actionBuilder(initialPose)
                .lineToY(4);

        Action action = tab1.build();
        Actions.runBlocking(
                new SequentialAction(action)
        );
    }

}
