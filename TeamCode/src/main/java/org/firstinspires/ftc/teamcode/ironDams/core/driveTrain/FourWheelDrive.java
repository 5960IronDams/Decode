package org.firstinspires.ftc.teamcode.ironDams.core.driveTrain;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FourWheelDrive {
    static DcMotor _leftFrontDrive;
    static DcMotor _leftBackDrive;
    static DcMotor _rightFrontDrive;
    static DcMotor _rightBackDrive;

    static void init(HardwareMap hardwareMap) {
        if (_leftFrontDrive != null) return;

        _leftFrontDrive = hardwareMap.get(DcMotor.class, "leftFront");
        _leftBackDrive = hardwareMap.get(DcMotor.class, "leftBack");
        _rightBackDrive = hardwareMap.get(DcMotor.class, "rightBack");
        _rightFrontDrive = hardwareMap.get(DcMotor.class, "rightFront");

        _leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        _leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        _rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        _rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        _leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        _leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        _rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        _rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }
}
