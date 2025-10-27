package org.firstinspires.ftc.teamcode.decode.teleOp;

import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.core.GreenBallPosition;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TeleOp(name = "VelocityTuner", group = "@@@@IronDams")
public class VelocityTunerOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        DcMotorEx dmfl  = hardwareMap.get(DcMotorEx.class, "leftFront");
        DcMotorEx dmbl = hardwareMap.get(DcMotorEx.class, "leftBack");
        DcMotorEx dmbr = hardwareMap.get(DcMotorEx.class, "rightBack");
        DcMotorEx dmfr = hardwareMap.get(DcMotorEx.class, "rightFront");

        DcMotorEx mls = hardwareMap.get(DcMotorEx.class, Constants.Shooter.MOTOR_LEFT_ID);
        DcMotorEx mrs = hardwareMap.get(DcMotorEx.class, Constants.Shooter.MOTOR_RIGHT_ID);

        DcMotorEx mi = hardwareMap.get(DcMotorEx.class, Constants.Intake.INTAKE_ID);

        Map<String, DcMotorEx> motors = new HashMap<>();
        motors.put("Left Front Drive", dmfl);
        motors.put("Left Back Drive", dmbl);
        motors.put("Right Back Drive", dmbr);
        motors.put("Right Front Drive", dmfr);
        motors.put("Left Shooter", mls);
        motors.put("Right Shooter", mrs);
        motors.put("Intake", mi);

        int activeMotorIndex = 0;
        DcMotorEx activeMotor = motors.get(activeMotorIndex);

        List<Map.Entry<String, DcMotorEx>> entries = new ArrayList<>(motors.entrySet());
        activeMotor = entries.get(activeMotorIndex).getValue();

        dmfl.setDirection(DcMotor.Direction.FORWARD);
        dmbl.setDirection(DcMotor.Direction.FORWARD);
        dmbr.setDirection(DcMotor.Direction.REVERSE);
        dmfr.setDirection(DcMotor.Direction.REVERSE);
        mi.setDirection(DcMotorEx.Direction.REVERSE);

        for (Map.Entry<String, DcMotorEx> motor : motors.entrySet()) {
            motor.getValue().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.getValue().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.getValue().setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        dmfl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dmbl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dmbr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dmfr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        while(!isStopRequested() && !opModeIsActive()) { }

        if (isStopRequested()) {
            return;
        }

        waitForStart();

        if (isStopRequested()) {
            return;
        }

        while (opModeIsActive()) {

            if (gamepad1.left_trigger != 0 && activeMotorIndex > 0)
            {
                activeMotorIndex--;
                activeMotor = entries.get(activeMotorIndex).getValue();
                sleep(250);
            } else if (gamepad1.right_trigger != 0 && activeMotorIndex < (entries.size() - 1)) {
                activeMotorIndex++;
                activeMotor = entries.get(activeMotorIndex).getValue();
                sleep(250);
            }

            activeMotor.setPower(gamepad1.left_stick_y);

            telemetry.addData("Active Motor", entries.get(activeMotorIndex).getKey());
            telemetry.addData("Current Position", activeMotor.getCurrentPosition());
            telemetry.addData("Velocity", activeMotor.getVelocity());
            telemetry.update();

        }
    }
}