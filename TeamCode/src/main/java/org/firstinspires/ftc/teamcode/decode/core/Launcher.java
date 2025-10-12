package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.decode.Constants;

public class Launcher {
    private final Servo _servo;
    private final DcMotorEx _left;
    private final DcMotorEx _right;
    private final LinearOpMode _opMode;

    public Launcher(LinearOpMode opMode) {
        _opMode = opMode;
        _servo = opMode.hardwareMap.get(Servo.class, Constants.Launcher.LAUNCHER_ID);
        _servo.setDirection(Servo.Direction.REVERSE);

        _left = opMode.hardwareMap.get(DcMotorEx.class, Constants.Launcher.MOTOR_LEFT_ID);
        _right = opMode.hardwareMap.get(DcMotorEx.class, Constants.Launcher.MOTOR_RIGHT_ID);

        _left.setDirection(DcMotorEx.Direction.REVERSE);

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

    public Action runAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                _servo.setPosition(Math.abs(_opMode.gamepad2.left_stick_y));
                packet.put("pos", _servo.getPosition());

                return true;
            }
        };
    }
}