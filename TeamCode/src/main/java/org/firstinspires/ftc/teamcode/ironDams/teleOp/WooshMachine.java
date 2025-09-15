package org.firstinspires.ftc.teamcode.ironDams.teleOp;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class WooshMachine {
    private final BNO055IMU _imu;
    private final HardwareMap _hardwareMap ;
    private DcMotor _leftFrontDrive;
    private DcMotor _leftBackDrive;
    private DcMotor _rightFrontDrive;
    private DcMotor _rightBackDrive;
    Orientation angles = new Orientation();
    private final Gamepad _gamepad1;

    double initYaw;
    double adjustedYaw;

    public WooshMachine(LinearOpMode opMode){
        _hardwareMap = opMode.hardwareMap;
        _imu = _hardwareMap.get(BNO055IMU.class, "imu");
        _gamepad1 = opMode.gamepad1;
        initIMU();
        initMotors();
    }
    private void initIMU(){
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;

        _imu.initialize(parameters);
        angles = _imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        initYaw = angles.firstAngle;
    }
    private void initMotors(){
        _leftFrontDrive = _hardwareMap.get(DcMotor.class, "leftFront");
        _leftBackDrive = _hardwareMap.get(DcMotor.class, "leftBack");
        _rightBackDrive = _hardwareMap.get(DcMotor.class, "rightBack");
        _rightFrontDrive = _hardwareMap.get(DcMotor.class, "rightFront");
        _leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        _leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        _rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        _rightBackDrive.setDirection(DcMotor.Direction.REVERSE);


        _leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }
    public Action runDrive() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }
                angles = _imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

                adjustedYaw = angles.firstAngle - initYaw;

                double zeroedYaw = -initYaw + angles.firstAngle;

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
                    initYaw = _imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
                }
                return true;
            }
        };
    }
}