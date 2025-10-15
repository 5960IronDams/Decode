package org.firstinspires.ftc.teamcode.ironDams.core.driveTrain;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.decode.Constants;

public class GyroMecanumDrive extends FourWheelDrive
        implements IDriveTrain{
    private  LinearOpMode _opMode;
    private BNO055IMU imu;
    private BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
    private Orientation angles = new Orientation();

    private double initYaw;

    public GyroMecanumDrive(LinearOpMode opMode) {
        super(opMode.hardwareMap);
        _opMode = opMode;

        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;

        imu = opMode.hardwareMap.get(BNO055IMU.class, "imu2");

        initImu();
    }

    public void initImu() {
        imu.initialize(parameters);
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        initYaw = angles.firstAngle;
    }

    public void reset() {
        if (_opMode.gamepad1.b) {
            initImu();
            _opMode.sleep(Constants.WAIT_DURATION_MS);
        }
    }

    public void drive() {
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double zeroedYaw = -initYaw + angles.firstAngle;

        double x = _opMode.gamepad1.right_stick_x;
        double y = -_opMode.gamepad1.right_stick_y;
        double turn = _opMode.gamepad1.left_stick_x;

        double theta = Math.atan2(y, x) * 180 / Math.PI; // aka angle

        double realTheta;

        realTheta = (360 - zeroedYaw) + theta;
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

        reset();
    }
}