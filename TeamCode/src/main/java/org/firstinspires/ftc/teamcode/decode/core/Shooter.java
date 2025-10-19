package org.firstinspires.ftc.teamcode.decode.core;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.decode.Constants;

public class Shooter {
    private final Servo _servo;
    private final DcMotorEx _left;
    private final DcMotorEx _right;
    private final LinearOpMode _opMode;

    private double _minLeftCurrent;
    private double _maxLeftCurrent;

    private double _minRightCurrent;
    private double _maxRightCurrent;

    public Shooter(LinearOpMode opMode) {
        _opMode = opMode;
        _servo = opMode.hardwareMap.get(Servo.class, Constants.Shooter.LAUNCHER_ID);
        _servo.setDirection(Servo.Direction.REVERSE);

        _left = opMode.hardwareMap.get(DcMotorEx.class, Constants.Shooter.MOTOR_LEFT_ID);
        _right = opMode.hardwareMap.get(DcMotorEx.class, Constants.Shooter.MOTOR_RIGHT_ID);

        _right.setDirection(DcMotorEx.Direction.REVERSE);

        _left.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        _right.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        _left.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        _right.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        close();
    }

    public Shooter open() {
        _servo.setPosition(Constants.Shooter.OPEN_POS);
        return this;
    }

    public Shooter close() {
        _servo.setPosition(Constants.Shooter.CLOSED_POS);
        return this;
    }

    public void stop() {
        _left.setPower(0);
        _right.setPower(0);
    }

    public void setPower(double power) {
        _left.setPower(power);
        _right.setPower(power);
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

    public boolean isInRange() {
        double leftCurrent = getLeftCurrent(CurrentUnit.MILLIAMPS);
        double rightCurrent = getRightCurrent(CurrentUnit.MILLIAMPS);

        return leftCurrent < Constants.Shooter.MAX_LEFT_CURRENT &&
                leftCurrent > Constants.Shooter.MIN_LEFT_CURRENT &&
                rightCurrent < Constants.Shooter.MAX_RIGHT_CURRENT &&
                rightCurrent > Constants.Shooter.MIN_RIGHT_CURRENT;


    }
}