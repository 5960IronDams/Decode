package org.firstinspires.ftc.teamcode.ironDams.core.driveTrain;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.IGyro;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Imu;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;

public class MecanumDrive
        implements IDriveTrain {
    public MecanumDrive(LinearOpMode opMode)  {
        FourWheelDrive.init(opMode.hardwareMap);
    }

    public void init() { }

    @Override
    public void drive(double horizontal, double vertical, double pivot) {
        double flp = (pivot + vertical + horizontal);
        double frp = (-pivot + (vertical - horizontal));
        double rlp = (pivot + (vertical - horizontal));
        double rrp = (-pivot + vertical + horizontal);

        FourWheelDrive._leftBackDrive.setPower(rlp);
        FourWheelDrive._rightBackDrive.setPower(rrp);
        FourWheelDrive._leftFrontDrive.setPower(flp);
        FourWheelDrive._rightFrontDrive.setPower(frp);
    }
}