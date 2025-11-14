package org.firstinspires.ftc.teamcode.decode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.irondams.core.WaitFor;
import org.firstinspires.ftc.teamcode.irondams.core.odometry.Pinpoint;

@TeleOp(name = "PinpointOutput", group = "Test")
public class PinpointOutputOpMode extends LinearOpMode {
    @Override
    public void runOpMode() {
        Pinpoint pinpoint = new Pinpoint(this);

        telemetry.addLine("Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            Pose2D pos = pinpoint.getPose();

            double pinHeading = pos.getHeading(AngleUnit.DEGREES);
            double heading = pinHeading % 360;
            if (heading < 0) heading += 360;

            telemetry.addData("x", pos.getX(DistanceUnit.INCH));
            telemetry.addData("y", pos.getY(DistanceUnit.INCH));
            telemetry.addData("heading", pinHeading);
            telemetry.addData("normalized heading", heading);

            telemetry.update();
        }
    }
}
