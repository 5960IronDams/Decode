package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class Intake {
    private final DcMotorEx INTAKE_MOTOR;

    private final double TARGET_VELOCITY = 1000;

    private final double TPS = 2245;
    private final double TARGET_VOLT = 12;

    public boolean isActive = false;

    public Intake(LinearOpMode opMode) {

        INTAKE_MOTOR = opMode.hardwareMap.get(DcMotorEx.class, "intake");

        INTAKE_MOTOR.setDirection(DcMotorEx.Direction.REVERSE);
        INTAKE_MOTOR.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        INTAKE_MOTOR.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        INTAKE_MOTOR.setVelocityPIDFCoefficients(1.5031, 0.15031, 0, 15.031);
        INTAKE_MOTOR.setPositionPIDFCoefficients(5.0);
    }

    public void setTargetVelocity() {
        setVelocity(TARGET_VELOCITY);
    }

    public void setVelocity(double velocity) {
        INTAKE_MOTOR.setVelocity(velocity);
    }

    public Action setIntakeVelocityAction(double millis, double velocity) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                setVelocity(velocity);

                return false;
            }
        };
    }
}
