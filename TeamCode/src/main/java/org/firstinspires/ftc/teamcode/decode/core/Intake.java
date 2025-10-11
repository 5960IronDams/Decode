package org.firstinspires.ftc.teamcode.decode.core;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.decode.Constants;

public class Intake {

    private final DcMotorEx _motor;

    public Intake(LinearOpMode opMode) {
        _motor = opMode.hardwareMap.get(DcMotorEx.class, Constants.Intake.INTAKE_ID);

        _motor.setDirection(DcMotorEx.Direction.REVERSE);
        _motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        _motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
    }

    public double getCurrent() {
        return _motor.getCurrent(CurrentUnit.MILLIAMPS);
    }

    public PIDFCoefficients GetPID() {
        return _motor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public Intake setPower(double power) {
        _motor.setPower(power);
        return this;
    }

    public Intake stop() {
        _motor.setPower(0);
        return this;
    }
}
