package org.firstinspires.ftc.teamcode.ironDams.teleOp;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.decode.teleOp.WooshMachine;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.IGyro;


import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;

@TeleOp(name = "PlayerOpModeTest", group = "_IronDams")
public class PlayerOpModeTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
         WooshMachine _drive = new WooshMachine(this, true);
        IGyro pinpoint = new Pinpoint(hardwareMap, new Pose2D(DistanceUnit.INCH, 0 , 0, AngleUnit.DEGREES, 0));
        waitForStart();
        while (opModeIsActive()){
            _drive.go();

            Pose2D pos = pinpoint.getPose();
            telemetry.addData("x", pos.getX(DistanceUnit.INCH));
            telemetry.addData("y", pos.getY(DistanceUnit.INCH));
            telemetry.addData("heading", pos.getHeading(AngleUnit.DEGREES));
        }
//        Actions.runBlocking(
//            new ParallelAction(
//                _drive.runDrive()
//            )
//        );
    }
}