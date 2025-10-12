package org.firstinspires.ftc.teamcode.ironDams.core.driveTrain;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.IGyro;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Imu;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;

public class MecanumDrive
        extends FourWheelDrive
        implements IDriveTrain {

    public MecanumDrive(HardwareMap hardwareMap, Gamepad gamepad, boolean usePinpoint) {
        super(hardwareMap);
    }


    @Override
    public void init() { }

    @Override
    public void drive(double powerX, double powerY, double powerTurn) {

        double x = powerX;
        double y = powerY;
        double turn = powerTurn;
        double power = Math.hypot(x, y);


        double leftFront = (power + turn);
        double rightFront = (power - turn);
        double leftBack = (power + turn);
        double rightBack = (power - turn);


        if ((power + Math.abs(turn)) > 1) {
            leftFront /= power + turn;
            rightFront /= power - turn;
            leftBack /= power + turn;
            rightBack /= power - turn;
        }

        _leftFrontDrive.setPower(leftFront);
        _rightFrontDrive.setPower(rightFront);
        _leftBackDrive.setPower(leftBack);
        _rightBackDrive.setPower(rightBack);
    }
}
