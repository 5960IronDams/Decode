package org.firstinspires.ftc.teamcode.ironDams.core.driveTrain;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.IGyro;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Imu;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;

public class GyroMecanumDrive
        extends FourWheelDrive
        implements IDriveTrain {

    private final Gamepad _gamepad1;
    private final IGyro _gyro;

    public GyroMecanumDrive(HardwareMap hardwareMap, Gamepad gamepad, boolean usePinpoint) {
        super(hardwareMap);

        _gyro = usePinpoint ? new Pinpoint(hardwareMap, new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0))
                : new Imu(hardwareMap);
        _gamepad1 = gamepad;
    }


    @Override
    public void init() { }

    @Override
    public void drive() {
        double zeroedYaw = _gyro.update();

        double x = _gamepad1.right_stick_x;
        double y = -_gamepad1.right_stick_y;
        double turn = -_gamepad1.left_stick_x;

        double theta = Math.atan2(y, x) * 180 / Math.PI; // aka angle

        double realTheta;

        realTheta = (360 - zeroedYaw) + theta;

        if (_gamepad1.left_trigger != 0) {
            realTheta = Math.atan2(y, x) * 180 / Math.PI;
        } else if ((_gamepad1.right_trigger != 0)) {
            theta = Math.atan2(y, x) * 180 / Math.PI;
            realTheta = (360 - zeroedYaw + theta) % 360;
        }
        double power = Math.hypot(x, y);

        double sin = Math.sin((realTheta * (Math.PI / 180)) - (Math.PI / 4));
        double cos = Math.cos((realTheta * (Math.PI / 180)) - (Math.PI / 4));
        double maxSinCos = Math.max(Math.abs(sin), Math.abs(cos));

        double leftFront = (power * cos / maxSinCos + turn);
        double rightFront = (power * sin / maxSinCos - turn);
        double leftBack = (power * sin / maxSinCos + turn);
        double rightBack = (power * cos / maxSinCos - turn);


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

        // Buttons
        if (_gamepad1.b) {
            _gyro.reset();
        }
    }
}
