package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.decode.Constants;

public class Launcher {
    private final Servo _servo;
    private final DcMotorEx _left;
    private final DcMotorEx _right;
    private final LinearOpMode _opMode;

    private double _minLeftCurrent;
    private double _maxLeftCurrent;

    private double _minRightCurrent;
    private double _maxRightCurrent;

    public Launcher(LinearOpMode opMode) {
        _opMode = opMode;
        _servo = opMode.hardwareMap.get(Servo.class, Constants.Launcher.LAUNCHER_ID);
        _servo.setDirection(Servo.Direction.REVERSE);

        _left = opMode.hardwareMap.get(DcMotorEx.class, Constants.Launcher.MOTOR_LEFT_ID);
        _right = opMode.hardwareMap.get(DcMotorEx.class, Constants.Launcher.MOTOR_RIGHT_ID);

        _right.setDirection(DcMotorEx.Direction.REVERSE);

        _left.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        _right.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        _left.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        _right.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        close();
    }

    public Launcher open() {
        _servo.setPosition(Constants.Launcher.OPEN_POS);
        return this;
    }

    public Launcher close() {
        _servo.setPosition(Constants.Launcher.CLOSED_POS);
        return this;
    }

    public void stop() {
        _left.setPower(0);
        _right.setPower(0);
    }

    public void setPower() {
        _left.setPower(Constants.Launcher.MAX_POWER);
        _right.setPower(Constants.Launcher.MAX_POWER);
    }

    public double getPower() {
        return _left.getPower();
    }

    public double getLeftCurrent(CurrentUnit currentUnit) {
        return _left.getCurrent(currentUnit);
    }

    public double getRightCurrent(CurrentUnit currentUnit) {
        return _right.getCurrent(currentUnit);
    }

    public double getLeftVelocity() {
        return _left.getVelocity();
    }

    public double getRightVelocity() {
        return _right.getVelocity();
    }

    public boolean isInRange() {
        double leftCurrent = getLeftCurrent(CurrentUnit.MILLIAMPS);
        double rightCurrent = getRightCurrent(CurrentUnit.MILLIAMPS);

        return leftCurrent < Constants.Launcher.MAX_LEFT_CURRENT &&
                leftCurrent > Constants.Launcher.MIN_LEFT_CURRENT &&
                rightCurrent < Constants.Launcher.MAX_RIGHT_CURRENT &&
                rightCurrent > Constants.Launcher.MIN_RIGHT_CURRENT;


    }
}