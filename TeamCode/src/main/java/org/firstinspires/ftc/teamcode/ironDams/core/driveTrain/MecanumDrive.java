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

        double vertical = -powerY;
        double horizontal = powerX;
        double pivot = powerTurn;

        double flp = (pivot + vertical + horizontal);
        double frp = (-pivot + (vertical - horizontal));
        double rlp = (pivot + (vertical - horizontal));
        double rrp = (-pivot + vertical + horizontal);

        _leftBackDrive.setPower(rlp);
        _rightBackDrive.setPower(rrp);
        _leftFrontDrive.setPower(flp);
        _rightFrontDrive.setPower(frp);
    }
}
