package org.firstinspires.ftc.teamcode.irondams.core.driveTrain;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;


public final class FourWheelDriveTrain {
    private final DcMotorEx _leftFrontDrive;
    private final DcMotorEx _leftBackDrive;
    private final DcMotorEx _rightFrontDrive;
    private final DcMotorEx _rightBackDrive;

    public DcMotorEx getLeftFrontDrive() {
        return _leftFrontDrive;
    }

    public DcMotorEx getLeftBackDrive() {
        return _leftBackDrive;
    }

    public DcMotorEx getRightFrontDrive() {
        return _rightFrontDrive;
    }

    public DcMotorEx getRightBackDrive() {
        return _rightBackDrive;
    }


    public FourWheelDriveTrain (HardwareMap hardwareMap) {
        _leftFrontDrive = hardwareMap.get(DcMotorEx.class, "leftFront");
        _leftBackDrive = hardwareMap.get(DcMotorEx.class, "leftBack");
        _rightBackDrive = hardwareMap.get(DcMotorEx.class, "rightBack");
        _rightFrontDrive = hardwareMap.get(DcMotorEx.class, "rightFront");

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