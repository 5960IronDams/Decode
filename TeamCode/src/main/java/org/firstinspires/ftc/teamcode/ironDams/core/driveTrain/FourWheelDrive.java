package org.firstinspires.ftc.teamcode.ironDams.core.driveTrain;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FourWheelDrive {
    final DcMotor _leftFrontDrive;
    final DcMotor _leftBackDrive;
    final DcMotor _rightFrontDrive;
    final DcMotor _rightBackDrive;

    FourWheelDrive(HardwareMap hardwareMap) {
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
        //TODO: Can we try to make all the motors brake?
    }
}
