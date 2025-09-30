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
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.IGyro;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;
import org.opencv.core.Mat;

@Config
@Autonomous(name = "AUTONOMOUS_TEST", group = "Autonomous")
public class AutonomousTest extends LinearOpMode {
    int visionOutputPosition = 1;

    @Override
    public void runOpMode() {
        // Define the starting pose of the robot
        Pose2d initialPose = new Pose2d(0, 0, 0);//To Do - Find pos x and y
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

 //       IGyro pinpoint = new Pinpoint(hardwareMap, new Pose2D(DistanceUnit.INCH, 0,0,
 //               AngleUnit.DEGREES, 0));
        telemetry.addLine("Ready to start");
        telemetry.update();

        waitForStart();
//        while (opModeIsActive()){
//
//        Pose2D pos = pinpoint.getPose();
//
//        telemetry.addData("X",pos.getX(DistanceUnit.INCH));
//        telemetry.addData("Y",pos.getY(DistanceUnit.INCH));
//        telemetry.addData("Z",pos.getHeading(AngleUnit.DEGREES));
//        telemetry.update();
//        }

        Action FirstSwoop = drive.actionBuilder(initialPose)
                .lineToX(20)
                //.splineTo(new Vector2d(48, 48), Math.PI / 2)
                .build();

        Actions.runBlocking(
                new SequentialAction(FirstSwoop)
        );
    }
}

