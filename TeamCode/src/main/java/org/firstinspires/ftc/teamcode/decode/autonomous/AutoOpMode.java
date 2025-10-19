package org.firstinspires.ftc.teamcode.decode.autonomous;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.MecanumDrive;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.IGyro;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;

@TeleOp(name = "AutoOpMode", group = "_IronDams")
public class AutoOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive(this);
        Pinpoint pinpoint = new Pinpoint(this);

        waitForStart();

        while (opModeIsActive()) {
            Pose2D pos = pinpoint.getPose();
            telemetry.addData("x", pos.getX(DistanceUnit.INCH));
            telemetry.addData("y", pos.getY(DistanceUnit.INCH));
            telemetry.addData("heading", pos.getHeading(AngleUnit.DEGREES));

            telemetry.update();
        }

    }

    public Action updateTelemetry() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                telemetry.update();

                return true;
            }
        };
    }
}