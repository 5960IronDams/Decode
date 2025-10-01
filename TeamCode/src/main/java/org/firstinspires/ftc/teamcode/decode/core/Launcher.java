package org.firstinspires.ftc.teamcode.decode.core;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Launcher {
    private final Servo _servo;
    private final DcMotorEx _left;
    private final DcMotorEx _right;

    public Launcher(HardwareMap hardwareMap) {
        _servo = hardwareMap.get(Servo.class, "launcher");

        _left = hardwareMap.get(DcMotorEx.class, "leftOut");
        _right = hardwareMap.get(DcMotorEx.class, "rightOut");

        _right.setDirection(DcMotorEx.Direction.REVERSE);

        _left.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        _right.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        _left.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        _right.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        close();
    }

    public Launcher open() {
        double openPos = 0.5;
        _servo.setPosition(openPos);
        return this;
    }

    public Launcher close() {
        double closedPos = 1.0;
        _servo.setPosition(closedPos);
        return this;
    }

    public Launcher run(double power) {
        _left.setPower(power);
        _right.setPower(power);
        return this;
    }

    public Launcher stop() {
        _left.setPower(0);
        _right.setPower(0);
        return this;
    }
}