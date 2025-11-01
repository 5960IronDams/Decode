package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.ironDams.Config;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

public class Intake {
    private final VoltageSensor VOLTAGE_SENSOR;
    private final Gamepad GAMEPAD2;
    private final DcMotorEx MOTOR;
    private final PIDFCoefficients PIDF;

    private Constants.Intake.Mode _mode = Constants.Intake.Mode.ACTIVE;
    public Constants.Intake.Mode getMode() {
        return _mode;
    }

    public void setMode(Constants.Intake.Mode mode) {
        _mode = mode;
    }

    private final WaitFor USER_DELAY = new WaitFor(Config.USER_DELAY_MS);

    public Intake(LinearOpMode opMode) {
        MOTOR = opMode.hardwareMap.get(DcMotorEx.class, Config.Hardware.Motors.Intake.INTAKE_MOTOR_ID);

        MOTOR.setDirection(DcMotorEx.Direction.REVERSE);
        MOTOR.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        MOTOR.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        VOLTAGE_SENSOR = opMode.hardwareMap.voltageSensor.iterator().next();

        PIDF = new PIDFCoefficients(
                0, 0, 0.0005, (32767 / Constants.Intake.TPS) * (Constants.Intake.TARGET_VOLT / VOLTAGE_SENSOR.getVoltage())
        );

        GAMEPAD2 = opMode.gamepad2;
    }

    public void setVelocity(double velocity) {
        MOTOR.setVelocity(velocity);
    }

    public void toggleIntakeVelocity() {
        if (GAMEPAD2.right_trigger != 0) {
            MOTOR.setVelocity(Constants.Intake.TARGET_VELOCITY);
            _mode = Constants.Intake.Mode.ACTIVE;
        } else if (GAMEPAD2.left_trigger != 0) {
            MOTOR.setVelocity(0);
            _mode = Constants.Intake.Mode.INACTIVE;
        }
    }

    public Action setVelocityAction(double velocity) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                MOTOR.setVelocity(velocity);

                return false;
            }
        };
    }

    public Action playerAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                toggleIntakeVelocity();

                packet.put("Intake Power", MOTOR.getPower());
                packet.put("Intake LT2", GAMEPAD2.left_trigger);
                packet.put("Intake RT2", GAMEPAD2.right_trigger);

                return true;
            }
        };
    }
}