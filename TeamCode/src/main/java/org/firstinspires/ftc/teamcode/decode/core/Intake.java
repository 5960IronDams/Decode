package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {

    private final DcMotorEx _motor;

    public Intake(HardwareMap hardwareMap) {
        _motor = hardwareMap.get(DcMotorEx.class, "intake");

        _motor.setDirection(DcMotorEx.Direction.REVERSE);
        _motor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        _motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
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

                setPower(0.15);

                return true;
            }
        };
    }

    public void stop() {
        _motor.setPower(0);
    }
}
