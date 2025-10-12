package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

public class Launcher {
    private final Servo _servo;
    private final DcMotorEx _left;
    private final DcMotorEx _right;
    private final LinearOpMode _opMode;
    private boolean _shoot;

    public Launcher(LinearOpMode opMode) {
        _opMode = opMode;
        _servo = opMode.hardwareMap.get(Servo.class, "launcher");

        _left = opMode.hardwareMap.get(DcMotorEx.class, "leftOut");
        _right = opMode.hardwareMap.get(DcMotorEx.class, "rightOut");

        _left.setDirection(DcMotorEx.Direction.REVERSE);

        _left.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        _right.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        _left.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        _right.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        close();
    }

    public boolean isRunning(){
        return _shoot;
    }

    public Launcher open() {
        double openPos = 0.5;
        _servo.setPosition(openPos);
        return this;
    }
    public boolean shootingToggle(){
        if(_opMode.gamepad2.a){
            _shoot = !_shoot;
            if(_shoot){
                setPower(0.5);
                open();
            }else{
                stop();
                close();
            }
            _opMode.sleep(200);
        }
        return _shoot;
    }

    public Launcher close() {
        double closedPos = 1.0;
        _servo.setPosition(closedPos);
        return this;
    }

    public Launcher setPower(double power) {
        _left.setPower(power);
        _right.setPower(power);
        return this;
    }

    public Launcher stop() {
        _left.setPower(0);
        _right.setPower(0);
        return this;
    }
    public Action manageLauncher() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                shootingToggle();

                return true;
            }
        };
    }
}