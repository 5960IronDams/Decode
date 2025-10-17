package org.firstinspires.ftc.teamcode.decode.autonomous;

// RR-specific imports

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.decode.core.Decoder;

@Config
//@Autonomous(name = "AUTONOMOUS_CAMERA", group = "Autonomous")
@TeleOp(name = "AUTONOMOUS_CAMERA", group = "IronDams")
public class AutonomousCamera extends LinearOpMode {


//    private final Pose2d initPose = new Pose2d(0,0,0);
//    private final MecanumDrive drive = new MecanumDrive(hardwareMap, initPose);

    @Override
    public void runOpMode() {
        Decoder decoder = new Decoder(this);

        waitForStart();

//        TrajectoryActionBuilder builder = drive.actionBuilder(initPose);

        Actions.runBlocking(
            new SequentialAction(
//                decoder.readTagAction(true),
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

    @Config
    @Autonomous(name = "AUTONOMOUS_TEST", group = "Autonomous")
    public static class AutonomousTest extends LinearOpMode {
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
}
