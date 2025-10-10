package org.firstinspires.ftc.teamcode.decode.core;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {

    private final DcMotorEx _motor;

    public Intake(HardwareMap hardwareMap) {
        _motor = hardwareMap.get(DcMotorEx.class, "intake");

        _motor.setDirection(DcMotorEx.Direction.REVERSE);
        _motor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        _motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
    }

    public void run(double power) {
        _motor.setPower(power);
    }

    public void stop() {
        _motor.setPower(0);
    }
}
