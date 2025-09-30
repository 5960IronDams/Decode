package org.firstinspires.ftc.teamcode.ironDams.teleOp;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.AprilTagReader;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.BallVisionReader;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;

@TeleOp(name = "TelemetyOpMode", group = "_IronDams")
public class TelemetyOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pinpoint _pinpoin = new Pinpoint(hardwareMap, new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));
        AprilTagReader _atr = new AprilTagReader(this, true);
        BallVisionReader _husky = new BallVisionReader(this, true);
        waitForStart();

        Actions.runBlocking(
            new ParallelAction(
                _pinpoin.pinpointTelemetry(),
                _atr.readTagAction(),
                _husky.readTagAction()
            )
        );
    }
}