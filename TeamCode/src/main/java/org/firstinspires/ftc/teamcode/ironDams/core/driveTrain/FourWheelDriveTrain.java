package org.firstinspires.ftc.teamcode.ironDams.core.driveTrain;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.ironDams.Config;

public final class FourWheelDriveTrain {
    private static DcMotorEx _leftFrontDrive;
    private static DcMotorEx _leftBackDrive;
    private static DcMotorEx _rightFrontDrive;
    private static DcMotorEx _rightBackDrive;

    static DcMotorEx getLeftFrontDrive() {
        return _leftFrontDrive;
    }

    static DcMotorEx getLeftBackDrive() {
        return _leftBackDrive;
    }

    static DcMotorEx getRightFrontDrive() {
        return _rightFrontDrive;
    }

    static DcMotorEx getRightBackDrive() {
        return _rightBackDrive;
    }


    static void init(HardwareMap hardwareMap) {
        if (_leftFrontDrive != null) return;

        _leftFrontDrive = hardwareMap.get(DcMotorEx.class, Config.Hardware.Motors.DriveTrain.LEFT_FRONT_MOTOR_ID);
        _leftBackDrive = hardwareMap.get(DcMotorEx.class, Config.Hardware.Motors.DriveTrain.LEFT_BACK_MOTOR_ID);
        _rightBackDrive = hardwareMap.get(DcMotorEx.class, Config.Hardware.Motors.DriveTrain.RIGHT_BACK_MOTOR_ID);
        _rightFrontDrive = hardwareMap.get(DcMotorEx.class, Config.Hardware.Motors.DriveTrain.RIGHT_FRONT_MOTOR_ID);

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
