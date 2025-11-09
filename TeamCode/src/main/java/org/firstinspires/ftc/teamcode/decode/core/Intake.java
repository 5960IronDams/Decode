package org.firstinspires.ftc.teamcode.decode.core;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class Intake {
    private final VoltageSensor VOLTAGE_SENSOR;
    private final DcMotorEx INTAKE_MOTOR;
    private final PIDFCoefficients PIDF;

    private final double TARGET_VELOCITY = 1000;

    private final double TPS = 2245;
    private final double TARGET_VOLT = 12;

    public boolean isActive = false;

    public Intake(LinearOpMode opMode, VoltageSensor voltageSensor) {

        INTAKE_MOTOR = opMode.hardwareMap.get(DcMotorEx.class, "intake");

        INTAKE_MOTOR.setDirection(DcMotorEx.Direction.REVERSE);
        INTAKE_MOTOR.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        INTAKE_MOTOR.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        VOLTAGE_SENSOR = voltageSensor; //opMode.hardwareMap.voltageSensor.iterator().next();

        PIDF = new PIDFCoefficients(
                0, 0, 0.0005, (32767 / TPS) * (TARGET_VOLT / VOLTAGE_SENSOR.getVoltage())
        );
    }

    public void setTargetVelocity() {
        setVelocity(TARGET_VELOCITY);
    }

    public void setVelocity(double velocity) {
        if (velocity > 0) PIDF.f = (32767 / TPS) * (TARGET_VOLT / VOLTAGE_SENSOR.getVoltage());
        INTAKE_MOTOR.setVelocity(velocity);
    }
}
