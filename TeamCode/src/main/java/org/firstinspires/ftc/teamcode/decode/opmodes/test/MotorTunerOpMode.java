package org.firstinspires.ftc.teamcode.decode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.decode.core.BallDetection;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Launcher;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.irondams.core.Logger;
import org.firstinspires.ftc.teamcode.irondams.core.WaitFor;
import org.firstinspires.ftc.teamcode.irondams.core.driveTrain.MecanumDrive;

@TeleOp(name = "MotorTuner", group = "Test")
public class MotorTunerOpMode extends LinearOpMode {
    @Override
    public void runOpMode() {
        WaitFor userDelay = new WaitFor(500);

        String[] motorKeys = { "leftOut", "rightOut", "intake", "leftFront", "leftBack", "rightBack", "rightFront" };
        DcMotorEx[] motors = new DcMotorEx[motorKeys.length];

        int activeMotorIndex = 0;
        double maxVelocity = 0;

        for (int i = 0; i < motorKeys.length; i++) {
            motors[i] = hardwareMap.get(DcMotorEx.class, motorKeys[i]);
            motors[i].setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
            motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            if (motorKeys[i].equals("rightOut") || motorKeys[i].equals("intake") ||
                motorKeys[i].equals("rightFront") || motorKeys[i].equals("leftBack")) {
                motors[i].setDirection(DcMotorEx.Direction.REVERSE);
            }
        }

        telemetry.addLine("Ready");
        telemetry.update();

        double power = 0;

        while (opModeInInit()) {
            if (gamepad1.left_trigger != 0 && userDelay.allowExec()) {
                if (activeMotorIndex > 0) activeMotorIndex--;
            } else if (gamepad1.right_trigger != 0 && userDelay.allowExec()) {
                if (activeMotorIndex < motorKeys.length - 1) activeMotorIndex++;
            }

            telemetry.addData("Active motor", motorKeys[activeMotorIndex]);
            telemetry.update();
        }

        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            if (gamepad1.dpad_up) {
                power = 1;
            } else if (gamepad1.dpad_down) {
                power = 0;
            }

            motors[activeMotorIndex].setPower(power);

            if (power > 0) {
                double velocity = motors[activeMotorIndex].getVelocity();
                if (velocity > maxVelocity) maxVelocity = velocity;

                telemetry.addData("Active motor", motorKeys[activeMotorIndex]);
                telemetry.addData("velocity", velocity);
                telemetry.addData("maxVelocity", maxVelocity);
                telemetry.addData("power", motors[activeMotorIndex].getPower());
                telemetry.addData("current", motors[activeMotorIndex].getCurrent(CurrentUnit.MILLIAMPS));
            }

            telemetry.update();
        }
    }
}
