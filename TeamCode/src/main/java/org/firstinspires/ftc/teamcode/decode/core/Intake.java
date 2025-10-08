package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {

    private final DcMotorEx _motor;
    private final LinearOpMode _opMode;
    private boolean _runningIntake;

    public Intake(LinearOpMode opMode) {
        _opMode = opMode;
        _motor = opMode.hardwareMap.get(DcMotorEx.class, "intake");

        _motor.setDirection(DcMotorEx.Direction.REVERSE);
        _motor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        _motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
    }

    public boolean isRunning(){
        return _runningIntake;
    }

    private Intake toggleIntake(){
        if(_opMode.gamepad2.b) {
            _runningIntake = !_runningIntake;

            if (_runningIntake) setPower(1);
            else stop();
            _opMode.sleep(100);
        }

        return this;
    }
    public void setPower(double power) {
        _motor.setPower(power);
    }
    public Action manageIntake() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                toggleIntake();

                return true;
            }
        };
    }

    public void stop() {
        _motor.setPower(0);
    }
}
